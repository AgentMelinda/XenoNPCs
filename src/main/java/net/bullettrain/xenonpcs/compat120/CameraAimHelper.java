package net.bullettrain.xenonpcs.compat120;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * DragonMineZ 1.21.1's {@code com.dragonminez.common.compat.CameraAimHelper}, which its 1.20.1
 * build does not have. Same persistent-data keys, freshness window and angle maths, read from the
 * 1.21.1 class's bytecode, so a stored aim means the same thing on both versions.
 */
public final class CameraAimHelper {
    private static final String AIM_X = "dmz_camera_aim_x";
    private static final String AIM_Y = "dmz_camera_aim_y";
    private static final String AIM_Z = "dmz_camera_aim_z";
    private static final String AIM_TIME = "dmz_camera_aim_time";
    private static final long AIM_FRESH_TICKS = 3L;
    private static final double VERTICAL_EPSILON = 1.0E-6;

    private CameraAimHelper() {}

    public static void store(LivingEntity entity, Vec3 dir) {
        if (!isValid(dir)) return;
        Vec3 n = dir.normalize();
        CompoundTag tag = entity.getPersistentData();
        tag.putDouble(AIM_X, n.x);
        tag.putDouble(AIM_Y, n.y);
        tag.putDouble(AIM_Z, n.z);
        tag.putLong(AIM_TIME, entity.level().getGameTime());
    }

    public static Vec3 resolve(LivingEntity entity) {
        CompoundTag tag = entity.getPersistentData();
        if (entity.level().getGameTime() - tag.getLong(AIM_TIME) <= AIM_FRESH_TICKS) {
            Vec3 stored = new Vec3(tag.getDouble(AIM_X), tag.getDouble(AIM_Y), tag.getDouble(AIM_Z));
            if (isValid(stored)) return stored.normalize();
        }
        return entity.getLookAngle().normalize();
    }

    public static float yaw(Vec3 dir) {
        return (float) (Mth.atan2(dir.z, dir.x) * (180.0 / Math.PI) - 90.0);
    }

    public static float yaw(LivingEntity entity, Vec3 dir) {
        double horizontal = dir.x * dir.x + dir.z * dir.z;
        return horizontal < VERTICAL_EPSILON ? entity.getYRot() : yaw(dir);
    }

    public static float pitch(Vec3 dir) {
        double horizontal = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
        return (float) -(Mth.atan2(dir.y, horizontal) * (180.0 / Math.PI));
    }

    private static boolean isValid(Vec3 v) {
        return v != null && Double.isFinite(v.x) && Double.isFinite(v.y) && Double.isFinite(v.z)
                && v.lengthSqr() > VERTICAL_EPSILON;
    }
}
