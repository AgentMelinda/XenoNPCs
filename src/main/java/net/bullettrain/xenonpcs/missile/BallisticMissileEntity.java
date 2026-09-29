package net.bullettrain.xenonpcs.missile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;
import java.util.UUID;

/**
 * Lightweight server-authoritative ballistic missile (entity, not a VS mini-ship).
 * Phases: eject → boost → coast → terminal guidance → impact.
 */
public class BallisticMissileEntity extends Entity {
    private static EntityDataAccessor<Integer> DATA_PHASE = null;
    /** True while the server's Effekseer thruster plays, so the client skips its vanilla flame. */
    private static EntityDataAccessor<Boolean> DATA_EFFEK_TRAIL = null;
    private static EntityDataAccessor<Integer> DATA_SIZE = null;

    private static final double TERMINAL_ACQUIRE_RANGE = 48.0;
    private static final double TERMINAL_MAX_TURN = 0.12;
    private static final double TERMINAL_GRAVITY_SCALE = 0.85;
    private static final double TERMINAL_MAX_LEAD_TICKS = 60.0;
    private static int MIN_LIFETIME = 0;

    private double targetX, targetY, targetZ;
    private boolean hasTarget;
    private boolean terminalEnabled = true;
    private double boostAccel = 0.35;
    private double gravitySi = 0.0;
    private double dragCoefficient = 0.0;
    private int maxLifetime = 0;
    private double terminalAcquireRange = 0.0;
    private int boostTicksLeft = 40;
    private int phaseAge;
    private float explosionPower = 4.0f;
    private UUID ownerId;
    private Vec3 loftDir = null;
    private Vec3 ejectOrigin = null;
    private double siloClearance = 0.0;
    private int launchWorldY;
    private int apexY;
    private int cruiseY;
    private ItemStack warhead = null;
    private MissileSize size = null;
    /** Silo/tube rounds fly the planner's corridor after the rail instead of an open-loop burn. */
    private MissileGuidance.Corridor corridor;
    /** Guidance V3 after the rail (taken from /xenoguidance at launch), or null. */
    private net.bullettrain.xenonpcs.missile.v3.TubeV3 v3;

    public static final class LaunchConfig {
        public Vec3 spawn = null;
        public Vec3 target = null;
        public Vec3 loft = null;
        public double boostAccel = 0.35;
        public int boostTicks = 40;
        public boolean terminal = true;
        public float yield = 0.0f;
        public UUID owner;
        public double gravitySi = 0.0;
        public double dragCoefficient = 0.0;
        public int apexY;
        public int cruiseY;
        public double siloClearance = 0.0;
        public int launchWorldY;
        public MissileSize size = null;
        public ItemStack warhead = null;
        public Vec3 ejectOrigin;
        /**
         * Fly the planned corridor (climb, then glide to target) after the rail. {@code loft} is
         * then only the rail axis. Without it a silo round burned straight up and fell back.
         */
        public boolean guided;
    }

    public BallisticMissileEntity(EntityType<? extends BallisticMissileEntity> type, Level level) { super(type, level); }

    public static BallisticMissileEntity create(ServerLevel level, Vec3 spawn, Vec3 target,
                                                Vec3 loftDirection, double boostAccel, int boostTicks,
                                                boolean terminal, float yield, UUID owner) { return null; }

    public static BallisticMissileEntity create(ServerLevel level, Vec3 spawn, Vec3 target,
                                                Vec3 loftDirection, double boostAccel, int boostTicks,
                                                boolean terminal, float yield, UUID owner,
                                                double gravitySi, double dragCoefficient) { return null; }

    public static BallisticMissileEntity create(ServerLevel level, LaunchConfig config) { return null; }

    public void setTarget(double x, double y, double z) { }

    public MissilePhase getPhase() { return null; }

    public MissileSize getMissileSize() { return null; }

    private void setPhase(MissilePhase phase) { }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }

    @Override
    public EntityDimensions getDimensions(Pose pose) { return null; }

    @Override
    public AABB getBoundingBoxForCulling() { return null; }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) { return false; }

    @Override
    public void tick() { }

    private void beginBoost(Vec3 pos) { }

    private Vec3 cruiseTowardTarget(Vec3 pos, Vec3 vel) { return null; }

    private static Vec3 applyDrag(Vec3 velocity, double dragCoefficient, double altitude) { return null; }

    private static Vec3 steer(Vec3 current, Vec3 desired, double maxTurn) { return null; }

    private void clientTrail(MissilePhase phase) { }

    private void detonate() { }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) { }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) { }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) { return null; }

    @Override
    protected boolean canAddPassenger(Entity passenger) { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean isAttackable() { return false; }
}
