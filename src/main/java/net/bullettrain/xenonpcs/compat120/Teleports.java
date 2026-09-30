package net.bullettrain.xenonpcs.compat120;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * 1.21.1's {@code new DimensionTransition(level, pos, Vec3.ZERO, yaw, pitch, DO_NOTHING)} as a
 * Forge 1.20.1 teleporter: arrive exactly at {@code pos}, facing that way, standing still, with no
 * portal built.
 */
public final class Teleports {
    private Teleports() {}

    public static ITeleporter to(Vec3 pos, float yaw, float pitch) {
        return new ITeleporter() {
            @Override
            public PortalInfo getPortalInfo(Entity entity, ServerLevel destination,
                                            Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                return new PortalInfo(pos, Vec3.ZERO, yaw, pitch);
            }

            @Override
            public boolean playTeleportSound(net.minecraft.server.level.ServerPlayer player, ServerLevel from,
                                             ServerLevel to) {
                return false;
            }
        };
    }
}
