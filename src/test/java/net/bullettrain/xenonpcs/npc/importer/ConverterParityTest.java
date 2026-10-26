package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.features.progression.*;
import net.bullettrain.xenonpcs.npc.dialog.XenoDialogueNbt;
import net.bullettrain.xenonpcs.npc.store.*;
import net.minecraft.nbt.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ConverterParityTest {
    @TempDir Path temp;

    @Test void crossCategoryBranchesAndQuestGatesResolveBeforeConversion() throws Exception {
        Path world = temp.resolve("world");
        Path source = world.resolve("mynpcs");
        Files.createDirectories(source.resolve("dialogs/first"));
        Files.createDirectories(source.resolve("dialogs/second"));
        Files.createDirectories(source.resolve("quests/start"));
        Files.createDirectories(source.resolve("quests/next"));
        Files.writeString(source.resolve("dialogs/first/1.json"), "{DialogId:1,DialogText:\"Hi\",Options:[{Option:{OptionType:1,Title:\"Go\",Dialog:2}}]}");
        Files.writeString(source.resolve("dialogs/second/2.json"), "{DialogId:2,DialogText:\"Later\",AvailabilityQuest:4,AvailabilityQuestId:7,Options:[]}");
        Files.writeString(source.resolve("quests/start/7.json"), "{Title:\"First\",Type:5,NextQuestId:8}");
        Files.writeString(source.resolve("quests/next/8.json"), "{Title:\"Next\",Type:5,NextQuestId:-1}");
        var store = new XenoNpcWorldStore(world.resolve("XenoNpcs"));
        store.loadAll();
        var report = NpcImportService.importAll(world, temp, store, false);
        assertTrue(report.failures().isEmpty(), report.failures().toString());
        var tree = XenoDialogueNbt.read(store.get(XenoNpcStoreCategory.DIALOGS, "mynpcs_first", "dialog_1"));
        assertNotNull(tree.nodes().get("n2"));
        assertEquals("n2", tree.startNode().options().getFirst().target());
        assertEquals("mynpcs_start_q7", tree.nodes().get("n2").availability().quests().getFirst().questId());
        assertEquals(QuestAvailability.QuestState.NOT_ACTIVE, tree.nodes().get("n2").availability().quests().getFirst().state());
        var json = com.google.gson.JsonParser.parseString(store.get(XenoNpcStoreCategory.QUESTS, "mynpcs_start", "mynpcs_start_q7").getString("DefinitionJson")).getAsJsonObject();
        assertEquals("mynpcs_next_q8", json.get("next_quest").getAsString());
    }

    @Test void unresolvedAvailabilityFailsClosedAndDuplicateSlotsAreAmbiguous() {
        var report = new NpcImportReport();
        var raw = new CompoundTag();
        raw.putInt("AvailabilityQuest", 4);
        raw.putInt("AvailabilityQuestId", 123);
        var gate = AvailabilityImport.convert(raw, "mynpcs", report, "quests/mynpcs_start_q7");
        assertFalse(QuestRuntime.available(null, new net.bullettrain.xenonpcs.capability.XenoPlayerData(), gate));
        // The gate is stored shut, so the entry counts as imported; the blocked line is the only
        // thing that says a player will never see it.
        assertEquals(1, report.blocked().size(), report.blocked().toString());
        assertTrue(report.blocked().getFirst().startsWith("quests/mynpcs_start_q7: "), report.blocked().toString());
        assertTrue(report.blocked().getFirst().contains("quest slot 123, which did not import"),
                report.blocked().toString());
        assertTrue(report.summary().contains("1 blocked"), report.summary());
        report.mapDialog("mynpcs", 4, new DialogTreeImport.Ref("one", "dialog_4"));
        report.mapDialog("mynpcs", 4, new DialogTreeImport.Ref("two", "dialog_4"));
        report.mapDialog("mynpcs", 4, new DialogTreeImport.Ref("one", "dialog_4"));
        assertNull(report.dialog("mynpcs", 4));
    }

    /**
     * Quest state 6, which the supplied archives use eleven times.
     *
     * <p>{@code EnumAvailabilityQuest.CanStart} in the CustomNPCs jar is ordinal 6 and our own
     * {@code QuestState.CAN_START} sits at the same ordinal, so refusing to map it was a converter
     * gap and not an unmappable author choice.
     */
    @Test void canStartIsTheSixthQuestStateAndMapsToOurOwn() {
        var report = new NpcImportReport();
        report.mapQuest("mynpcs", 12, "mynpcs_dbz_q4_q12");
        var raw = new CompoundTag();
        raw.putInt("AvailabilityQuest", 6);
        raw.putInt("AvailabilityQuestId", 12);
        var gate = AvailabilityImport.convert(raw, "mynpcs", report, "dialogs/mynpcs_dbz_q4#n14");
        assertEquals(List.of(new QuestAvailability.QuestGate("mynpcs_dbz_q4_q12",
                QuestAvailability.QuestState.CAN_START)), gate.quests());
        assertTrue(report.blocked().isEmpty(), report.blocked().toString());
    }

    /**
     * An author who never picked a quest leaves the id at -1, and the source evaluated that as a
     * lookup miss: {@code isQuestActive(-1)} is false, so NOT_ACTIVE passed for everyone. Carrying
     * it as a gate would hide a dialog the old server showed to every player.
     */
    @Test void aNegatedGateOnNoQuestAtAllIsNoCondition() {
        var report = new NpcImportReport();
        var raw = new CompoundTag();
        raw.putInt("AvailabilityQuest4", 4);
        raw.putInt("AvailabilityQuest4Id", -1);
        var gate = AvailabilityImport.convert(raw, "mynpcs", report, "dialogs/mynpcs_dbz_q4#n13");
        assertEquals(QuestAvailability.NONE, gate);
        assertTrue(report.blocked().isEmpty(), report.blocked().toString());
        assertTrue(report.notes().stream().anyMatch(note -> note.contains("referenced no quest at all")
                && note.contains("condition dropped")), report.notes().toString());
    }

    /** The same unset id behind a positive condition was already impossible in the source itself. */
    @Test void anAffirmativeGateOnNoQuestAtAllStaysShutAndSaysTheSourceWasTheSame() {
        var report = new NpcImportReport();
        var raw = new CompoundTag();
        raw.putInt("AvailabilityDialog", 1);
        raw.putInt("AvailabilityDialogId", -1);
        var gate = AvailabilityImport.convert(raw, "mynpcs", report, "quests/mynpcs_start_q7");
        assertFalse(QuestRuntime.available(null, new net.bullettrain.xenonpcs.capability.XenoPlayerData(), gate));
        assertEquals(1, report.blocked().size(), report.blocked().toString());
        assertTrue(report.blocked().getFirst().contains("already impossible in the source"),
                report.blocked().toString());
    }

    /**
     * One dialog node is converted once per tree that reaches it, so the same problem would otherwise
     * be listed twice and the operator would count twice as many defects as there are.
     */
    @Test void theSameBlockedEntryIsOnlyListedOnce() {
        var report = new NpcImportReport();
        report.blocked("dialogs/x#n1", "same reason");
        report.blocked("dialogs/x#n1", "same reason");
        report.blocked("dialogs/x#n2", "same reason");
        assertEquals(2, report.blocked().size(), report.blocked().toString());
    }

    @Test void uuidProgressMergePreservesXenoAndResumesKillCountersIdempotently() {
        var report = new NpcImportReport();
        report.mapQuest("mynpcs", 1, "raditz");
        report.mapQuest("mynpcs", 2, "saibamen");
        report.mapFaction("mynpcs", 0, "heroes");
        report.mapDialog("mynpcs", 8, new DialogTreeImport.Ref("story", "dialog_8"));
        CompoundTag source = ForeignSnbt.parse("{UUID:\"4f1bc134-bc25-4230-bceb-1d5e22817a40\",QuestData:{CompletedQuests:[{Quest:1,Date:123L}],ActiveQuests:[{Quest:2,ExtraData:{Killed:[{Slot:\"Saibamen A\",Value:2}]}}]},DialogData:[{Dialog:8}],FactionData:[{Faction:0,Points:2000}],ScriptStoreddata:{paid:1.0d}}");
        CompoundTag existing = new CompoundTag();
        existing.putInt("SkillPoints", 91);
        var standing = new CompoundTag(); standing.putInt("heroes", -100); existing.put("FactionStanding", standing);
        var def = new ParallelQuests.QuestDef("saibamen", "Saibamen", "", 5,
                new QuestObjective.Goal(QuestObjective.KILL_NPC, "Saibamen A"), QuestReward.DEFAULT);
        CompoundTag merged = PlayerProgressImport.merge(source, existing, "mynpcs", report, id -> id.equals("saibamen") ? def : null);
        QuestBook book = new QuestBook(); book.loadFrom(merged);
        assertEquals(123, book.completedAt("raditz"));
        assertEquals(2, book.active("saibamen").progress());
        assertEquals(-100, merged.getCompound("FactionStanding").getInt("heroes"));
        assertEquals(91, merged.getInt("SkillPoints"));
        assertEquals(1.0, merged.getCompound("XenoScriptData").getDouble("paid"));
        assertEquals("story/dialog_8", merged.getList("ViewedDialogues", Tag.TAG_STRING).getString(0));
        assertEquals(merged, PlayerProgressImport.merge(source, merged, "mynpcs", report, id -> def));
        assertFalse(existing.contains("ActiveQuests"));
    }

    @Test void sourceEntityModelsAndExplicitGeckoModelsSurvive() {
        var source = new CompoundTag();
        var model = new CompoundTag(); model.putString("EntityName", "dragonminez:saga_raditz"); source.put("NpcModelData", model);
        assertEquals("ENTITY", PlacedNpcProfileMigrator.profileTag(source).getString("ModelKind"));
        assertEquals("dragonminez:saga_raditz", PlacedNpcProfileMigrator.profileTag(source).getString("ModelId"));
        var nativeProfile = new CompoundTag(); nativeProfile.putString("ModelId", "mypack:giant"); nativeProfile.putString("ModelKind", "GECKOLIB");
        var forge = new CompoundTag(); forge.put("XenoPixelsProfile", nativeProfile); source.put("NeoForgeData", forge);
        assertEquals("GECKOLIB", PlacedNpcProfileMigrator.profileTag(source).getString("ModelKind"));
        assertEquals("mypack:giant", PlacedNpcProfileMigrator.profileTag(source).getString("ModelId"));
    }
}
