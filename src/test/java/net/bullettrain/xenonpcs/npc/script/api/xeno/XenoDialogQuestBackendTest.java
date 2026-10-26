package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.bullettrain.xenonpcs.npc.dialog.XenoDialogue;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class XenoDialogQuestBackendTest {
    @Test void scriptSelectsQuestOwnerExplicitlyAndDoesNotReuseStaleCommand() {
        var dialog = new XenoDialogAdapter("test", "backend", null,
                new XenoDialogue("root", Map.of("root", new XenoDialogue.Node("Hi", List.of(
                        new XenoDialogue.Option("Choose", XenoDialogue.OptionType.COMMAND, "", "", "say stale"))))));
        var option = dialog.getOption(0);
        option.setDmzQuestNpc("goku");
        assertEquals("goku", option.getDmzQuestNpc());
        assertEquals("", option.getXenoQuest());
        assertEquals(0, option.getCommands().length);
        assertEquals(XenoDialogue.OptionType.DMZ_QUEST, dialog.current().options().get(0).type());
        option.setXenoQuest("xenonpcs:training");
        assertEquals("", option.getDmzQuestNpc());
        assertEquals("xenonpcs:training", option.getXenoQuest());
        assertEquals(XenoDialogue.OptionType.QUEST, dialog.current().options().get(0).type());
        assertThrows(IllegalArgumentException.class, () -> option.setDmzQuestNpc("goku command"));
        assertEquals("xenonpcs:training", option.getXenoQuest());
    }
}
