package net.bullettrain.xenonpcs.compat;

/** Java 21 clamp overloads for the Java 17 Forge distribution. */
public final class Java17Math {
    private Java17Math() {}
    public static int clamp(long value, int min, int max) {
        if (min > max) throw new IllegalArgumentException("min > max");
        return (int) Math.max(min, Math.min(max, value));
    }
    public static long clamp(long value, long min, long max) {
        if (min > max) throw new IllegalArgumentException("min > max");
        return Math.max(min, Math.min(max, value));
    }
    public static float clamp(float value, float min, float max) {
        if (!(min <= max)) throw new IllegalArgumentException("invalid bounds");
        return Math.max(min, Math.min(max, value));
    }
    public static double clamp(double value, double min, double max) {
        if (!(min <= max)) throw new IllegalArgumentException("invalid bounds");
        return Math.max(min, Math.min(max, value));
    }
}
