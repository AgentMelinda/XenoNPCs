package net.bullettrain.xenonpcs.block.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

/**
 * Ship thruster BE — hot-path optimized for fleets of idle thrusters on VS hulls.
 * <ul>
 *   <li>Idle: early-out, no world queries (redstone from neighborChanged)</li>
 *   <li>Active: ship resolve by id, phys force every N ticks, client-only plumes</li>
 *   <li>Guidance-owned: visual only (ballistic uses CoM thrust)</li>
 * </ul>
 */
public class ShipThrusterBlockEntity extends BlockEntity {
    private static final double LEGACY_DEFAULT_MAX_FORCE = 80_000.0;
    /** A second, more recent legacy value — see the migration in {@link #loadAdditional}. */
    private static final double PREVIOUS_DEFAULT_MAX_FORCE = 1_200_000.0;
    /**
     * {@link dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle#applyImpulseAtPoint} is a
     * real physics impulse (&Delta;v = impulse / mass, standard rigid-body integration) — it is
     * not a "how strong does this feel" slider. This project's own block masses
     * ({@code datapacks/xeno_ship_masses/}) run 0.6&ndash;3.5 per block, so a modest few-hundred-
     * block ship has a total mass in the low hundreds. The previous default of 1,200,000 (and the
     * 80,000 one before it) produced a velocity change of hundreds to thousands of blocks/s in a
     * single tick against a ship that light, which is what tore ships apart. This value is sized
     * from that same mass range against a target acceleration of roughly 10&ndash;15 blocks/s&sup2;
     * (mass &times; accel &asymp; 1,000&ndash;4,500) — a reasoned correction grounded in this
     * project's own numbers, not a measured/tuned final value; still expect to adjust it after
     * flying a real ship.
     */
    public static final double DEFAULT_MAX_FORCE = 3_000.0;

    private double power;
    private double ccPower = -1; // -1 = redstone; else 0..1
    private double maxForce = 0.0;
    private boolean soundPlayed;
    private @Nullable BlockPos pairedGuidance;
    private boolean guidanceOwned;
    private boolean forceCleared = true;
    private String cachedKey;
    private long cachedShipId = 0L;
    /** Ship whose transient attachment currently owns this thruster's force entry. */
    private long registeredForceShipId = -1L;
    private int shipLookupCooldown;
    private double lastPushedPower = -1;
    private int lastPushedFx, lastPushedFy, lastPushedFz;
    /** From neighborChanged — never queried on the idle path. 0..15, vanilla redstone strength. */
    private int cachedRedstoneStrength;
    private static final int PHYS_PUSH_INTERVAL = 3;

    public ShipThrusterBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SHIP_THRUSTER.get(), pos, state); }

    public @Nullable BlockPos getPairedGuidance() { return null; }

    public boolean isPaired() { return false; }

    public boolean isGuidanceOwned() { return false; }

    public void setPairedGuidance(@Nullable BlockPos guidancePos) { }

    public void setGuidanceOwned(boolean owned) { }

    /** Visual throttle during ballistic — no setChanged / no phys force. */
    public void setGuidanceThrottle(double throttle) { }

    public void forceShutdown() { }

    private void clearPhysicsForce() { }

    private void forcePoweredState(boolean lit) { }

    public double getPower() { return 0.0; }

    public double getMaxForce() { return 0.0; }

    public void setMaxForce(double force) { }

    public boolean isActive() { return false; }

    public void setCcPower(double value) { }

    public double getCcPower() { return 0.0; }

    public float cyclePreset() { return 0.0f; }

    /** Called from block neighborChanged — only place that reads redstone. */
    public void onRedstoneChanged(int strength) { }

    private boolean redstoneInited;

    public void onLoad() { }

    public void tick() { }

    private void serverPhysics() { }

    /**
     * Plume placement, corrected for a thruster living on a moving/rotated Sable ship.
     *
     * <p>{@code worldPosition} is the block's position in the ship's own local/model space, not
     * the actual place it renders in the world — the previous version of this method spawned
     * particles straight at those raw coordinates, which is only correct for a ship sitting
     * exactly at the world origin with no rotation. Any ship that has actually moved or turned
     * had its exhaust plume appear in the wrong place entirely. Fixed the same way this file
     * already resolves a ship server-side ({@code resolveShipCached}/{@code logicalPose()} in
     * {@link #playerNear}), using the client-side equivalent verified via {@code javap} against
     * the real Sable companion jar: {@link SableCompanion#getContainingClient(net.minecraft.world.level.block.entity.BlockEntity)}
     * returns a {@link ClientSubLevelAccess}, whose {@link ClientSubLevelAccess#renderPose()}
     * transforms local position/direction into the ship's current rendered world pose.
     *
     * <p>Particle count now scales with {@link #power} instead of a fixed one-particle cadence,
     * so the plume reads as a denser stream near full throttle and a faint wisp near idle —
     * learned from studying {@code create-propulsion-simulated}'s thruster (MIT-licensed,
     * verified) density-scaling idea, but built on this project's own vanilla
     * {@link ParticleTypes#FLAME}/{@link ParticleTypes#SMOKE} rather than that mod's custom
     * particle types/textures, at the user's explicit request. Inheriting the ship's own velocity
     * into the particle (which that mod also does) was not attempted here — that data is not
     * exposed on the public {@link ClientSubLevelAccess} interface this project already uses, and
     * reaching past it into Sable's internal client sub-level implementation would mean guessing
     * at an unverified API rather than using one actually confirmed to exist.
     */
    /**
     * The Effekseer rocket plume (slot {@code ship_thruster}), re-sent every 8 ticks while lit and
     * staggered per block so a bank of thrusters does not pulse in step. Placed at the nozzle's
     * real world pose: on a Sable ship the block's position is ship-local, so it goes through the
     * ship's logical pose (the same transform {@link #playerNear} uses), and so does the exhaust
     * direction.
     */
    private void serverPlume(ServerLevel sl) { }

    private void clientPlume() { }

    private boolean playerNear(ServerLevel sl, double range) { return false; }

    private String thrusterKey() { return ""; }

    public void setRemoved() { }

    private void sync() { }

    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) { }

    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) { }

    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) { return null; }

    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() { return null; }
}
