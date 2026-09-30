package net.bullettrain.xenonpcs.aero;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import java.util.EnumSet;
import java.util.Set;

/**
 * Avionics bus — the single authoritative state snapshot for one controller.
 *
 * <p>Every module reads from here and only {@link AeroActionDispatcher} writes to it, so the
 * GUI, the physical panel, and any peripheral observe the same values. The bus lives on the
 * server; clients receive a copy through the state sync packet.
 *
 * <p><b>Persistence rule:</b> operator configuration (mode, enabled subsystems) is saved;
 * live flight state (throttle, attitude setpoints, engagement) is not. This matches the
 * existing contract in {@code ShipVlsGuidanceBlockEntity}, which never resumes a flight after
 * a chunk reload, and in {@code ShipThrusterBlockEntity}, whose guidance ownership is
 * deliberately runtime-only. Persisting throttle would make a parked ship thrust on world
 * load.
 */
public final class AeroBus {
    /** Degradation tiers as available power falls. */
    public enum PowerTier {
        /** Full authority, everything the operator enabled is running. */
        NOMINAL,
        /** Non-essential subsystems shed; flight authority intact. */
        REDUCED,
        /** Flight authority limited; only stop/stabilize commands honored. */
        CRITICAL,
        /** No usable power. Emergency stop only. */
        OFFLINE
    }

    private ControllerMode mode = null;
    private Set<AeroSubsystem> enabled = null;

    private double throttle;
    private double yawDeg;
    private double pitchDeg;
    private double rollDeg;
    /** Raw pitch/roll/yaw stick position, -1..1 — only meaningful in keyboard mode; see
     * {@link #mouseAim} and {@link net.bullettrain.xenonpcs.aero.control.AeroFlightCore#tick}. */
    private double pitchStick;
    private double rollStick;
    private double yawStick;
    /** True in mouse-aim mode (attitude-hold PD stabilizer drives rotation, as before), false in
     * keyboard mode (sticks deflect panels directly and rate-only damping runs instead). Defaults
     * false — keyboard is the default flight mode — but every non-seat attitude source (GUI,
     * autopilot, CC, panel) calls the 3-arg {@link #setAttitude(double, double, double)}, which
     * always passes {@code true} explicitly, so this default only affects seat flight before its
     * first input packet arrives; from then on the seat's own {@code mouseAim} (driven by
     * {@code XenoClientConfig.flightMouseAim}, also default false) overwrites it every tick. */
    private boolean mouseAim = false;
    private boolean flightEngaged;
    private AeroAutopilotMode autopilotMode = null;
    private int waypointIndex;
    private int waypointCount;
    private double targetDistance;
    private double actualSpeed;

    /** Current flap extension, 0..1 (live state — not persisted, see class javadoc). */
    private double flap;
    /** Commanded flap extension, 0..1. {@link #flap} eases toward this at {@code AeroConfig.flapSpeedPerSec}. */
    private double flapTarget;
    /** When true, the flight tick overrides {@link #flapTarget} from speed/approach. */
    private boolean autoFlap;
    /** Live air-brake state; drives BRAKE-role panels and zeroes effective thrust while held. */
    private boolean airBrakeEngaged;

    private PowerTier powerTier = null;
    private int storedEnergy;
    private int drawFePerTick;

    private int linkCount;
    private int healthyLinkCount;
    private String status = "idle";

    public ControllerMode mode() { return null; }

    void setMode(ControllerMode mode) { }

    public boolean isEnabled(AeroSubsystem subsystem) { return false; }

    void setEnabled(AeroSubsystem subsystem, boolean on) { }

    public Set<AeroSubsystem> enabledSubsystems() { return java.util.Set.of(); }

    public double throttle() { return 0.0; }

    void setThrottle(double value) { }

    public double yawDeg() { return 0.0; }

    public double pitchDeg() { return 0.0; }

    public double rollDeg() { return 0.0; }

    public double pitchStick() { return 0.0; }

    public double rollStick() { return 0.0; }

    public double yawStick() { return 0.0; }

    public boolean mouseAim() { return false; }

    void setAttitude(double yaw, double pitch, double roll) { }

    void setAttitude(double yaw, double pitch, double roll,
                     double pitchStick, double rollStick, double yawStick, boolean mouseAim) { }

    private static double wrapDegrees(double value) { return 0.0; }

    public boolean isFlightEngaged() { return false; }

    void setFlightEngaged(boolean engaged) { }

    public AeroAutopilotMode autopilotMode() { return null; }

    void setAutopilotMode(AeroAutopilotMode mode) { }

    public int waypointIndex() { return 0; }
    public int waypointCount() { return 0; }
    public double targetDistance() { return 0.0; }
    public double actualSpeed() { return 0.0; }

    public double flap() { return 0.0; }
    public double flapTarget() { return 0.0; }
    public boolean autoFlap() { return false; }

    void setFlap(double value) { }

    void setFlapTarget(double value) { }

    void setAutoFlap(boolean on) { }

    public boolean airBrakeEngaged() { return false; }

    void setAirBrakeEngaged(boolean engaged) { }

    /**
     * Advance live flap travel one server tick.
     *
     * <p>Public for the same reason {@link #reportAutopilot} is: the authoritative flight tick
     * lives outside this package and has to move flaps, while operator <i>intent</i> still only
     * ever arrives through {@link AeroActionDispatcher}. Flaps ease rather than snap, because a
     * step change in lift and drag is both unrealistic and a jolt to the physics body.
     *
     * <p>With auto-flap on, the setpoint is derived from airspeed instead of the operator's:
     * fully out at or below {@link AeroConfig#autoFlapExtendSpeed}, fully in at or above
     * {@link AeroConfig#autoFlapRetractSpeed}, smoothly interpolated between.
     *
     * @param deltaSeconds observed server tick length
     * @param airspeed     current speed in blocks/s; ignored unless auto-flap is on
     */
    public void advanceFlaps(double deltaSeconds, double airspeed) { }

    /** Hermite smoothstep, 0 below {@code e0}, 1 above {@code e1}. */
    private static double smoothstep(double e0, double e1, double x) { return 0.0; }

    /** Server flight-director telemetry; does not alter operator configuration. */
    public void reportAutopilot(int index, int count, double distance, double speed, String message) { }

    /** Safety transition used by the authoritative server controller on lost ship/power. */
    public void disengageRuntime(String reason) { }

    public PowerTier powerTier() { return null; }

    void setPowerTier(PowerTier tier) { }

    public int storedEnergy() { return 0; }

    void setStoredEnergy(int stored) { }

    public int drawFePerTick() { return 0; }

    void setDrawFePerTick(int draw) { }

    public int linkCount() { return 0; }

    public int healthyLinkCount() { return 0; }

    void setLinkCounts(int total, int healthy) { }

    public String status() { return ""; }

    void setStatus(String status) { }

    /** Zero every live flight command. Does not touch operator configuration. */
    void resetFlightState() { }

    /** Saves operator configuration only — see the persistence rule in the class javadoc. */
    public CompoundTag save() { return null; }

    public void load(CompoundTag tag) { }
}
