package net.bullettrain.xenonpcs.fx.effek;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

/**
 * Whether an effect may play: the server config switches, the per-tick punch cap, and one punch
 * effect per target per tick (a combo hit reaches both {@code CombatFx} and the damage hook).
 * Pure and single-threaded: only the server thread plays effects.
 */
public final class EffectGate {
    public enum Category { PUNCH, HAKAI, MISSILE, SPARKING, SHIP, KI }

    /** Why an effect may or may not play. */
    public enum Result { OK, OFF, CAPPED, DUPLICATE }

    private long tick = 0L;
    private int punchesThisTick;
    private IntOpenHashSet punchedThisTick = null;

    /** Check and, when allowed, record it. @param targetId entity id of the target, or -1 */
    public boolean allow(EffectSlot slot, long gameTick, int targetId) { return false; }

    /** Whether it may play, without recording anything. */
    public Result check(EffectSlot slot, long gameTick, int targetId) { return null; }

    /** Records a played effect (punch cap and per-target slot). */
    public void commit(EffectSlot slot, long gameTick, int targetId) { }

    private void roll(long gameTick) { }

    /** How far away players are still sent this effect, in blocks. */
    public double range(EffectSlot slot) { return 0.0; }

    public void reset() { }

    private static boolean categoryOn(Category category) { return false; }
}
