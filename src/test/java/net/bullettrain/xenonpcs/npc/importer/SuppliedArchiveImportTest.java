package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.features.progression.ParallelQuests;
import net.bullettrain.xenonpcs.npc.dialog.XenoDialogue;
import net.bullettrain.xenonpcs.npc.dialog.XenoDialogueNbt;
import net.bullettrain.xenonpcs.npc.store.XenoNpcStoreCategory;
import net.bullettrain.xenonpcs.npc.store.XenoNpcWorldStore;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The operator's own My NPCs and CustomNPCs archives, run through preview, import and repeat.
 *
 * <p>The synthetic fixtures in {@code src/test/resources/clones} pin individual tags; they cannot say
 * whether the converter survives a real save with sixty-three dialogs, cross-references between
 * quests and dialog trees, and content from two mods in one world at the same time. That is the case
 * the migration is actually for, and it is the case the plan asks to be exercised.
 *
 * <p>The archives are supplied material, not committed fixtures, so the test assumes them away when
 * they are not on disk. It is therefore evidence about this checkout, and about any machine that has
 * the same folders - not a guarantee that holds everywhere.
 */
class SuppliedArchiveImportTest {

    /**
     * Where the archives live: {@code -Dxenonpcs.import.dir}, else the checkout's own folder. Gradle
     * runs tests from its own working directory, so the project dir comes in explicitly
     * ({@code xenopixels.projectDir}, set by {@code tasks.withType(Test)}).
     */
    private static Path archiveDir() {
        String configured = System.getProperty("xenonpcs.import.dir");
        if (configured != null && !configured.isBlank()) return Path.of(configured).toAbsolutePath();
        String project = System.getProperty("xenopixels.projectDir");
        return Path.of(project == null || project.isBlank() ? "." : project)
                .resolve("customnpcs-mynpcs").toAbsolutePath();
    }

    @Test
    void previewThenImportThenRepeatAcrossBothArchives(@TempDir Path work) throws Exception {
        Path dir = archiveDir();
        Assumptions.assumeTrue(Files.isDirectory(dir), "no archives under " + dir);
        Path world = work.resolve("world");
        Path gameDir = work.resolve("games");
        List<String> summary = new ArrayList<>();
        for (String mod : List.of("mynpcs", "customnpcs")) {
            Path zip = dir.resolve(mod + ".zip");
            Assumptions.assumeTrue(Files.isRegularFile(zip), "no " + zip);
            Path root = world.resolve(mod);
            extract(zip, root);
            // A 1.5.0 install writes clones beside the game directory, not under the world, and the
            // importer looks in both - so the fixture has to be laid out the way a real one is.
            Path clones = root.resolve("clones");
            if (Files.isDirectory(clones)) {
                Files.createDirectories(gameDir.resolve(mod));
                Files.move(clones, gameDir.resolve(mod).resolve("clones"), StandardCopyOption.ATOMIC_MOVE);
            }
        }

        XenoNpcWorldStore store = new XenoNpcWorldStore(world.resolve("XenoNpcs"));
        store.loadAll();

        NpcImportReport preview = NpcImportService.importAll(world, gameDir, store, true);
        assertTrue(preview.imported() > 0, "the archives hold importable content");
        assertTrue(store.list(XenoNpcStoreCategory.FACTIONS).isEmpty(), "a preview writes nothing");
        assertTrue(store.list(XenoNpcStoreCategory.DIALOGS).isEmpty(), "a preview writes nothing");
        assertTrue(store.list(XenoNpcStoreCategory.QUESTS).isEmpty(), "a preview writes nothing");

        NpcImportReport imported = NpcImportService.importAll(world, gameDir, store, false);
        int dialogs = store.list(XenoNpcStoreCategory.DIALOGS).size();
        int quests = store.list(XenoNpcStoreCategory.QUESTS).size();
        int factions = store.list(XenoNpcStoreCategory.FACTIONS).size();
        summary.add("preview=" + preview.imported() + " imported=" + imported.imported()
                + " notes=" + imported.notes().size() + " blocked=" + imported.blocked().size()
                + " failed=" + imported.failures().size()
                + " stored(dialogs=" + dialogs + ",quests=" + quests + ",factions=" + factions + ")");
        System.out.println("XENO ARCHIVE IMPORT " + String.join(" | ", summary));
        for (String line : imported.blocked()) {
            System.out.println("XENO ARCHIVE BLOCKED " + line);
        }
        for (NpcImportReport.Failure failure : imported.failures()) {
            System.out.println("XENO ARCHIVE FAILED " + failure);
        }
        imported.notes().stream().filter(note -> note.contains("did not import")
                        || note.contains("no quest") || note.contains("no dialog") || note.contains("no faction"))
                .forEach(note -> System.out.println("XENO ARCHIVE NOTE " + note));
        int scripts = store.list(XenoNpcStoreCategory.SCRIPTS).size()
                + store.list(XenoNpcStoreCategory.PLAYER_SCRIPTS).size()
                + store.list(XenoNpcStoreCategory.FORGE_SCRIPTS).size();
        assertTrue(scripts > 0, "supplied script libraries and global tabs must import");
        assertEquals(quests + dialogs + factions + scripts, imported.imported(),
                "every stored entry is counted, and nothing else is: " + imported.summary());
        // Execute one supplied library through the bundled engine. JS probes isolate the source
        // contract from live entity APIs; this is script execution, not an in-game event claim.
        var steps = store.list(XenoNpcStoreCategory.SCRIPTS).stream()
                .filter(entry -> entry.tag().getString("SourcePath").equals("ecmascript/xenopixels_ssj_steps.js"))
                .findFirst().orElseThrow();
        String probe = "\nvar probeCalls = 0; var probeState = -1;"
                + "var XenoPixels = {setMastery: function(n,g,f,v) { probeCalls++; }};"
                + "var probeNpc = {getStoreddata:function(){return {has:function(k){return probeState !== -1;},"
                + "get:function(k){return probeState;},put:function(k,v){probeState=v;}};}};"
                + "function archiveProbe(){init({npc:probeNpc});return probeCalls + ':' + probeState;}";
        var instance = net.bullettrain.xenonpcs.npc.script.NpcScriptEngines.forLanguage("ecmascript")
                .instantiate(steps.tag().getString("Script") + probe,
                        net.bullettrain.xenonpcs.npc.script.NpcScriptScope.EMPTY);
        assertTrue(instance.ok(), instance.loadResult().describe());
        var executed = instance.call("archiveProbe");
        assertTrue(executed.ok(), executed.describe());
        assertEquals("3:0", executed.value());

        // Whatever arrived has to be readable by the systems that consume it, or the import wrote
        // rows that only the importer itself can parse.
        for (var entry : store.list(XenoNpcStoreCategory.QUESTS)) {
            String json = entry.tag().getString("DefinitionJson");
            ParallelQuests.QuestDef def = net.bullettrain.xenonpcs.features.progression.XenoQuests
                    .parse(entry.id(), com.google.gson.JsonParser.parseString(json));
            assertNotNull(def, "quest " + entry.group() + "/" + entry.id() + " does not parse");
            assertFalse(def.title().isBlank(), "quest " + entry.id() + " arrived nameless");
        }
        for (var entry : store.list(XenoNpcStoreCategory.DIALOGS)) {
            XenoDialogue tree = XenoDialogueNbt.read(entry.tag());
            assertNotNull(tree, "dialog " + entry.group() + "/" + entry.id() + " does not read back");
            assertNotNull(tree.nodes().get(tree.start()), "dialog " + entry.id() + " has no start node");
        }

        NpcImportReport repeat = NpcImportService.importAll(world, gameDir, store, false);
        assertEquals(0, repeat.imported(), "a repeat import must not claim new entries: " + repeat.summary());
        assertEquals(dialogs, store.list(XenoNpcStoreCategory.DIALOGS).size(), "a repeat import rewrote dialogs");
        assertEquals(quests, store.list(XenoNpcStoreCategory.QUESTS).size(), "a repeat import rewrote quests");
        assertFalse(repeat.failures().isEmpty(), "and it says so, per entry");

        for (NpcImportReport.Failure failure : repeat.failures()) {
            assertTrue(failure.toString().contains("already exists"), failure.toString());
        }
        // Both clone trees are present in these archives, so the skip has to be visible.
        assertTrue(imported.notes().stream().anyMatch(note -> note.contains("clone root left alone")
                        && note.contains("mynpcs")), imported.notes().toString());
        assertTrue(imported.notes().stream().anyMatch(note -> note.contains("clone root left alone")
                        && note.contains("customnpcs")), imported.notes().toString());
        // The two archives define the same five factions and their Aggressive differs (mynpcs attacks
        // Neutral, customnpcs attacks nobody), so the refused copy has to say which root lost - the
        // server now runs the other one's tuning.
        assertTrue(imported.failures().stream().anyMatch(failure -> failure.category().equals("factions")
                        && failure.entry().startsWith("customnpcs/")),
                "a faction both archives define names the refused root: " + imported.failures());
    }

    private static void extract(Path zip, Path target) throws IOException {
        Files.createDirectories(target);
        try (ZipFile archive = new ZipFile(zip.toFile())) {
            var entries = archive.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                Path out = target.resolve(winSafe(entry.getName())).normalize();
                if (!out.startsWith(target)) {
                    throw new IOException("archive entry escapes its folder: " + entry.getName());
                }
                Files.createDirectories(out.getParent());
                Files.copy(archive.getInputStream(entry), out, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * The name a real Windows server would have stored this content under.
     *
     * <p>{@code mynpcs.zip} has a quest folder literally named {@code The Saiyans greatest form. . .}
     * - trailing dots and spaces, which the archive format keeps but Win32 refuses to create, so
     * extracting verbatim gives a directory the copy step cannot resolve. Trimming them is what the
     * source mod would have been forced to do on this platform; the group name the importer then
     * derives is the one an operator on Windows would actually see.
     */
    private static String winSafe(String entryName) {
        String[] parts = entryName.replace('\\', '/').split("/");
        StringBuilder path = new StringBuilder();
        for (String part : parts) {
            String trimmed = part.stripTrailing();
            while (trimmed.endsWith(".")) {
                trimmed = trimmed.substring(0, trimmed.length() - 1).stripTrailing();
            }
            if (trimmed.isEmpty()) continue;
            if (path.length() > 0) path.append('/');
            path.append(trimmed);
        }
        return path.toString();
    }
}
