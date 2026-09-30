package net.bullettrain.xenonpcs.missile.v3;

import net.bullettrain.xenonpcs.missile.MissilePhase;
import net.minecraft.world.phys.Vec3;

/**
 * Guidance V3 for tube (entity) missiles after they leave the silo. Tube missiles are kinematic -
 * the entity sets its own velocity - so each tick is: V3 command + gravity + drag, straight into
 * the velocity, from the missile's current position.
 *
 * <p>A cruise altitude on the guidance computer selects {@link GuidanceV3.Mode#GUIDED} (hold that
 * height, then dive); otherwise the round flies {@link GuidanceV3.Mode#BALLISTIC} with coast correction.
 */
public final class TubeV3 {
    private GuidanceV3.Params params;
    private double gravity;
    private double drag;
    private GuidanceV3.Phase phase = null;
    private int phaseTicks;
    private int fuelTicks;

    private TubeV3(GuidanceV3.Params params, double gravity, double drag, int fuelTicks) {  }

    public static TubeV3 plan(Vec3 launch, Vec3 target, double boostAccel, int boostTicks, double gravitySi,
                              double drag, double apexY, double cruiseY, boolean terminal, double clearance,
                              Vec3 railAxis) { return null; }

    /** Rebuilds a saved flight: same apex, phase and fuel left. */
    public static TubeV3 restore(Vec3 launch, Vec3 target, double boostAccel, int fuelLeft, double gravitySi,
                                 double drag, double apexY, double cruiseY, boolean terminal, double clearance,
                                 Vec3 railAxis, int phaseOrdinal) { return null; }

    /** The next velocity (blocks/tick) for a round at {@code pos} moving at {@code vel}. */
    public Vec3 step(Vec3 pos, Vec3 vel, Vec3 target) { return null; }

    public MissilePhase phase() { return null; }

    public int phaseOrdinal() { return 0; }

    public int fuelTicks() { return 0; }

    public double apexY() { return 0.0; }

    public boolean reachable() { return false; }
}
