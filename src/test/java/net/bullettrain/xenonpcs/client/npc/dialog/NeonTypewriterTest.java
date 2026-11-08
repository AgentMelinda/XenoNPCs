package net.bullettrain.xenonpcs.client.npc.dialog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NeonTypewriterTest {

    @Test
    void revealsOneAndAHalfCharactersPerTick() {
        assertEquals(0, NeonTypewriter.visible(100, 0));
        assertEquals(1, NeonTypewriter.visible(100, 1));
        assertEquals(3, NeonTypewriter.visible(100, 2));
        assertEquals(15, NeonTypewriter.visible(100, 10));
    }

    @Test
    void neverShowsMoreThanThereIsAndHandlesEmptyText() {
        assertEquals(10, NeonTypewriter.visible(10, 1000));
        assertEquals(0, NeonTypewriter.visible(0, 50));
        assertEquals(0, NeonTypewriter.visible(10, -4));
        assertEquals(7, NeonTypewriter.visible(7, Integer.MAX_VALUE));
    }

    @Test
    void ticksToFinishIsTheFirstAgeThatShowsEverything() {
        for (int total : new int[] {1, 2, 3, 10, 99, 100, 257}) {
            int ticks = NeonTypewriter.ticksToFinish(total);
            assertTrue(NeonTypewriter.done(total, ticks), "done at " + ticks + " for " + total);
            assertFalse(NeonTypewriter.done(total, ticks - 1), "not done a tick earlier for " + total);
        }
        assertEquals(0, NeonTypewriter.ticksToFinish(0));
    }
}
