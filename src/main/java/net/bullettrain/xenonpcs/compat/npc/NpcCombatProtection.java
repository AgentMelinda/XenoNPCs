package net.bullettrain.xenonpcs.compat.npc;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID)
public final class NpcCombatProtection {
    private NpcCombatProtection() {}

    public static boolean isKnockable(Entity entity) {
        NpcCombatProfile profile = profile(entity);
        return profile == null || profile.knockable;
    }

    public static boolean isPunchable(Entity entity) {
        NpcCombatProfile profile = profile(entity);
        return profile == null || profile.punchable;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!isPunchable(event.getTarget())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingHurtEvent event) {
        if (!isPunchable(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKnockback(LivingKnockBackEvent event) {
        if (!isKnockable(event.getEntity())) {
            event.setCanceled(true);
            return;
        }
        NpcCombatProfile profile = profile(event.getEntity());
        if (profile != null && profile.npcKnockbackResistance > 0) {
            event.setStrength(event.getStrength()
                    * (1.0f - NpcCombatProfile.clampNpcResistance(
                    profile.npcKnockbackResistance) / 100.0f));
        }
    }

    private static NpcCombatProfile profile(Entity entity) {
        if (!(entity instanceof LivingEntity) || !NpcCombatProfile.hasProfile(entity)) return null;
        return NpcCombatProfile.readCached(entity);
    }
}
