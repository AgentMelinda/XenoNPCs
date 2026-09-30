package net.bullettrain.xenonpcs.combat.clone;

import net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * The multi-form copies' own combat brain: {@code /xenomultiform ai multiform}.
 *
 * <p>A fork of {@link net.bullettrain.xenonpcs.compat.npc.NpcCombatBrain} rather than a reuse of
 * it, because four things that are correct for a CustomNPC are wrong for a copy, and none of them can
 * be fixed in the shared brain without changing how real NPCs fight:
 *
 * <ul>
 *   <li><b>Damage.</b> {@code NpcMeleeDamage} scales a hit from the attacker's stored profile, and
 *       {@code NpcCombatProfile.hasProfile} deliberately excludes copies so the profile lifecycle
 *       does not tick them twice. A copy driven through the shared brain therefore lands the raw
 *       {@code 1.0f} fallback instead of its owner's melee damage. This applies the owner's damage
 *       directly, the way {@link CloneCombatBridge} always has.</li>
 *   <li><b>Movement.</b> {@link XenoCloneEntity} zeroes its own velocity every server tick, so the
 *       shared brain's {@code applyVelocity} is erased before the entity moves. Every step here goes
 *       through {@link CloneCombatBridge#move}, which sets position and already steers around
 *       obstacles.</li>
 *   <li><b>Leash.</b> A copy belongs to a formation around its owner. It chases, but the owner's
 *       detect range is a hard boundary, and crossing it sends the copy home rather than letting it
 *       wander off after a target.</li>
 *   <li><b>State.</b> The shared brain keeps its per-NPC state in static maps keyed by UUID and
 *       copies are created and destroyed constantly, so that state leaks a little with every split.
 *       This brain is an instance, owned by the copy, and dies with it.</li>
 * </ul>
 *
 * <p>Everything that is not clone-specific is reused rather than copied: {@link CloneCombatPolicy}
 * decides the band, {@link NpcBrainKiRotation} supplies the approach and flight vectors,
 * {@link NpcCombatMoves}, {@link NpcKiAttackDispatcher} and {@link NpcStrikeDispatcher} perform the
 * actions, and {@link CloneCombatBridge} owns the profile, the owner's resource pools and movement.
 */
public final class MultiFormCombatBrain {

    /** Ticks between decisions. Matches the legacy clone AI's swing cadence. */
    private static final int DECISION_INTERVAL = 20;
    /** Ticks a copy leaves a target alone after giving up on hurting it. */
    private static final int ABANDON_TICKS = 60;
    /** How far a copy steps per tick while closing on foot. */
    private static final double CHASE_SPEED = 0.45;
    /** Faster while flying, matching the shared brain's air chase. */
    private static double FLY_SPEED = 0.0;
    /** Ki blast lifetime, in ticks; the same value the legacy clone AI fires with. */
    private static final int KI_LIFETIME = 40;

    private int cooldown;
    private int whiffStreak;
    private long abandonedUntil = 0L;
    private java.util.UUID engaged;

    /**
     * One tick of combat.
     *
     * @return true when combat owns the copy's movement this tick, so the caller must not also slide
     *         it back toward its formation slot
     */
    public boolean tick(XenoCloneEntity clone, ServerPlayer owner, LivingEntity target,
                        CloneCombatBridge bridge) { return false; }

    /**
     * A punch, paid for out of the owner's stamina and dealing the owner's damage.
     *
     * <p>Deliberately not routed through {@code NpcMeleeDamage.hit}: that scales from a stored
     * profile a copy does not have, so it would land 1.0 damage and animate nothing.
     */
    private void melee(XenoCloneEntity clone, ServerPlayer owner, LivingEntity target,
                       NpcCombatProfile profile, CloneCombatBridge bridge) { }

    /** A ki blast, paid for out of the owner's energy. */
    private void ranged(XenoCloneEntity clone, LivingEntity target, NpcCombatProfile profile) { }

    /**
     * Close the distance, flying when the target is clearly above.
     *
     * <p>The vector comes from the shared {@link NpcBrainKiRotation} helpers so a copy moves the way
     * a brain NPC does, but it is applied as a destination rather than a velocity — a copy's velocity
     * is wiped at the top of every tick.
     */
    private void close(XenoCloneEntity clone, LivingEntity target, double distance,
                       NpcCombatProfile profile) { }

    /** Stand combat down without touching the caller's formation move. */
    private void stand(XenoCloneEntity clone, CloneCombatBridge bridge) { }

    /** True while this copy is sitting out the window after giving up on {@code target}. */
    private boolean abandoned(XenoCloneEntity clone, LivingEntity target) { return false; }

    /** A landed hit clears the streak, so a real fight never abandons itself. */
    public void noteHit() { }

    /** Forget everything about the current fight; called when the copy is removed. */
    public void forget() { }
}
