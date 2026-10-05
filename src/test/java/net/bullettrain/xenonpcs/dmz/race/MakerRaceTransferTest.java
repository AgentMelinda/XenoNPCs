package net.bullettrain.xenonpcs.dmz.race;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.io.*;
import java.util.zip.*;
import static org.junit.jupiter.api.Assertions.*;
class MakerRaceTransferTest {
    @TempDir Path root;
    private static byte[] zip(String name, byte[] data) throws Exception {
        var out = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(out)) { zip.putNextEntry(new ZipEntry(name)); zip.write(data); zip.closeEntry(); }
        return out.toByteArray();
    }
    @Test void rejectsTraversalBeforeWriting() throws Exception {
        assertThrows(IOException.class, () -> MakerRaceTransfer.install(root, zip("../character.json", new byte[0])));
        assertEquals(0, Files.list(root).count());
    }
    @Test void rejectsExpandedBomb() throws Exception {
        var bomb = zip("character.json", new byte[MakerRaceTransfer.MAX_BYTES + 1]);
        assertTrue(bomb.length < MakerRaceTransfer.MAX_BYTES);
        assertThrows(IOException.class, () -> MakerRaceTransfer.decode(bomb));
    }
    @Test void snapshotRevisionStableAndInstallationAdditive() throws Exception {
        Path source = root.resolve("source"); Files.createDirectories(source);
        Files.writeString(source.resolve("character.json"), "{}");
        var first = MakerRaceTransfer.snapshot(source);
        assertArrayEquals(first, MakerRaceTransfer.snapshot(source));
        Path target = root.resolve("target"); Files.createDirectories(target);
        Files.writeString(target.resolve("owner.txt"), "preserve");
        MakerRaceTransfer.install(target, first);
        assertEquals("{}", Files.readString(target.resolve("character.json")));
        assertEquals("preserve", Files.readString(target.resolve("owner.txt")));
        Files.writeString(source.resolve("character.json"), "{\"changed\":true}");
        assertNotEquals(MakerRaceTransfer.revision(first), MakerRaceTransfer.revision(MakerRaceTransfer.snapshot(source)));
    }
    @Test void rejectsAllPathsBeforeOverwritingEarlierFiles() throws Exception {
        Files.writeString(root.resolve("character.json"), "original"); Files.createDirectory(root.resolve("bad.json"));
        var out = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(out)) {
            zip.putNextEntry(new ZipEntry("character.json")); zip.write("changed".getBytes()); zip.closeEntry();
            zip.putNextEntry(new ZipEntry("bad.json")); zip.write(new byte[0]); zip.closeEntry();
        }
        assertThrows(IOException.class, () -> MakerRaceTransfer.install(root, out.toByteArray()));
        assertEquals("original", Files.readString(root.resolve("character.json")));
    }
}
