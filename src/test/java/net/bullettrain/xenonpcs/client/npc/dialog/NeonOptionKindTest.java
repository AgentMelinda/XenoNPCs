package net.bullettrain.xenonpcs.client.npc.dialog;

import net.bullettrain.xenonpcs.npc.dialog.XenoDialogue;
import net.bullettrain.xenonpcs.npc.dialog.XenoDialogue.OptionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NeonOptionKindTest {

    private static XenoDialogue.Option option(OptionType type, String quest) {
        return new XenoDialogue.Option("text", type, "", quest, "");
    }

    @Test
    void questOfferWithAnIdIsThePrimaryButton() {
        assertEquals(NeonOptionKind.PRIMARY, NeonOptionKind.of(option(OptionType.QUEST, "xenonpcs:kill")));
    }

    @Test
    void questOptionNamingNoQuestCannotBeClicked() {
        assertEquals(NeonOptionKind.DISABLED, NeonOptionKind.of(option(OptionType.QUEST, "")));
        assertEquals(NeonOptionKind.DISABLED, NeonOptionKind.of(option(OptionType.QUEST, "  ")));
        assertEquals(NeonOptionKind.DISABLED, NeonOptionKind.of(option(OptionType.QUEST, null)));
    }

    @Test
    void roleOptionsAreDisabledBecauseNoRoleSystemExists() {
        assertFalse(NeonOptionKind.of(option(OptionType.ROLE, "")).usable());
    }

    @Test
    void everyOtherTypeIsUsable() {
        assertEquals(NeonOptionKind.SECONDARY, NeonOptionKind.of(option(OptionType.QUIT, "")));
        for (OptionType type : new OptionType[] {OptionType.TEXT, OptionType.COMMAND, OptionType.TRANSPORT}) {
            assertEquals(NeonOptionKind.CHOICE, NeonOptionKind.of(option(type, "")));
        }
        assertTrue(NeonOptionKind.PRIMARY.usable());
        assertFalse(NeonOptionKind.of(null).usable());
    }
}
