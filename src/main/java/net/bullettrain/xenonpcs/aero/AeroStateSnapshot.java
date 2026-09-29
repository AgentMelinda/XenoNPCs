package net.bullettrain.xenonpcs.aero;

import net.minecraft.core.BlockPos;

import java.util.EnumSet;
import java.util.Set;

/**
 * Immutable wire view of an {@link AeroBus}, sent server → client for the controller GUI.
 *
 * <p>Kept as a plain record with no Minecraft client types so the dedicated server can load
 * it, matching the existing {@code ClientScreens.GuidanceOpenData} convention.
 *
 * <p>Enabled subsystems travel as a bitmask over {@link AeroSubsystem#ordinal()} rather than a
 * list, which keeps the packet fixed-size. That does mean subsystem <b>declaration order is a
 * wire contract</b>: append new entries at the end, never reorder.
 */
public record AeroStateSnapshot(
        BlockPos controllerPos,
        ControllerMode mode,
        int enabledMask,
        double throttle,
        double yawDeg,
        double pitchDeg,
        double rollDeg,
        boolean flightEngaged,
        AeroAutopilotMode autopilotMode,
        int waypointIndex,
        int waypointCount,
        double targetDistance,
        double actualSpeed,
        AeroBus.PowerTier powerTier,
        int storedEnergy,
        int drawFePerTick,
        int linkCount,
        int healthyLinkCount,
        double flap,
        double flapTarget,
        boolean autoFlap,
        boolean airBrake,
        String status
) {
    public static AeroStateSnapshot of(BlockPos pos, AeroBus bus) { return null; }

    public static int maskOf(Set<AeroSubsystem> subsystems) { return 0; }

    public boolean isEnabled(AeroSubsystem subsystem) { return false; }

    public Set<AeroSubsystem> enabledSubsystems() { return java.util.Set.of(); }
}
