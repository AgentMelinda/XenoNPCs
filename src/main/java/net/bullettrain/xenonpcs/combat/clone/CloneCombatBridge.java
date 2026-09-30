package net.bullettrain.xenonpcs.combat.clone;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import net.bullettrain.xenonpcs.compat.npc.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

/** Server-only adapter. Copies never receive generic NPC profiles or independent resource pools. */
public final class CloneCombatBridge {
    private NpcCombatProfile profile;
    private int refreshAt;
    private int cooldown;
    private java.util.UUID attackTarget;
    /** Melee attempts in a row that connected with nothing; see CloneCombatPolicy.shouldAbandon. */
    private int whiffStreak;
    /** Server tick this clone stops refusing the target it gave up on, or Long.MIN_VALUE. */
    private long abandonedUntil = 0L;
    /** Whether {@link #cancel} has already run since combat last owned this clone. */
    private boolean cancelled = true;

    public static ServerPlayer owner(XenoCloneEntity clone) { return null; }

    public static XenoCloneEntity sourceClone(Entity source) { return null; }

    public static boolean validTarget(ServerPlayer owner, LivingEntity target) { return false; }

    public static boolean permittedTarget(ServerPlayer owner, LivingEntity target) { return false; }

    public static boolean active(XenoCloneEntity clone) { return false; }

    private static StatsData stats(ServerPlayer player) { return null; }

    public static com.dragonminez.common.stats.techniques.StrikeAttackData strike(XenoCloneEntity clone, String id) { return null; }

    public static double strikeCost(XenoCloneEntity clone,
            com.dragonminez.common.stats.techniques.StrikeAttackData strike) { return 0.0; }

    /**
     * Stamina one melee swing costs the owner, never less than 1.
     *
     * <p>Exposed so the multi-form brain charges a punch exactly what the legacy clone AI charges
     * one. The pools are the owner's, so the two AI modes drawing from them at different rates would
     * make the choice of mode a balance decision rather than a behaviour one.
     */
    public static double meleeStaminaCost(XenoCloneEntity clone) { return 0.0; }

    public static NpcResources.Snapshot resources(XenoCloneEntity clone) { return null; }

    public static boolean spend(XenoCloneEntity clone, double energy, double stamina) { return false; }

    public NpcCombatProfile profile(XenoCloneEntity clone) { return null; }

    /** How long a clone leaves a target alone after giving up on hurting it. */
    private static final int ABANDON_TICKS = 60;

    private boolean abandoned(XenoCloneEntity clone, LivingEntity target) { return false; }

    /**
     * Stand this clone's combat down.
     *
     * <p>Idempotent. Every idle tick used to re-broadcast {@code NpcDmzAnim.stop} and
     * {@code XenoAnimApi.stopClip} to every client tracking the clone, which is a packet per clone
     * per tick for a formation that is simply standing still, and it interleaved a stop with the
     * punch clip starting on the next tick. The movement zero still runs unconditionally: that is
     * local state, it is free, and the formation code relies on it.
     */
    public void cancel(XenoCloneEntity clone) { }

    public void noteHit() { }

    /** Returns true when combat owns movement this tick. */
    public boolean tick(XenoCloneEntity clone, ServerPlayer owner, LivingEntity target) { return false; }

    public static void move(XenoCloneEntity clone, Vec3 destination, double speed) { }
}
