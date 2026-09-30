package net.bullettrain.xenonpcs.mixin.common;

import com.dragonminez.common.stats.character.Stats;
import net.bullettrain.xenonpcs.compat.npc.NpcStatsAttributeHost;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1 counterpart of XenoPixels' {@code StatsNpcHostMixin}. DragonMineZ 1.20.1 keeps the six
 * main stats nowhere but in the host player's attributes ({@code setStrength} ends in
 * {@code setAttributeBaseValue}, {@code getStrength} reads {@code getAttributeBaseValue}, both
 * no-ops without a player), unlike the 1.21.1 port, which stores them in fields. An NPC blob has no
 * player, so without this every stat XenoNPCs wrote was dropped and read back as 0: zero battle
 * power and no melee damage (2026-09-30 owner: "not getting damaged from npcs", "no bp").
 * The stats live in the NPC's own DMZ attributes instead, which {@code XenoNpcEntity} registers.
 */
@Mixin(value = Stats.class, remap = false)
public abstract class StatsNpcHostMixin120 implements NpcStatsAttributeHost {
    @Unique
    private LivingEntity xenonpcs$npcHost;

    @Override
    public void xenopixels$setNpcHost(LivingEntity npc) {
        this.xenonpcs$npcHost = npc;
    }

    @Inject(method = "setAttributeBaseValue", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void xenonpcs$npcSetAttr(Attribute attribute, int value, CallbackInfo ci) {
        LivingEntity host = this.xenonpcs$npcHost;
        if (host == null) return;
        AttributeInstance instance = host.getAttribute(attribute);
        if (instance != null && Math.abs(instance.getBaseValue() - (double) value) >= 0.5) {
            instance.setBaseValue(value);
        }
        ci.cancel();
    }

    @Inject(method = "getAttributeBaseValue", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void xenonpcs$npcReadAttr(Attribute attribute, int fallback, CallbackInfoReturnable<Integer> cir) {
        LivingEntity host = this.xenonpcs$npcHost;
        if (host == null) return;
        AttributeInstance instance = host.getAttribute(attribute);
        cir.setReturnValue(instance == null ? fallback : (int) Math.round(instance.getBaseValue()));
    }
}
