package net.bullettrain.xenonpcs.combat.clone;

/** Original, deterministic decisions; no third-party AI implementation is used. */
public final class CloneCombatPolicy {
    public enum Action { FORMATION, APPROACH, MELEE, RANGED, RECOVER }
    public static final double LEASH = 32;
    public static final double MELEE_RANGE = 2.8;
    public static final double RANGED_RANGE = 24;

    private CloneCombatPolicy() {  }

    public static Action decide(boolean active, boolean validTarget, double ownerDistance,
                                double targetDistance, boolean visible, int cooldown) { return null; }

    public static Action decide(boolean active, boolean validTarget, double ownerDistance,
                                double targetDistance, boolean visible, int cooldown, double leash) { return null; }

    /**
     * How many melee attempts in a row may land nothing before a clone gives up on its target.
     *
     * <p>Small on purpose. A clone that cannot hurt what it is swinging at will never be able to,
     * and the failure looks identical to the clone being broken: it stands there punching air.
     */
    public static final int MAX_WHIFFS = 3;

    /**
     * Whether a clone should disengage after a run of melee attempts that connected with nothing.
     *
     * <p>{@code LivingEntity.hurt} returning false is the honest signal that a swing did nothing —
     * the target is invulnerable, already dying, protected by another mod, or the damage was
     * cancelled outright. None of that is visible to {@link #decide}, whose inputs are all
     * positional, so a clone would otherwise keep choosing MELEE forever against something it can
     * never actually hit.
     */
    public static boolean shouldAbandon(int whiffStreak) { return false; }

    /** True only while a lock-on body is actually present and alive. */
    public static boolean hasLivingLock(boolean present, boolean alive) { return false; }

    /**
     * Updates the miss counter after one swing. A connected hit resets; a miss (including a
     * strike that fired but has not landed) counts toward {@link #shouldAbandon}.
     */
    public static int noteSwing(int whiffStreak, boolean connected) { return 0; }

    public static boolean canSpend(double energy, double stamina, double energyCost, double staminaCost) { return false; }

    public static boolean earnsMastery(long now, long previous, float damage, int mastery) { return false; }
}
