package net.bullettrain.xenonpcs.network;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.character.Resources;
import net.bullettrain.xenonpcs.config.XenoServerConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import com.dragonminez.compat.util.LazyOptional;
import com.dragonminez.compat.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Budokai Tenkaichi / Sparking Zero inspired combat (server-authoritative).
 */
public class Bt3CombatPacket {
    public enum Action {
        COMBO_HIT,
        /** Vanish behind lock-on target (close range). */
        VANISH,
        /** High-speed chase into the target from mid range. */
        CHASE_DASH,
        /** Step back off the target while facing them. */
        BACKSTEP,
        /** Hold-charge fist smash (stamina). */
        CHARGE_FIST,
        /** Hold-charge kick (stamina). */
        CHARGE_KICK,
        /** Dragon dash: smash target away then chase. */
        DRAGON_DASH,
        /** Hold-to-block. comboStep 1 = start/hold, 0 = release. */
        GUARD,
        /** Super-counter vanish (after hit window). */
        SUPER_COUNTER,
        /** Mid-combo light ki blast that cancels the string. */
        KI_BLAST_CANCEL,
        /** Mid-combo Z-Burst step-in toward target. */
        Z_BURST,
        /** Air rush / chase chain after knockup. */
        RUSH_CHAIN,
        /** Sonic sway side-step (comboStep: -1 left, +1 right). */
        SONIC_SWAY,
        /** Ultimate skill smash. */
        ULTIMATE,
        /** Activate sparking mode (full meter). */
        SPARKING,
        /** Start a Hakai erasure channel on the target (Ctrl + left click). */
        HAKAI_START,
        /** Ctrl or the mouse button was released: stop channeling, no penalty. */
        HAKAI_CANCEL,
        /** Release chase hold: stop mid-flight. Appended — do not reorder. */
        CHASE_STOP,
        /** Open a Zanzoken read. Appended — do not reorder. */
        ZANZOKEN,
        /** Shi Shin No Ken: divide into several bodies, or reunite. Appended — do not reorder. */
        MULTIFORM,
        /** Sync ki charge percent to clones (0-100). Appended — do not reorder. */
        SYNC_KI_CHARGE,
        /** X-X-X then Dragon Dash button automatic cinematic rush. Appended — do not reorder. */
        CINEMATIC_RUSH,
        /** Grant-gated rush-combo strike. Appended — do not reorder. */
        RUSH_COMBO,
        /** Grant-gated lift-combo strike. Appended — do not reorder. */
        LIFT_COMBO
    }

    /** Fallback when config has not loaded yet. Prefer {@link XenoServerConfig#vanishGap}. */
    public static final double VANISH_GAP = 1.35;
    /** Left/right offset for A vs D vanish. */
    public static final double VANISH_SIDE = 1.05;
    /** Fallback; live chase stop uses {@link XenoServerConfig#chaseStopGap}. */
    public static final double CHASE_GAP = 0.0;
    public static final double BACKSTEP_DIST = 3.2;
    public static final double DRAGON_LAUNCH = 6.5;

    private Action action;
    private int targetId;
    private int comboStep;
    /** 0..100 charge percent for charge attacks / dragon dash. */
    private int chargePercent;
    /** Kick vertical bias: +1 hold W (up), -1 hold S (down), 0 neutral. */
    private int verticalBias;
    /**
     * COMBO_HIT only: which held direction key was down, as a
     * {@code Bt3ComboChoreography.MASH_STYLE_*} constant. The client cannot be trusted for the
     * combo step, but it is the only side that knows which key is held, so the style rides along
     * and the server applies it to the step it owns.
     */
    private int mashStyle;

    public Bt3CombatPacket(Action action, int targetId, int comboStep) { this(action, targetId, comboStep, 0, 0); }

    public Bt3CombatPacket(Action action, int targetId, int comboStep, int chargePercent) { this(action, targetId, comboStep, chargePercent, 0); }

    public Bt3CombatPacket(Action action, int targetId, int comboStep, int chargePercent, int verticalBias) { this(action, targetId, comboStep, chargePercent, verticalBias,
                net.bullettrain.xenonpcs.combat.Bt3ComboChoreography.MASH_STYLE_ROUTE); }

    public Bt3CombatPacket(Action action, int targetId, int comboStep, int chargePercent, int verticalBias,
                           int mashStyle) {  }

    public static void encode(Bt3CombatPacket msg, FriendlyByteBuf buf) { }

    public static Bt3CombatPacket decode(FriendlyByteBuf buf) { return null; }

    public static void handle(Bt3CombatPacket msg, Supplier<NetworkEvent.Context> ctx) { }

    /**
     * @param side -1 = double-tap A (left of behind), +1 = double-tap D (right of behind)
     */
    private static void handleVanish(ServerPlayer player, LivingEntity target, Resources res, int side) { }

    private static void handleChase(ServerPlayer player, LivingEntity target, Resources res) { }

    private static void handleBackstep(ServerPlayer player, LivingEntity target, Resources res) { }

    /**
     * Charge punch or kick with no lock-on: play the release anim and hit a short cone ahead.
     * Stay planted; only entities in the cone take knockback.
     */
    private static void handleUntargetedCharge(ServerPlayer player, Resources res, StatsData data,
                                               boolean kick, int chargePercent, int verticalBias) { }

    private static void handleChargeAttack(ServerPlayer player, LivingEntity target, Resources res, StatsData data,
                                           boolean kick, int chargePercent, int verticalBias) { }

    /** Hit reach for charged kicks; S-hold extends range. */
    public static double kickHitRange(float charge, int verticalBias) {
        double base = Math.max(5.5, XenoServerConfig.chargeAttackRange + 0.75);
        if (verticalBias < 0) {
            base += XenoServerConfig.kickDownRangeBonus * (0.75 + 0.35 * charge);
        }
        return base;
    }

    /**
     * +1 W = launch target upward, -1 S = smash downward, 0 = default arc.
     */
    public static Vec3 kickTargetLaunch(Vec3 awayFlat, float charge, int verticalBias) {
        double horiz = (0.9 + charge) * (verticalBias == 0 ? 1.0 : 0.55)
                * Math.max(0.1, XenoServerConfig.kickKnockbackScale);
        double up;
        if (verticalBias > 0) {
            double scale = Math.max(0.1, XenoServerConfig.kickKnockbackScale);
            double[] d = net.bullettrain.xenonpcs.combat.Bt3ComboChoreography.launcherDelta(
                    awayFlat.x, awayFlat.z,
                    XenoServerConfig.comboLauncherHoriz * scale,
                    XenoServerConfig.comboLauncherUp);
            return new Vec3(d[0], d[1], d[2]);
        } else if (verticalBias < 0) {
            up = -XenoServerConfig.kickDownLaunch * (0.7 + charge * 0.8);
            horiz *= 0.65;
        } else {
            return ballArcLaunch(awayFlat, charge);
        }
        return awayFlat.normalize().scale(horiz).add(0, up, 0);
    }

    /** Ballistic hang-time arc for kicks (mash + charged). */
    private static Vec3 ballArcLaunch(Vec3 awayFlat, float charge) {
        float c = Math.max(0.25f, Math.min(1.0f, charge));
        double scale = Math.max(0.1, XenoServerConfig.kickKnockbackScale);
        double horiz = Math.max(0.7, XenoServerConfig.comboLauncherHoriz * 1.5) * scale * (0.75 + 0.4 * c);
        double up = Math.max(1.5, XenoServerConfig.comboLauncherUp * 0.95) * (0.8 + 0.35 * c);
        double[] d = net.bullettrain.xenonpcs.combat.Bt3ComboChoreography.launcherDelta(
                awayFlat.x, awayFlat.z, horiz, up);
        return new Vec3(d[0], d[1], d[2]);
    }

    private static double kickVerticalImpulse(ServerPlayer player, float charge, int verticalBias, boolean aerialSelf) { return 0.0; }

    /**
     * Dragon dash: heavy hit launches the target, then the attacker teleports/chases after them.
     */
    private static void handleDragonDash(ServerPlayer player, LivingEntity target, Resources res, StatsData data,
                                         int chargePercent) { }

    /** Returns true if chase succeeds based on {@link XenoServerConfig#chaseSuccessChance}. */
    private static boolean rollChaseSuccess(ServerPlayer player) { return false; }

    static boolean rollChaseSuccess(float chance, java.util.function.DoubleSupplier roll) { return false; }

    private static void handleSuperCounter(ServerPlayer player, LivingEntity target, Resources res, StatsData data,
                                           int side) { }

    private static void handleKiBlastCancel(ServerPlayer player, LivingEntity target, Resources res, StatsData data) { }

    private static void handleRushChain(ServerPlayer player, LivingEntity target, Resources res, StatsData data) { }


    /**
     * Opens a Zanzoken read. The ki is spent on the press, not on the dodge — reading the swing
     * wrong is supposed to cost something, or the move is just a better block.
     */
    public static void handleZanzoken(ServerPlayer player) { }

    /**
     * A read that landed: leave the image where the body was, then reappear behind the attacker.
     *
     * <p>Called from the damage hook rather than the packet handler, because the technique only
     * resolves at the moment something actually swings.
     */
    public static void performZanzoken(ServerPlayer player, LivingEntity attacker) { }

    /**
     * Points the attacker's lock-on at one of the images.
     *
     * <p>DragonMineZ's lock marker follows the real entity, so without this it names the dodger
     * outright and the ring is decoration. Redirecting rather than clearing matters: a lock that
     * simply drops announces the dodge, while a lock resting on an image actively misleads, and it
     * breaks on its own when that image is struck.
     *
     * <p>Only touched when the attacker actually had the dodger locked. A lock aimed at someone
     * else is none of this technique's business.
     */
    private static void redirectLockToImage(
            ServerPlayer player, LivingEntity attacker,
            java.util.List<net.bullettrain.xenonpcs.combat.clone.XenoCloneEntity> images) { }

    private static void redirectOneLock(
            ServerPlayer player, ServerPlayer hunter,
            java.util.List<net.bullettrain.xenonpcs.combat.clone.XenoCloneEntity> images) { }

    /** DragonMineZ ships the afterimage sound; fall back to the generic evade cue if it is gone. */
    private static void playZanzoken(ServerPlayer player, Vec3 at) { }

    /**
     * Shi Shin No Ken. Toggles the split, charging ki only when dividing — reuniting is free,
     * because a fighter should never be stranded in four weak bodies by an empty bar.
     */
    public static void handleMultiForm(ServerPlayer player) { }

    private static void handleSonicSway(ServerPlayer player, Resources res, int side) { }

    private static void handleUltimate(ServerPlayer player, LivingEntity target, Resources res, StatsData data) { }

    private static void handleZBurst(ServerPlayer player, LivingEntity target, Resources res, StatsData data) { }

    private static void handleCombo(ServerPlayer player, LivingEntity target, Resources res, StatsData data,
                                    int verticalBias, int mashStyle) { }

    private static void startAutomaticBallChase(ServerPlayer player, LivingEntity target) { }

    /**
     * @return false when the player cannot pay. A missing DMZ {@link Resources} capability means
     * "cannot act", not "free" — the old {@code res == null → true} gave every move away to a
     * player mid-join data race (or a forged state) while they still dealt fallback damage.
     * Zero-cost actions are always allowed regardless.
     */
    /**
     * Begins a Hakai channel, resolving the target and refusing with a reason when it cannot.
     *
     * <p>Shared by the {@code HAKAI_START} packet and by a cast from a DragonMineZ technique slot,
     * so both routes get identical targeting, range and enable checks. The channel completes on
     * its own timer, so a slot cast needs no release to finish it; only the held key sends a
     * {@code HAKAI_CANCEL} to abort early.
     *
     * @param target the caster's locked target, or {@code null} to search where they are looking
     */
    public static void startHakai(ServerPlayer player, LivingEntity target) { }

    private static void hakaiFail(ServerPlayer player, String reason) { }

    private static boolean trySpendKi(Resources res, float cost) { return false; }

    private static boolean trySpendStamina(Resources res, float cost) { return false; }

    private static void teleport(ServerPlayer player, Vec3 dest) { }

    /**
     * Teleport and face the target in one packet — zeros velocity so DMZ flight
     * cannot carry residual speed after the warp.
     */
    static void teleportFacing(ServerPlayer player, Vec3 dest, LivingEntity target) { }

    /** Position + look teleport without {@code hurtMarked} (chase flight; hurtMarked cranks the camera). */
    static void teleportPos(ServerPlayer player, Vec3 dest, float yaw, float pitch) { }

    /**
     * BT3 vanish: land just past the target (their back relative to you),
     * offset left (A) or right (D). Always at the target's height so you don't
     * stay sky-high and "fly away".
     *
     * @param side -1 left, +1 right, 0 center-behind
     */
    public static Vec3 vanishBehind(Entity player, LivingEntity target) {
        return vanishBehind(player, target, 0);
    }

    public static Vec3 vanishBehind(Entity player, LivingEntity target, int side) {
        return vanishPoint(
                player.getX(), player.getZ(),
                target.getX(), target.getY(), target.getZ(),
                target.yBodyRot, side,
                Math.max(0.0, XenoServerConfig.vanishGap),
                Math.max(0.0, XenoServerConfig.vanishSide),
                Math.max(0.0, XenoServerConfig.vanishNearField));
    }

    /**
     * The vanish landing point, as pure geometry.
     *
     * <p>At range the landing direction is the line you came in on, so you end up past the target
     * on the far side from where you stood — the original behaviour, reproduced exactly once
     * {@code sep >= nearField}.
     *
     * <p>Up close that line stops meaning anything. It used to be trusted down to a hundredth of a
     * block, and a vector that short is noise: it flips between frames, and the client — which
     * predicts this same landing from positions roughly a hundred milliseconds behind the
     * server's — could compute a direction opposite to the one the server picked and get yanked
     * across the target. Hovering directly above or below someone made it worse still, because the
     * horizontal separation there is near zero while the fight is very much close range. So the
     * closer you are, the more the direction comes from the target's own body facing, which is
     * both the spot a vanish is supposed to land on and a value that is synchronised between
     * client and server in a way a sub-block position delta is not. The two blend, so there is no
     * snap as you cross the boundary, and {@code nearField} of zero disables the near-field
     * behaviour entirely.
     *
     * @param targetBodyYawDeg the target's {@code yBodyRot}
     * @param side             -1 left, +1 right, 0 centre-behind
     */
    public static Vec3 vanishPoint(double px, double pz, double tx, double ty, double tz,
                                   float targetBodyYawDeg, int side,
                                   double gap, double sideOff, double nearField) { return null; }

    /**
     * {@link #vanishBehind} moved off anything solid.
     *
     * <p>Nothing on the vanish path used to check this, so a vanish aimed into terrain teleported
     * the fighter into the terrain. The candidates are a fixed list rather than a search, because
     * the attacking client runs this too in order to predict its own landing and any difference in
     * the order the two sides try spots shows up as a rubber-band. A vanish with nowhere to go
     * leaves the fighter standing where they were, which is a wasted move rather than a
     * suffocation.
     */
    public static Vec3 vanishLanding(Entity player, LivingEntity target, int side) {
        Vec3 preferred = vanishBehind(player, target, side);
        if (!XenoServerConfig.vanishOpenSpotSearch || isSpotOpen(player, preferred)) {
            return preferred;
        }
        Vec3 centre = vanishBehind(player, target, 0);
        double dirX = centre.x - target.getX();
        double dirZ = centre.z - target.getZ();
        double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
        if (len > 1.0e-6) {
            double rightX = -dirZ / len;
            double rightZ = dirX / len;
            for (double lateral : new double[]{0.75, -0.75, 1.5, -1.5}) {
                Vec3 candidate = new Vec3(
                        preferred.x + rightX * lateral, preferred.y, preferred.z + rightZ * lateral);
                if (isSpotOpen(player, candidate)) {
                    return candidate;
                }
            }
            for (double shrink : new double[]{0.75, 0.5}) {
                Vec3 candidate = new Vec3(
                        target.getX() + (preferred.x - target.getX()) * shrink,
                        preferred.y,
                        target.getZ() + (preferred.z - target.getZ()) * shrink);
                if (isSpotOpen(player, candidate)) {
                    return candidate;
                }
            }
        }
        if (side != 0) {
            Vec3 mirrored = vanishBehind(player, target, -side);
            if (isSpotOpen(player, mirrored)) {
                return mirrored;
            }
        }
        return player.position();
    }

    private static Vec3 openRingLanding(Entity player, LivingEntity target, int preferred,
                                        int slots, double radius) { return null; }

    /** Land on the target (or {@link XenoServerConfig#chaseStopGap} short), at their height. */
    public static Vec3 chaseLanding(Entity player, LivingEntity target) {
        Vec3 flat = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ());
        if (flat.lengthSqr() < 1.0e-4) {
            flat = new Vec3(0, 0, 1);
        } else {
            flat = flat.normalize();
        }
        double gap = Math.max(0.0, XenoServerConfig.chaseStopGap);
        return new Vec3(
                target.getX() - flat.x * gap,
                target.getY(),
                target.getZ() - flat.z * gap);
    }

    /** Step away from target horizontally; keep player Y for air combat. */
    public static Vec3 backstepDest(Entity player, LivingEntity target) { return null; }

    /** Used by dragon dash only — small lateral nudge if blocked. */
    private static Vec3 findOpenSpot(ServerPlayer player, LivingEntity target, Vec3 preferred) { return null; }

    public static boolean isSpotOpen(Entity player, Vec3 pos) {
        if (player == null || pos == null || player.level() == null) {
            return false;
        }
        var box = player.getBoundingBox().move(
                pos.x - player.getX(),
                pos.y - player.getY(),
                pos.z - player.getZ());
        return player.level().noCollision(player, box);
    }

    static void playItSound(ServerPlayer player, double x, double y, double z, boolean leave) { }

    private static void playHitSound(ServerPlayer player, LivingEntity target, boolean finisher) { }

    private static void playKickHitSound(ServerPlayer player, LivingEntity target, boolean fullCharge) { }

    /** DMZ unarmed pack only — never vanilla strong/crit/sweep. */
    private static void playDmzConnect(ServerPlayer player, LivingEntity target, boolean heavy) { }

    private static SoundEvent dmzHitEvent(ServerPlayer player, boolean heavy) { return null; }

    private static SoundEvent dmzEvent(
            net.neoforged.neoforge.registries.DeferredHolder<SoundEvent, ? extends SoundEvent> holder) { return null; }

    private static void faceTarget(ServerPlayer player, LivingEntity target) { }

    /** Keep GeckoLib attacks aligned with DMZ's synced camera/crosshair yaw. */
    private static void faceBodyToLook(ServerPlayer player) { }

    private static float yawToward(net.minecraft.world.entity.Entity from, LivingEntity target) { return 0.0f; }
}
