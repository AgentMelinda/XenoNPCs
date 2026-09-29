package net.bullettrain.xenonpcs.aero.v2;

import net.bullettrain.xenonpcs.aero.control.AeroFlightCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3dc;

import java.util.List;
import java.util.Set;

/**
 * V2 flight tick. Reuses the verified v1 force hooks (panel deflection, Sable lift,
 * {@code VectorMixer}) but can force throttle to zero for {@link GuidanceV2SurfaceMode#FLAPS_ONLY}.
 */
public final class GuidanceV2Core {
    private AeroFlightCore core = null;

    /** True only when v2 is allowed to write thruster force this tick. */
    public static boolean requestsThrusterForce(GuidanceV2SurfaceMode mode, double throttle) { return false; }

    public static double appliedThrottle(GuidanceV2SurfaceMode mode, double throttle) { return 0.0; }
}
