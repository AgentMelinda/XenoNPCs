package net.bullettrain.xenonpcs.dmz.race;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/** Bounded race snapshots; paths are confined to one race, and links are never followed. */
public final class MakerRaceTransfer {
    public static final int MAX_BYTES = 2 * 1024 * 1024;
    public static final int MAX_FILES = 256;
    private MakerRaceTransfer() {}

    public static byte[] snapshot(Path raceDirectory) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int total = 0, count = 0;
        try (ZipOutputStream zip = new ZipOutputStream(bytes); var paths = Files.walk(raceDirectory)) {
            for (Path file : paths.filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)).sorted().toList()) {
                String relative = raceDirectory.relativize(file).toString().replace('\\', '/');
                if (!supported(relative)) continue;
                if (++count > MAX_FILES || Files.size(file) > MAX_BYTES - total) throw new IOException("Race pack exceeds snapshot limits");
                byte[] content;
                try (var input = Files.newInputStream(file)) { content = input.readNBytes(MAX_BYTES - total + 1); }
                total += content.length;
                if (total > MAX_BYTES) throw new IOException("Race pack exceeds snapshot limits");
                ZipEntry entry = new ZipEntry(relative); entry.setTime(0);
                zip.putNextEntry(entry); zip.write(content); zip.closeEntry();
            }
        }
        if (bytes.size() > MAX_BYTES) throw new IOException("Race snapshot is too large");
        return bytes.toByteArray();
    }

    public static Map<String, byte[]> decode(byte[] archive) throws IOException {
        if (archive == null || archive.length > MAX_BYTES) throw new IOException("Invalid race snapshot size");
        Map<String, byte[]> files = new LinkedHashMap<>();
        int total = 0;
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(archive))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory() || !supported(name) || files.containsKey(name)) throw new IOException("Invalid race file path");
                if (files.size() >= MAX_FILES) throw new IOException("Too many race files");
                ByteArrayOutputStream content = new ByteArrayOutputStream();
                int read;
                while ((read = zip.read(buffer)) != -1) {
                    total += read;
                    if (total > MAX_BYTES) throw new IOException("Expanded race snapshot is too large");
                    content.write(buffer, 0, read);
                }
                files.put(name, content.toByteArray());
            }
        }
        return files;
    }

    public static String revision(byte[] snapshot) {
        try { return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(snapshot)); }
        catch (java.security.NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }

    public static void install(Path directory, byte[] archive) throws IOException {
        Map<String, byte[]> files = decode(archive);
        Path root = directory.toAbsolutePath().normalize();
        Map<Path, byte[]> old = new LinkedHashMap<>();
        // Preflight every path before writing, including ancestors above the supplied root.
        for (var entry : files.entrySet()) {
            Path file = root.resolve(entry.getKey()).normalize();
            if (!file.startsWith(root)) throw new IOException("Race path escapes its root");
            for (Path ancestor = file; ancestor != null; ancestor = ancestor.getParent()) {
                if (Files.isSymbolicLink(ancestor)) throw new IOException("Race paths may not contain symbolic links");
            }
            if (Files.exists(file) && !Files.isRegularFile(file)) throw new IOException("Race file is not a regular file");
            if (Files.exists(file) && Files.size(file) > MAX_BYTES) throw new IOException("Existing race file is too large");
            old.put(file, Files.exists(file) ? Files.readAllBytes(file) : null);
        }
        List<Path> written = new ArrayList<>();
        try {
            for (var entry : files.entrySet()) {
                Path file = root.resolve(entry.getKey());
                writeAtomic(file, entry.getValue()); written.add(file);
            }
        } catch (IOException failure) {
            Collections.reverse(written);
            for (Path file : written) {
                try {
                    if (old.get(file) == null) Files.deleteIfExists(file); // Only a file created by this operation.
                    else writeAtomic(file, old.get(file));
                } catch (IOException rollback) { failure.addSuppressed(rollback); }
            }
            throw failure;
        }
    }

    private static void writeAtomic(Path file, byte[] data) throws IOException {
        Files.createDirectories(file.getParent());
        Path pending = Files.createTempFile(file.getParent(), ".maker-", ".tmp");
        try {
            Files.write(pending, data);
            try { Files.move(pending, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ignored) { Files.move(pending, file, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(pending); }
    }

    private static boolean supported(String name) {
        if (name == null || name.length() > 256 || name.contains("\\") || name.startsWith("/") || name.contains(":") || name.contains("..")) return false;
        if (!name.matches("[a-zA-Z0-9_./-]+")) return false;
        return name.endsWith(".json") || (name.startsWith("catalog/") && name.endsWith(".png"));
    }
}
