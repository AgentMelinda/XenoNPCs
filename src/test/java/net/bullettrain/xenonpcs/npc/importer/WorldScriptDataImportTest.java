package net.bullettrain.xenonpcs.npc.importer;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class WorldScriptDataImportTest {
    @TempDir Path world;

    @Test
    void sourceRootMapMergesOnlyMissingBoundedValues() {
        CompoundTag source = new CompoundTag();
        source.putString("conflict", "source");
        source.putString("new", "carried");
        source.putInt("number", 5);
        source.putDouble("infinity", Double.POSITIVE_INFINITY);
        source.putString("large", "x".repeat(1025));
        source.put("nested", new CompoundTag());
        CompoundTag existing = new CompoundTag();
        existing.putString("conflict", "native");
        var report = new NpcImportReport();
        CompoundTag merged = WorldScriptDataImport.merge(source, existing, "mynpcs", report);
        assertEquals("native", merged.getString("conflict"));
        assertEquals("carried", merged.getString("new"));
        assertEquals(5.0, merged.getDouble("number"));
        assertFalse(merged.contains("infinity"));
        assertFalse(merged.contains("large"));
        assertFalse(merged.contains("nested"));
        assertEquals(1, existing.size());
        assertEquals(4, report.notes().size());
        assertEquals(merged, WorldScriptDataImport.merge(source, merged, "mynpcs", new NpcImportReport()));
    }

    @Test
    void backupPrecedesWritePreviewIsInertAndRepeatDoesNotWrite() throws Exception {
        Path source = world.resolve("mynpcs/scripts/world_data.json");
        Files.createDirectories(source.getParent());
        Files.writeString(source, "{score:12d,owner:\"source\"}");
        var state = new AtomicReference<>(new CompoundTag());
        var writes = new AtomicInteger();
        java.util.function.Consumer<CompoundTag> write = merged -> {
            Path backup = world.resolve("XenoNpcs/import-backups/worlddata/mynpcs");
            assertTrue(Files.isDirectory(backup), "backup exists before live mutation");
            try (var paths = Files.walk(backup)) {
                assertEquals(2, paths.filter(Files::isRegularFile).count());
            } catch (Exception e) { throw new AssertionError(e); }
            state.set(merged);
            writes.incrementAndGet();
        };
        var preview = new NpcImportReport();
        WorldScriptDataImport.importRoots(world, world, state.get(), write, true, preview);
        assertEquals(1, preview.imported());
        assertEquals(0, writes.get());
        assertFalse(Files.exists(world.resolve("XenoNpcs/import-backups")));
        var actual = new NpcImportReport();
        WorldScriptDataImport.importRoots(world, world, state.get(), write, false, actual);
        assertEquals(1, actual.imported());
        assertTrue(actual.failures().isEmpty(), actual.failures().toString());
        assertEquals(1, writes.get());
        assertEquals(12, state.get().getDouble("score"));
        WorldScriptDataImport.importRoots(world, world, state.get(), write, false, new NpcImportReport());
        assertEquals(1, writes.get());
        assertEquals("{score:12d,owner:\"source\"}", Files.readString(source));
    }

    @Test
    void failedBackupDoesNotInvokeNativeWriter() throws Exception {
        Path source = world.resolve("mynpcs/scripts/world_data.json");
        Files.createDirectories(source.getParent());
        Files.writeString(source, "{new:\"value\"}");
        Files.createDirectories(world.resolve("XenoNpcs"));
        Files.writeString(world.resolve("XenoNpcs/import-backups"), "blocked");
        var report = new NpcImportReport();
        WorldScriptDataImport.importRoots(world, world, new CompoundTag(),
                merged -> fail("writer must not run before backup succeeds"), false, report);
        assertEquals(0, report.imported());
        assertEquals(1, report.failures().size());
    }
}
