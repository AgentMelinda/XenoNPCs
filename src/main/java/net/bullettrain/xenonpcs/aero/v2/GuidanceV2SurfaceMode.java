package net.bullettrain.xenonpcs.aero.v2;

/** How a v2 host turns the hull. */
public enum GuidanceV2SurfaceMode {
    /** Existing mix: thrusters plus control surfaces. */
    THRUST_AND_FLAPS,
    /** Steer only with linked flaps / panels. Thruster force stays zero. */
    FLAPS_ONLY;

    public static GuidanceV2SurfaceMode byName(String name) { return null; }

    public boolean flapsOnly() { return false; }
}
