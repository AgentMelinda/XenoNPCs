package net.bullettrain.xenonpcs.missile;

/**
 * Physical size tiers for tube-launched entity missiles.
 * {@link #renderScale} drives hitbox/yield; {@link #visualLength} / {@link #visualRadius} are
 * block-space (1 block = 1 m) for the in-world model.
 */
public enum MissileSize {
    SMALL(0.6f, 0.75f, 64, 3.0f, 0.28f),
    MEDIUM(1.0f, 1.0f, 64, 6.0f, 0.40f),
    LARGE(1.6f, 1.5f, 64, 10.0f, 0.45f),
    MEGA(2.4f, 2.0f, 64, 16.0f, 0.80f);

    private static final int STACK = 64;

    private float renderScale;
    private float yieldScale;
    private int stackSize;
    private float visualLength;
    private float visualRadius;

    MissileSize(float renderScale, float yieldScale, int stackSize, float visualLength, float visualRadius) {  }

    public float renderScale() { return 0.0f; }

    public float yieldScale() { return 0.0f; }

    public int stackSize() { return 0; }

    /** Length along the nose axis, in blocks. */
    public float visualLength() { return 0.0f; }

    /** Fuselage half-width, in blocks. */
    public float visualRadius() { return 0.0f; }

    public float hitbox() { return 0.0f; }

    public static int maxStackSize() { return 0; }

    public static MissileSize byOrdinal(int ordinal) { return null; }
}
