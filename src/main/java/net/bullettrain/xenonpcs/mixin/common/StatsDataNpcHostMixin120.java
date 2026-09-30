package net.bullettrain.xenonpcs.mixin.common;

import com.dragonminez.common.stats.StatsData;
import net.bullettrain.xenonpcs.compat.npc.NpcStatsDataAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1 counterpart of XenoPixels' {@code StatsDataNpcHostMixin}: DragonMineZ's secondary
 * attribute reads (and armor) come from the NPC carrying the blob, where DMZ would use its player.
 * See {@link StatsNpcHostMixin120} for why 1.20.1 needs this and 1.21.1 does not.
 */
@Mixin(value = StatsData.class, remap = false)
public abstract class StatsDataNpcHostMixin120 implements NpcStatsDataAccess {
    @Shadow private boolean isDataLoaded;

    @Unique
    private LivingEntity xenonpcs$npcHost;

    @Override
    public void xenopixels$setNpcHost(LivingEntity npc) {
        this.xenonpcs$npcHost = npc;
    }

    @Override
    public LivingEntity xenopixels$getNpcHost() {
        return this.xenonpcs$npcHost;
    }

    @Override
    public void xenopixels$markLoaded() {
        this.isDataLoaded = true;
    }

    @Inject(method = "getSecondaryAttributeValue", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void xenonpcs$hostAttrValue(Attribute attribute, double fallback, CallbackInfoReturnable<Double> cir) {
        LivingEntity host = this.xenonpcs$npcHost;
        if (host == null) return;
        AttributeInstance instance = host.getAttribute(attribute);
        cir.setReturnValue(instance != null ? instance.getValue() : fallback);
    }

    @Inject(method = "getSecondaryAttributeBaseValue", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void xenonpcs$hostAttrBase(Attribute attribute, double fallback, CallbackInfoReturnable<Double> cir) {
        LivingEntity host = this.xenonpcs$npcHost;
        if (host == null) return;
        AttributeInstance instance = host.getAttribute(attribute);
        cir.setReturnValue(instance != null ? instance.getBaseValue() : fallback);
    }

    @Inject(method = "getArmorToughnessValue", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void xenonpcs$hostToughness(CallbackInfoReturnable<Double> cir) {
        LivingEntity host = this.xenonpcs$npcHost;
        if (host == null) return;
        AttributeInstance toughness = host.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS);
        cir.setReturnValue(toughness != null ? toughness.getValue() : 0.0);
    }

    @Redirect(method = {"getMaxDefense", "getDefense"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"),
            remap = false, require = 0)
    private int xenonpcs$hostArmor(Player player) {
        LivingEntity host = this.xenonpcs$npcHost != null ? this.xenonpcs$npcHost : player;
        return host == null ? 0 : host.getArmorValue();
    }
}
