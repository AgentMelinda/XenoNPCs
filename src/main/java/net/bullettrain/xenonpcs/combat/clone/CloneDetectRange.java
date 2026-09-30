package net.bullettrain.xenonpcs.combat.clone;

/**
 * How far copies notice a fight. Multi-form uses it as the leash/hostile scan;
 * Zanzoken uses it as the radius that nearby AI can be fooled onto afterimages.
 *
 * <p>Minecraft-free so the clamp and in-range tests stay unit-tested.
 */
public final class CloneDetectRange {
    public static double DEFAULT = 0.0;
    public static final double MIN = 1.0;
    public static final double MAX = 128.0;

    private CloneDetectRange() {  }

    public static double clamp(double range) { return 0.0; }

    public static boolean within(double distance, double range) { return false; }

    public static boolean withinSqr(double distSqr, double range) { return false; }
}
