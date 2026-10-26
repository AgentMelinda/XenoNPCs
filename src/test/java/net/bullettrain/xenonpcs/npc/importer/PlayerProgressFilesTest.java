package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.npc.store.XenoNpcStoreCategory;
import net.bullettrain.xenonpcs.npc.store.XenoNpcWorldStore;
import net.minecraft.nbt.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerProgressFilesTest {
    @TempDir Path work;
    private final UUID uuid = UUID.fromString("12345678-1234-1234-1234-123456789abc");

    @Test
    void previewDoesNotWriteAndImportBacksUpExactFilesBeforeIdempotentMerge() throws Exception {
        Path world = work.resolve("world");
        Path source = source(world, uuid);
        Path destination = destination(world);
        byte[] sourceBefore = Files.readAllBytes(source);
        byte[] vanillaBefore = Files.readAllBytes(destination);
        var store = store(world);
        var preview = PlayerProgressFiles.migrateOffline(world, work, store, uuid, true);
        assertEquals(1, preview.imported());
        assertArrayEquals(vanillaBefore, Files.readAllBytes(destination));
        assertFalse(Files.exists(world.resolve("XenoNpcs/import-backups")));
        var report = PlayerProgressFiles.migrateOffline(world, work, store, uuid, false);
        assertTrue(report.failures().isEmpty(), report.failures().toString());
        assertEquals(1, report.imported());
        CompoundTag player = NbtIo.readCompressed(destination, NbtAccounter.unlimitedHeap());
        assertEquals("keep vanilla", player.getString("Marker"));
        assertEquals("keep foreign", player.getCompound("neoforge:attachments").getString("foreign:attachment"));
        assertEquals(55, player.getCompound("neoforge:attachments").getCompound("xenonpcs:xeno_data")
                .getCompound("FactionStanding").getInt("friendly"));
        Path backupRoot = world.resolve("XenoNpcs/import-backups/playerdata/mynpcs/" + uuid);
        Path backup;
        try (var stream = Files.list(backupRoot)) { backup = stream.findFirst().orElseThrow(); }
        assertArrayEquals(sourceBefore, Files.readAllBytes(backup.resolve("source.json")));
        assertArrayEquals(vanillaBefore, Files.readAllBytes(backup.resolve("player-before.dat")));
        assertTrue(Files.isRegularFile(backup.resolve("xeno-before.snbt")));
        byte[] firstImport = Files.readAllBytes(destination);
        var repeat = PlayerProgressFiles.migrateOffline(world, work, store, uuid, false);
        assertEquals(0, repeat.imported());
        assertArrayEquals(firstImport, Files.readAllBytes(destination));
        try (var stream = Files.list(backupRoot)) { assertEquals(1, stream.count()); }
        try (var stream = Files.list(destination.getParent())) {
            assertTrue(stream.noneMatch(p -> p.toString().endsWith(".tmp")));
        }
        assertArrayEquals(sourceBefore, Files.readAllBytes(source));
    }

    @Test
    void backupFailureLeavesVanillaSaveUntouched() throws Exception {
        Path world = work.resolve("world");
        source(world, uuid);
        Path destination = destination(world);
        byte[] before = Files.readAllBytes(destination);
        var store = store(world);
        Files.writeString(world.resolve("XenoNpcs/import-backups"), "blocks directory creation");
        var report = PlayerProgressFiles.migrateOffline(world, work, store, uuid, false);
        assertEquals(0, report.imported());
        assertEquals(1, report.failures().size());
        assertArrayEquals(before, Files.readAllBytes(destination));
    }

    @Test
    void mismatchedUuidAndMissingVanillaSaveRefuseWithoutBackup() throws Exception {
        Path world = work.resolve("world");
        Path source = source(world, UUID.randomUUID());
        var store = store(world);
        var mismatch = PlayerProgressFiles.migrateOffline(world, work, store, uuid, false);
        assertTrue(mismatch.failures().getFirst().reason().contains("UUID"));
        source(world, uuid);
        var noSave = PlayerProgressFiles.migrateOffline(world, work, store, uuid, false);
        assertTrue(noSave.failures().getFirst().reason().contains("no vanilla player save"));
        assertFalse(Files.exists(world.resolve("XenoNpcs/import-backups")));
        assertTrue(Files.isRegularFile(source));
    }

    private Path source(Path world, UUID storedUuid) throws Exception {
        Path file = world.resolve("mynpcs/playerdata/" + uuid + ".json");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "{UUID:\"" + storedUuid + "\",FactionData:[{Faction:1,Points:55}]}");
        return file;
    }

    private Path destination(Path world) throws Exception {
        Path file = world.resolve("playerdata/" + uuid + ".dat");
        Files.createDirectories(file.getParent());
        CompoundTag player = new CompoundTag();
        player.putString("Marker", "keep vanilla");
        CompoundTag attachments = new CompoundTag();
        attachments.putString("foreign:attachment", "keep foreign");
        player.put("neoforge:attachments", attachments);
        NbtIo.writeCompressed(player, file);
        return file;
    }

    private XenoNpcWorldStore store(Path world) {
        var store = new XenoNpcWorldStore(world.resolve("XenoNpcs"));
        CompoundTag faction = new CompoundTag();
        faction.putString("Name", "Friendly");
        faction.putString("SourceMod", "mynpcs");
        faction.putInt("SourceSlot", 1);
        assertNull(store.put(XenoNpcStoreCategory.FACTIONS, "", "friendly", faction));
        return store;
    }
}
