package net.bullettrain.xenonpcs.combat.targeting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Resolves a target's velocity for lead prediction, in blocks per second, world space.
 *
 * <p>XenoNPCs overlay: XenoPixels also reads a Sable ship's velocity for a target riding a pilot
 * seat; XenoNPCs has neither Sable nor pilot seats, so this is the entity's own motion.
 */
public final class TargetMotionEstimator {

    private TargetMotionEstimator() {
    }

    /** Velocity in blocks/second, world space. Never null; zero when nothing better is known. */
    public static Vec3 velocityOf(ServerLevel level, Entity target) {
        if (target == null) return Vec3.ZERO;
        // getDeltaMovement is blocks/tick; the rest of this system works in blocks/second.
        return target.getDeltaMovement().scale(20.0);
    }
}
