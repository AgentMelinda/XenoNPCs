package net.bullettrain.xenonpcs.npc.dialog;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DmzNpcQuestPolicyTest {
    @Test void rejectsUnavailablePlayersHostsAndInvalidReach() {
        assertTrue(DmzNpcQuestPolicy.mayOpen(true, true, true, false, 64, "goku"));
        assertFalse(DmzNpcQuestPolicy.mayOpen(false, true, true, false, 1, "goku"));
        assertFalse(DmzNpcQuestPolicy.mayOpen(true, false, true, false, 1, "goku"));
        assertFalse(DmzNpcQuestPolicy.mayOpen(true, true, false, false, 1, "goku"));
        assertFalse(DmzNpcQuestPolicy.mayOpen(true, true, true, true, 1, "goku"));
        for (double distance : new double[] {64.01, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertFalse(DmzNpcQuestPolicy.mayOpen(true, true, true, false, distance, "goku"));
        }
    }

    @Test void boundsNativeBindingWithoutInterpretingCommandsOrWhitespace() {
        for (String id : new String[] {"", " ", "goku command", "goku\n", "x".repeat(129)}) {
            assertFalse(DmzNpcQuestPolicy.validNpcId(id));
        }
        assertFalse(DmzNpcQuestPolicy.validNpcId(null));
        assertTrue(DmzNpcQuestPolicy.validNpcId("goku"));
        assertTrue(DmzNpcQuestPolicy.validNpcId("dragonminez:quest_giver"));
    }

    @Test void bothBackendsSurviveNpcSaveWithoutReinterpretingExistingQuestIds() {
        XenoDialogue original = new XenoDialogue("root", Map.of("root", new XenoDialogue.Node("Quests", List.of(
                new XenoDialogue.Option("Xeno", XenoDialogue.OptionType.QUEST, "", "goku", ""),
                new XenoDialogue.Option("DMZ", XenoDialogue.OptionType.DMZ_QUEST, "", "goku", "")))));
        var restored = XenoDialogueNbt.read(XenoDialogueNbt.write(original));
        assertNotNull(restored);
        assertEquals(XenoDialogue.OptionType.QUEST, restored.startNode().options().get(0).type());
        assertEquals(XenoDialogue.OptionType.DMZ_QUEST, restored.startNode().options().get(1).type());
        assertEquals("goku", restored.startNode().options().get(0).quest());
        assertEquals("goku", restored.startNode().options().get(1).quest());
    }

    @Test void authoredJsonSelectsNativeBackendExplicitly() {
        var dialogue = XenoDialogue.fromJson(JsonParser.parseString("""
                {"nodes":{"root":{"options":[
                {"type":"quest","quest":"goku"},
                {"type":"dmz_quest","quest":"goku"}]}}}
                """));
        assertEquals(XenoDialogue.OptionType.QUEST, dialogue.startNode().options().get(0).type());
        assertEquals(XenoDialogue.OptionType.DMZ_QUEST, dialogue.startNode().options().get(1).type());
    }
}
