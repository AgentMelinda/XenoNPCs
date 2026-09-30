package net.bullettrain.xenonpcs.compat.npc;

import net.bullettrain.xenonpcs.npc.XenoNpcEntity;
import net.bullettrain.xenonpcs.npc.movement.NpcMovementOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;

/** Forge combat movement: native pursuit and smooth turning, with combat teleports disabled. */
public final class NpcCombatMotionPolicy {
    private NpcCombatMotionPolicy() {}

    public static boolean active(LivingEntity npc) {
        return npc instanceof XenoNpcEntity nativeNpc && valid(nativeNpc, nativeNpc.getTarget());
    }

    private static boolean valid(LivingEntity npc, LivingEntity target) {
        return npc != null && target != null && target != npc && target.isAlive() && npc.isAlive();
    }

    public static boolean hold(LivingEntity npc, LivingEntity target) {
        if (!(npc instanceof XenoNpcEntity nativeNpc) || npc.level().isClientSide()) return false;
        if (!valid(npc, target)) {
            NpcMovementOwner.release(npc, NpcMovementOwner.Claim.COMBAT);
            return false;
        }
        if (!NpcCombatRanges.withinMelee(npc, target)) return false;
        nativeNpc.getNavigation().stop();
        NpcSprintSkill.apply(nativeNpc, NpcCombatProfile.readCached(npc), false);
        // Stop approach only when actually within reach. Leave velocity to physical effects.
        return true;
    }

    public static void look(LivingEntity npc, float yaw, float pitch) {
        if (!(npc instanceof XenoNpcEntity)) {
            NpcKiAim.applyLook(npc, yaw, pitch);
            return;
        }
        npc.setYRot(Mth.approachDegrees(npc.yRotO, yaw, 8));
        npc.yBodyRot = Mth.approachDegrees(npc.yBodyRotO, npc.getYRot(), 8);
        npc.yHeadRot = Mth.approachDegrees(npc.yHeadRotO, yaw, 12);
        npc.setXRot(Mth.approachDegrees(npc.xRotO, pitch, 8));
    }
}
