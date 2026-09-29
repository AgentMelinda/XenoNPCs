package net.bullettrain.xenonpcs.aero;

/**
 * Animation state of a flight controller, derived from {@link AeroBus}.
 *
 * <p>The renderer plays exactly one of these and never decides for itself. The design notes
 * record animations repeatedly breaking after patches; the cause is usually renderer-local
 * guesswork drifting out of step with server state. Deriving the state from synced values in
 * one place means an animation can only ever be wrong if the state itself is.
 *
 * <p>Names match the animation ids in
 * {@code assets/xenonpcs/animations/flight_controller.animation.json}.
 */
public enum AeroAnimState {
    /** Missile mode, or flight mode idle and unengaged. */
    IDLE("idle"),
    /** Powered and engaging — a short one-shot before {@link #ACTIVE}. */
    POWERING("powering"),
    /** Flight engaged with nominal power. */
    ACTIVE("active"),
    /** Running with subsystems shed. */
    DEGRADED("degraded"),
    /** No usable power. */
    FAULT("fault");

    private String animationId;

    AeroAnimState(String animationId) {  }

    public String animationId() { return ""; }

    public boolean loops() { return false; }

    /** Single source of truth for which animation a controller should be playing. */
    public static AeroAnimState of(ControllerMode mode, AeroBus.PowerTier tier, boolean engaged) { return null; }
}
