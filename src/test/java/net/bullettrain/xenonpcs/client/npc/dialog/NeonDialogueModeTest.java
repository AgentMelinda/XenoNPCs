package net.bullettrain.xenonpcs.client.npc.dialog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The three presentations stay switchable: the neon menu wins, then bubbles, then the old screen. */
class NeonDialogueModeTest {

    @Test
    void neonMenuWinsWhenOn() {
        assertEquals(NeonDialogueMode.NEON, NeonDialogueMode.choose(true, true));
        assertEquals(NeonDialogueMode.NEON, NeonDialogueMode.choose(true, false));
    }

    @Test
    void turningTheMenuOffRestoresThePreviousChoice() {
        assertEquals(NeonDialogueMode.BUBBLES, NeonDialogueMode.choose(false, true));
        assertEquals(NeonDialogueMode.CLASSIC, NeonDialogueMode.choose(false, false));
    }
}
