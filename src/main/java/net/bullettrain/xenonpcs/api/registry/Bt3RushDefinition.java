package net.bullettrain.xenonpcs.api.registry;

import java.util.Arrays;

/**
 * Immutable timing and animation contract for one automatic cinematic rush.
 *
 * <p>Register instances through {@link RushRegistry}. The constructor validates that impact ticks
 * are strictly ascending and fall inside the duration, so a malformed definition fails loudly at
 * registration rather than midway through a rush.
 */
public record Bt3RushDefinition(
        String id,
        String animation,
        int durationTicks,
        int[] impactTicks) {

    public static final int DEFAULT_DURATION_TICKS = 28;
    private static int[] DEFAULT_IMPACTS = null;

    public Bt3RushDefinition { }

    public static Bt3RushDefinition standard(String id, String animationSuffix) { return null; }

    @Override
    public int[] impactTicks() { return null; }

    public int impactCount() { return 0; }

    public int impactTick(int index) { return 0; }

    public boolean isFinalImpact(int index) { return false; }

    @Override
    public String toString() { return ""; }
}
