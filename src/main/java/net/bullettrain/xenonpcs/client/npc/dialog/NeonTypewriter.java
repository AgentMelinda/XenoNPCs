package net.bullettrain.xenonpcs.client.npc.dialog;

/**
 * How much of the NPC's line is showing, as it is typed out.
 *
 * <p>Pure, so the pacing is a number a test can check rather than a feel. A click or key press
 * skips to the end; a conversation that makes the player wait for every line is worse than one that
 * has no animation at all.
 */
public final class NeonTypewriter {

    /** Characters revealed per client tick, in half-characters (3 = 1.5 characters a tick). */
    static final int HALF_CHARS_PER_TICK = 3;

    private NeonTypewriter() {
    }

    /** Characters visible after {@code ageTicks} ticks, never more than {@code total}. */
    public static int visible(int total, int ageTicks) {
        if (total <= 0) {
            return 0;
        }
        if (ageTicks < 0) {
            return 0;
        }
        long shown = (long) ageTicks * HALF_CHARS_PER_TICK / 2;
        return (int) Math.min(total, shown);
    }

    public static boolean done(int total, int ageTicks) {
        return visible(total, ageTicks) >= total;
    }

    /** The age at which {@code total} characters are fully shown. */
    public static int ticksToFinish(int total) {
        return total <= 0 ? 0 : (total * 2 + HALF_CHARS_PER_TICK - 1) / HALF_CHARS_PER_TICK;
    }
}
