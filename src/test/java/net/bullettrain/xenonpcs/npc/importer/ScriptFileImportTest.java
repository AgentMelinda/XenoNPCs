package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.npc.store.XenoNpcStoreCategory;
import net.bullettrain.xenonpcs.npc.store.XenoNpcWorldStore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScriptFileImportTest {
    @TempDir Path dir;

    @Test
    void referenceOnlyNpcTabBindsLibraryAndReimportKeepsTheBinding() throws Exception {
        Path root = dir.resolve("mynpcs");
        write(root, "scripts/ecmascript/lib.js", "function init(e) { log('library'); }");
        var store = new XenoNpcWorldStore(dir.resolve("store"));
        ScriptFileImport.importRoot(new ForeignNpcRoots.Root("mynpcs", root), store, false, new NpcImportReport());
        CompoundTag source = source("", "lib.js");
        var imported = NpcScriptImport.importContainer(source, "mynpcs", "npc", store, new NpcImportReport());
        assertEquals(1, imported.tabs().size());
        assertEquals(List.of(ScriptFileImport.id("mynpcs", "ecmascript/lib.js")), imported.tabs().getFirst().loaded());
        assertTrue(imported.enabled());
        var repeatReport = new NpcImportReport();
        var repeat = NpcScriptImport.importContainer(source, "mynpcs", "npc", store, repeatReport);
        assertEquals(imported.tabs(), repeat.tabs());
        assertEquals(0, repeatReport.imported());
        assertTrue(repeatReport.failures().isEmpty());
    }

    @Test
    void globalsLoadLibrariesBeforeInlineAndPreserveDisabledAndSourceTabs() throws Exception {
        Path root = dir.resolve("mynpcs");
        write(root, "scripts/ecmascript/lib.js", "var shared = 5;");
        CompoundTag raw = source("function login(e) { log(shared); }", "lib.js");
        raw.putBoolean("ScriptEnabled", false);
        write(root, "scripts/player_scripts.json", raw.toString());
        write(root, "scripts/forge_scripts.json", source("", "lib.js").toString());
        var store = new XenoNpcWorldStore(dir.resolve("store"));
        var preview = new NpcImportReport();
        var foreignRoot = new ForeignNpcRoots.Root("mynpcs", root);
        ScriptFileImport.importRoot(foreignRoot, store, true, preview);
        assertEquals(3, preview.imported());
        assertTrue(store.list(XenoNpcStoreCategory.SCRIPTS).isEmpty());
        assertTrue(store.list(XenoNpcStoreCategory.PLAYER_SCRIPTS).isEmpty());
        var report = new NpcImportReport();
        ScriptFileImport.importRoot(foreignRoot, store, false, report);
        assertEquals(3, report.imported());
        var player = store.list(XenoNpcStoreCategory.PLAYER_SCRIPTS).getFirst().tag();
        assertEquals("function login(e) { log(shared); }", player.getString("Script"));
        assertEquals("var shared = 5;\nfunction login(e) { log(shared); }",
                net.bullettrain.xenonpcs.npc.store.XenoNpcScripts.composeSource(player,
                        id -> store.get(XenoNpcStoreCategory.SCRIPTS, "", id).getString("Script")));
        // Shared edits are read on rebuild, rather than trapped in an import-time snapshot.
        assertEquals("var shared = 9;\nfunction login(e) { log(shared); }",
                net.bullettrain.xenonpcs.npc.store.XenoNpcScripts.composeSource(player, id -> "var shared = 9;"));
        assertNull(net.bullettrain.xenonpcs.npc.store.XenoNpcScripts.composeSource(player, id -> null));
        assertFalse(player.getBoolean("Enabled"));
        assertEquals(raw.getList("Scripts", 10).getCompound(0), player.getCompound("SourceTab"));
        assertTrue(net.bullettrain.xenonpcs.npc.store.XenoNpcScripts.composeSource(
                        store.list(XenoNpcStoreCategory.FORGE_SCRIPTS).getFirst().tag(), id -> "var shared = 5;")
                .startsWith("var shared = 5;\n"));
        assertEquals(raw.toString(), Files.readString(root.resolve("scripts/player_scripts.json")));
        ScriptFileImport.importRoot(foreignRoot, store, false, new NpcImportReport());
        assertEquals(1, store.list(XenoNpcStoreCategory.PLAYER_SCRIPTS).size());
    }

    @Test
    void missingAndTraversingReferencesFailClosedRatherThanRunningPartialNpc() {
        var store = new XenoNpcWorldStore(dir.resolve("store"));
        for (String reference : List.of("missing.js", "../secret.js", "C:\\secret.js", "/secret.js")) {
            var report = new NpcImportReport();
            var container = NpcScriptImport.importContainer(source("function init(e) {}", reference),
                    "mynpcs", "npc_" + report.hashCode(), store, report);
            assertFalse(container.enabled());
            assertEquals(1, report.blocked().size());
            assertTrue(container.tabs().getFirst().loaded().isEmpty());
        }
    }

    @Test
    void refusingOversizedLibraryAlsoDisablesItsGlobalTab() throws Exception {
        Path root = dir.resolve("mynpcs");
        write(root, "scripts/ecmascript/large.js", "x".repeat(32769));
        write(root, "scripts/player_scripts.json", source("function init(e) {}", "large.js").toString());
        var store = new XenoNpcWorldStore(dir.resolve("store"));
        var report = new NpcImportReport();
        ScriptFileImport.importRoot(new ForeignNpcRoots.Root("mynpcs", root), store, false, report);
        assertTrue(store.list(XenoNpcStoreCategory.SCRIPTS).isEmpty());
        assertEquals(1, report.failures().size());
        assertEquals(1, report.blocked().size());
        assertFalse(store.list(XenoNpcStoreCategory.PLAYER_SCRIPTS).getFirst().tag().getBoolean("Enabled"));
    }

    @Test
    void namespacedLibraryIdsDoNotAliasSanitisedPathsOrMods() {
        assertNotEquals(ScriptFileImport.id("mynpcs", "ecmascript/a b.js"), ScriptFileImport.id("mynpcs", "ecmascript/ab.js"));
        assertNotEquals(ScriptFileImport.id("mynpcs", "ecmascript/lib.js"), ScriptFileImport.id("customnpcs", "ecmascript/lib.js"));
    }

    private static CompoundTag source(String inline, String reference) {
        CompoundTag source = new CompoundTag();
        source.putString("ScriptLanguage", "ECMAScript");
        CompoundTag tab = new CompoundTag();
        tab.putString("Script", inline);
        CompoundTag line = new CompoundTag();
        line.putString("Line", reference);
        ListTag files = new ListTag();
        files.add(line);
        tab.put("ScriptList", files);
        ListTag tabs = new ListTag();
        tabs.add(tab);
        source.put("Scripts", tabs);
        return source;
    }

    private static void write(Path root, String relative, String text) throws Exception {
        Path file = root.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, text);
    }
}
