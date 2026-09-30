package net.bullettrain.xenonpcs.aero.seat;

import net.minecraft.util.Mth;

/**
 * The pilot's last commanded control positions, held server-side for one seat.
 *
 * <p>Everything here is what the client <i>asked</i> for, already clamped. It is not authority:
 * the values are handed to {@link net.bullettrain.xenonpcs.aero.AeroActionDispatcher},
 * which independently enforces flight mode, engagement and power tier and clamps again, and the
 * bus clamps a third time.
 *
 * <p>The staleness rule matters more than it looks. A pilot who crashes, times out or is
 * teleported away stops sending packets while the last throttle command is still latched on the
 * bus, and a ship at full thrust with nobody flying it does not stop on its own. So input that
 * has not been refreshed within {@link #STALE_TICKS} is treated as released rather than held.
 */
public final class AeroSeatInput {

    /** Roughly half a second at 20 tps: long enough to ride out a hiccup, short enough to be safe. */
    public static final int STALE_TICKS = 10;

    private double throttle;
    private double yawDeg;
    private double pitchDeg;
    private double rollDeg;
    private double pitchStick;
    private double rollStick;
    private double yawStick;
    private boolean mouseAim = false;
    private double flap;
    private boolean airBrake;
    private boolean autoLevel;
    private boolean flapCommanded;
    private int ticksSinceUpdate = 0;

    public double throttle() { return 0.0; }

    public double yawDeg() { return 0.0; }

    public double pitchDeg() { return 0.0; }

    public double rollDeg() { return 0.0; }

    public double pitchStick() { return 0.0; }

    public double rollStick() { return 0.0; }

    public double yawStick() { return 0.0; }

    public boolean mouseAim() { return false; }

    public double flap() { return 0.0; }

    public boolean airBrake() { return false; }

    public boolean autoLevel() { return false; }

    /** True only while the pilot is actively moving the flap control. */
    public boolean flapCommanded() { return false; }

    /** True once the pilot has stopped sending; the seat then commands idle. */
    public boolean isStale() { return false; }

    /** Accept a validated control frame. Non-finite values are refused, not clamped to zero. */
    public boolean accept(double throttle, double yawDeg, double pitchDeg, double rollDeg,
                          double pitchStick, double rollStick, double yawStick, boolean mouseAim,
                          double flap, boolean airBrake, boolean autoLevel, boolean flapCommanded) { return false; }

    /** Called once per server tick by the seat. */
    public void tick() { }

    /** Drop everything the pilot was holding — dismount, death, logout. */
    public void release() { }
}
