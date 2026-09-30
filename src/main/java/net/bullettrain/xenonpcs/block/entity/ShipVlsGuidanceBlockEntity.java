package net.bullettrain.xenonpcs.block.entity;

import net.bullettrain.xenonpcs.aero.AeroAnimState;
import net.bullettrain.xenonpcs.aero.AeroAutopilotMode;
import net.bullettrain.xenonpcs.aero.AeroBus;
import net.bullettrain.xenonpcs.aero.AeroControlHost;
import net.bullettrain.xenonpcs.aero.AeroLinkManager;
import net.bullettrain.xenonpcs.aero.control.AeroFlightCore;
import net.bullettrain.xenonpcs.aero.v2.GuidanceV2Core;
import net.bullettrain.xenonpcs.aero.v2.GuidanceV2State;
import net.bullettrain.xenonpcs.aero.power.AeroEnergyStorage;
import net.bullettrain.xenonpcs.missile.BallisticCalculator;
import net.bullettrain.xenonpcs.missile.BallisticFlightPlan;
import net.bullettrain.xenonpcs.missile.MissilePhase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Native ballistic guidance computer for XenoPixels.
 *
 * <p><b>Pair thrusters</b> (shift-click thruster, or GUI “Pair Nearby”), set XYZ target,
 * then <b>redstone pulse</b> → ship launches and paired thrusters follow flight phases.
 *
 * <p><b>Primary mode (on a VS2 ship):</b> hull is the ballistic missile via
 * {@link ShipBallisticController}. Paired thrusters provide boost scale + plume.
 */
public class ShipVlsGuidanceBlockEntity extends BlockEntity  implements GeoBlockEntity, AeroControlHost {
    private BlockPos target;
    private boolean wasPowered;
    private int searchRadius = 8;
    private boolean terminalGuidance = true;
    private double boostAccel = 0.35;
    private int boostTicks = 40;
    /**
     * User-facing missile speed / power 1–20 (GUI).
     * Higher = more accel + longer burn — use 12–20 for heavy hulls.
     */
    private int speedLevel = 5;
    /**
     * Loft peak (world Y) to climb to first. {@code 0} = auto from range/physics.
     */
    private int desiredApexY = 0;
    /**
     * Level-cruise altitude (world Y) after loft — fly straight to target here.
     * {@code 0} = same as loft peak (no separate glide-down).
     */
    private int desiredCruiseY = 0;
    /** Flight environment used by both preview calculator and live VS2 controller. */
    private double gravitySi = 0.0;
    private double dragCoefficient = 0.0;
    /** 0 = automatic hull-safe distance; otherwise requested target distance in blocks. */
    private double guidanceStopDistance;
    /** Persistent ship-local block calibration used by the body visualizer/SAS. */
    private @Nullable BlockPos missileBaseBlock;
    private @Nullable BlockPos missileCenterBlock;
    private @Nullable BlockPos missileNoseBlock;
    private BallisticFlightPlan.Settings plannerSettings = null;
    private @Nullable BallisticFlightPlan.Result appliedPlan;
    /** VS2 moving-target designation; -1 means the normal fixed BlockPos target. */
    private long targetShipId = -1L;
    private int movingTargetMissingTicks;
    private int adaptiveReplanCooldown;
    /** 0 = standalone; positive values join a dimension-scoped fleet channel. */
    private int fleetChannel;
    /** Delay between vessels in a fleet salvo. */
    private int salvoIntervalTicks = 10;
    /** 0 = no explosion (default). Tube entity missiles only explode if set &gt; 0. */
    private float warheadYield = 0.0f;
    /** When true (default), firePulse launches the hosting VS ship as the warhead. */
    private boolean shipMissileMode = true;
    /** Also fire entity tubes when launching the ship (submunitions). Default false. */
    private boolean alsoFireTubes = false;
    /** Explicit thruster positions paired to this computer. */
    private Set<BlockPos> pairedThrusters = null;
    private Set<BlockPos> linkedPanels = null;
    private int lastLaunchCount;
    private int lastEtaTicks = -1;
    private double lastPitchDeg;
    private @Nullable BallisticCalculator.Result lastCalculation;
    private String lastStatus = "idle";
    private boolean commandingFlight;
    private int pruneCooldown;
    private MissilePhase lastSyncedPhase;
    private double lastSyncedThrottle = -1;
    /** Cached ship id for flight sync (avoid expensive pos scan every tick). */
    private long cachedShipId = -1L;
    private int shipCacheCooldown;
    /** Redstone edge from neighborChanged — no hasNeighborSignal every tick when idle. */
    private boolean cachedRedstone;
    private int redstoneSafetyPoll;
    /** EMA of actual server tick spacing, used for lag-aware ETA and target lead. */
    private transient long lastServerTickNanos;
    private transient double observedTickSeconds = 0.05;
    /** Aero flight-controller state. Inert while the bus is in {@link ControllerMode#MISSILE}. */
    private AeroBus aeroBus = null;
    private int aeroLinkCooldown;
    /** FE buffer for flight mode. Inert (and undrawn) while the controller is in missile mode. */
    private AeroEnergyStorage aeroEnergy = null;
    /** Cached link survey, refreshed on the same slow cadence the bus counts use. */
    private List<AeroLinkManager.Link> aeroLinks = null;
    /** True while we are actively commanding engines, so shutdown happens exactly once. */
    private boolean aeroCommanding;
    /**
     * Last heading the flight director produced, held while it has none to give. Runtime-only
     * and reset on disengage, like every other piece of live flight state.
     */
    private transient double aeroAutoYawDeg;
    private transient double aeroAutoPitchDeg;
    /** Shared per-tick flight maths; owns its scratch vectors so the tick allocates nothing. */
    private transient AeroFlightCore aeroCore = null;
    private transient GuidanceV2Core guidanceV2Core = null;
    private GuidanceV2State guidanceV2 = null;
    private static final String MANUAL_STATUS = "manual flight";
    private static final String MANUAL_STALL_STATUS = "manual flight — STALL";
    /** Client-only mirrors of runtime bus state, used purely to pick an animation. */
    private AeroBus.PowerTier clientTier = null;
    private boolean clientEngaged;
    /** GeckoLib animation cache; the renderer drives it from {@link #aeroAnimState()}. */
    private AnimatableInstanceCache aeroAnimCache = null;

    private static AeroBus.PowerTier parseTier(String name) { return null; }

    public ShipVlsGuidanceBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SHIP_VLS_GUIDANCE.get(), pos, state); }

    // --- Aero flight controller ------------------------------------------------------

    public AeroBus aeroBus() { return null; }

    public BlockPos hostPos() { return null; }

    /** True while a missile launch is in progress; mode changes are refused during one. */
    public boolean isCommandingFlight() { return false; }

    /** Push controller state to tracking clients. Separate name so intent reads clearly. */
    public void syncAero() { }

    public GuidanceV2State guidanceV2() { return null; }

    /** Re-survey paired devices and publish their health onto the bus. */
    public void refreshAeroLinks() { }

    /**
     * Flight-mode tick. Called from {@link #serverTick()} before the missile idle early-out,
     * and does nothing at all unless an operator has switched the block into flight mode —
     * that gate is what keeps existing missile behavior byte-identical.
     */
    private void aeroTick(ServerLevel sl) { }

    /**
     * Phase 3 flight control: distribute commanded thrust and run attitude stabilization.
     *
     * <p>Gated on flight actually being engaged and powered — a controller that is merely in
     * flight mode must not command engines. On any of those failing, engines are cut and the
     * stabilizer released rather than left latched.
     */
    private void aeroFlightControl(ServerLevel sl) { }

    private Vector3d aeroBodyNose() { return null; }

    /**
     * Roll reference for the hull, i.e. which way is "up" on the ship.
     *
     * <p><b>Body calibration cannot supply this.</b> Base, centre and nose are three points
     * along the hull's axis — collinear by construction, and the default calibration is exactly
     * {@code pos.below()}, {@code pos}, {@code pos.above()}. Projecting {@code centre - base}
     * perpendicular to the nose therefore yields the zero vector, and
     * {@code SableAttitudeMath.orthogonalUp} falls through to an arbitrary axis. That is why a
     * ship could hold pitch and yaw perfectly while sitting at the wrong roll: the roll
     * reference was never derived from the ship at all.
     *
     * <p>The controller block's own {@code FACING} is used instead. It is a genuine ship-local
     * direction, it is stored in the block state, and it is only collinear with the nose if the
     * operator has pointed the controller straight along the hull axis — in which case we fall
     * back to the old calibration-derived vector and then to world up.
     */
    private Vector3d aeroBodyUp(Vector3dc nose) { return null; }

    private List<Vector3d> aeroRoute(AeroAutopilotMode mode) { return java.util.List.of(); }

    /** Exposed for the {@code Capabilities.EnergyStorage.BLOCK} provider. */
    public AeroEnergyStorage aeroEnergy() { return null; }

    public void registerThruster(BlockPos thrusterPos) { }

    public void unregisterThruster(BlockPos thrusterPos) { }

    public int getPairedThrusterCount() { return 0; }

    public List<BlockPos> getPairedThrusters() { return java.util.List.of(); }

    public Set<BlockPos> getLinkedPanels() { return java.util.Set.of(); }

    public boolean linkPanel(BlockPos pos) { return false; }

    public boolean unlinkPanel(BlockPos pos) { return false; }

    /**
     * Pair every thruster in {@link #searchRadius} (same ship preferred) to this computer.
     *
     * <p>The scan itself lives in {@link AeroLinkManager} so the pilot seat, which owns engines
     * the same way, cannot drift from it.
     *
     * @return number of thrusters newly paired
     */
    public int pairNearbyThrusters() { return 0; }

    public boolean pairOneThruster(BlockPos pos) { return false; }

    public boolean unpairOneThruster(BlockPos pos) { return false; }

    public int clearPairedThrusters() { return 0; }

    public @Nullable BlockPos getTarget() { return null; }

    public int getFleetChannel() { return 0; }

    public void setFleetChannel(int channel) { }

    public int getSalvoIntervalTicks() { return 0; }

    public void setSalvoIntervalTicks(int ticks) { }

    public int broadcastFleetTarget() { return 0; }

    public int queueFleetSalvo() { return 0; }

    void applyFleetTarget(BlockPos fleetTarget) { }

    String fleetVesselKey() { return ""; }

    public int getSearchRadius() { return 0; }

    public void setSearchRadius(int radius) { }

    public boolean isTerminalGuidance() { return false; }

    public void setTerminalGuidance(boolean terminalGuidance) { }

    public double getBoostAccel() { return 0.0; }

    public void setBoostAccel(double boostAccel) { }

    public int getBoostTicks() { return 0; }

    public void setBoostTicks(int boostTicks) { }

    public int getSpeedLevel() { return 0; }

    /** Loft peak world Y (climb to), or 0 for auto. */
    public int getDesiredApexY() { return 0; }

    /**
     * Set loft peak (world Y) to climb to before pitch-over / cruise.
     * @param y 0 = automatic loft; else absolute world Y (clamped 16k)
     */
    public void setDesiredApexY(int y) { }

    /** Level-cruise world Y (fly straight to target), or 0 = same as loft. */
    public int getDesiredCruiseY() { return 0; }

    public double getGravitySi() { return 0.0; }

    public double getDragCoefficient() { return 0.0; }

    public double getGuidanceStopDistance() { return 0.0; }

    public void setGuidanceStopDistance(double blocks) { }

    public BlockPos getMissileBaseBlock() { return null; }

    public BlockPos getMissileCenterBlock() { return null; }

    public BlockPos getMissileNoseBlock() { return null; }

    public enum BodyCalibrationResult {
        APPLIED("Missile body calibration applied"),
        INVALID_POSITIONS("Base, center, and nose must be three different blocks"),
        HOST_SHIP_MISSING("The guidance computer is not on a loaded VS ship"),
        BLOCK_UNLOADED("One or more selected blocks are not loaded"),
        BLOCK_EMPTY("One or more selected positions do not contain a block"),
        WRONG_SHIP("All three blocks must belong to this guidance computer's ship");

        private String message;

        BodyCalibrationResult(String message) {  }

        public String message() { return ""; }
    }

    /** Applies three occupied blocks on this same VS ship as body calibration. */
    public BodyCalibrationResult applyMissileBodyCalibration(BlockPos base, BlockPos center, BlockPos nose) { return null; }

    public boolean setMissileBodyCalibration(BlockPos base, BlockPos center, BlockPos nose) { return false; }

    private static String shortPos(BlockPos p) { return ""; }

    public BallisticFlightPlan.Settings getPlannerSettings() { return null; }

    public void setPlannerSettings(BallisticFlightPlan.Settings settings) { }

    public @Nullable BallisticFlightPlan.Result calculateAdvancedPlan(int revision) { return null; }

    public double getObservedTickSeconds() { return 0.0; }

    public void applyAdvancedPlan(BallisticFlightPlan.Result result) { }

    public void setFlightPhysics(double gravitySi, double dragCoefficient) { }

    /**
     * Set cruise altitude after loft. Climb to loft first, then hold this Y toward target.
     * @param y 0 = cruise at loft peak; else absolute world Y
     */
    public void setDesiredCruiseY(int y) { }

    /**
     * Set missile speed/power 1–20 and derive boost accel + burn time.
     * Heavy ships: try 12–20 (GUI Heavy / MAX) if the craft barely climbs.
     * <ul>
     *   <li>1 → accel 0.20, burn 30t</li>
     *   <li>5 (default) → ~0.68 / 70t</li>
     *   <li>14 (Heavy) → ~1.76 / 160t</li>
     *   <li>20 (MAX) → ~2.48 / 220t</li>
     * </ul>
     */
    public void setSpeedLevel(int level) { }

    public float getWarheadYield() { return 0.0f; }

    public void setWarheadYield(float warheadYield) { }

    public boolean isShipMissileMode() { return false; }

    public void setShipMissileMode(boolean shipMissileMode) { }

    public boolean isAlsoFireTubes() { return false; }

    public void setAlsoFireTubes(boolean alsoFireTubes) { }

    public int getLastLaunchCount() { return 0; }

    public int getLastEtaTicks() { return 0; }

    public double getLastPitchDeg() { return 0.0; }

    public @Nullable BallisticCalculator.Result getLastCalculation() { return null; }

    public String getLastStatus() { return ""; }

    public void clearTarget() { }

    public void setTargetWorld(int x, int y, int z) { }

    public void setMovingTarget(long shipId, BlockPos lastKnownPosition) { }

    public long getTargetShipId() { return 0L; }

    /**
     * Horizontal range from computer (world) to current target, or -1 if no target.
     */
    public double horizontalRangeToTarget() { return 0.0; }

    /** @return null if in range; otherwise a short error string */
    public @Nullable String rangeLimitMessage() { return ""; }

    private Vec3 launchWorldPos() { return null; }

    public Vec3 getLaunchWorldPosition() { return null; }

    public void setTargetFromLook(Player player) { }

    /** From block neighborChanged — preferred redstone path. */
    public void onRedstoneChanged(boolean powered) { }

    public void serverTick() { }

    private void tickAdaptiveGuidance(ServerLevel sl) { }

    /** Debug-friendly ship detect for status strings / GUI. */
    public String describeShipLink(ServerLevel sl) { return ""; }

    /**
     * Redstone / CC fire.
     * <ol>
     *   <li>If guidance sits on a VS ship and {@link #shipMissileMode}: launch <b>the ship</b>
     *       as a real ballistic missile.</li>
     *   <li>Else (or also if {@link #alsoFireTubes}): launch nearby entity tubes.</li>
     * </ol>
     *
     * @return number of munitions started (1 for ship launch + tube count)
     */
    public int firePulse(ServerLevel sl) { return 0; }

    /** Abort an in-flight ship ballistic if this computer is on that hull. */
    public boolean abortShipFlight() { return false; }

    private boolean tryLaunchShipMissile(ServerLevel sl) { return false; }

    private void syncThrustersToFlight(ServerLevel sl) { }

    private void engageThrusters(List<BlockPos> thrusters, double throttle) { }

    private void releaseThrusters() { }

    private void pruneDeadPairs() { }

    /** Resolve previously paired native or external engines without another volume scan. */
    private List<BlockPos> resolvePairedThrusterPositions() { return java.util.List.of(); }

    private Vec3 resolveLoftDirection(List<BlockPos> thrusters) { return null; }

    private int fireTubes(ServerLevel sl) { return 0; }

    /** Writes speed / silo clearance / launch Y onto every tube in the search radius. */
    public int applyNearbyTubeSettings(Integer speedLevel, Integer siloClearance, Integer launchWorldY) { return 0; }

    /** Solve loft for UI / CC without firing. */
    public void recomputeSolution() { }

    private void sync() { }

    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) { }

    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) { }

    public void setRemoved() { }

    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) { return null; }

    /** Client-side animation state, driven entirely by synced values. */
    public AeroAnimState aeroAnimState() { return null; }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { }

    public AnimatableInstanceCache getAnimatableInstanceCache() { return null; }

    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() { return null; }
}
