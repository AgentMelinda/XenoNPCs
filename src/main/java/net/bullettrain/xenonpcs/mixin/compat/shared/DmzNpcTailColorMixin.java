package net.bullettrain.xenonpcs.mixin.compat.shared;

import com.dragonminez.client.render.layer.DMZRacePartsLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.bullettrain.xenonpcs.client.compat.npc.NpcFullDmzRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;

/** Applies an NPC-only tail override after DMZ has resolved its normal race/form inheritance. */
@Mixin(value = DMZRacePartsLayer.class, remap = false)
public abstract class DmzNpcTailColorMixin {
    private static final ThreadLocal<float[]> XENOPIXELS_TAIL_COLOR = new ThreadLocal<>();

    @Inject(
            method = "renderTargetedBone(Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                    "Lnet/minecraft/client/renderer/RenderType;FFFFFI)V",
            at = @At("HEAD"), require = 1)
    private void xenopixels$captureNpcTailColor(
            GeoBone bone, PoseStack pose, MultiBufferSource buffers,
            AbstractClientPlayer player, RenderType type,
            float red, float green, float blue, float alpha, float partialTick, int light,
            CallbackInfo ci) {
        float[] color = NpcFullDmzRenderer.tailColorOverride();
        // Any tail bone, not just the one this layer used to hardcode -- see
        // NpcFullDmzRenderer.isTailBone for why the match is on the bone rather than the race.
        if (bone != null && NpcFullDmzRenderer.isTailBone(bone.getName())
                && color != null && color.length >= 3) {
            XENOPIXELS_TAIL_COLOR.set(color);
        } else {
            XENOPIXELS_TAIL_COLOR.remove();
        }
    }

    @ModifyArg(
            method = "renderTargetedBone(Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                    "Lnet/minecraft/client/renderer/RenderType;FFFFFI)V",
            at = @At(value = "INVOKE", target =
                    "Lsoftware/bernie/geckolib/renderer/GeoRenderer;renderRecursively(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lnet/minecraft/client/renderer/RenderType;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V"),
            index = 10, require = 1)
    private float xenopixels$useNpcTailColor0(float original) {
        float[] color = XENOPIXELS_TAIL_COLOR.get();
        return color == null || color.length < 3 ? original : color[0];
    }

    @ModifyArg(
            method = "renderTargetedBone(Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                    "Lnet/minecraft/client/renderer/RenderType;FFFFFI)V",
            at = @At(value = "INVOKE", target =
                    "Lsoftware/bernie/geckolib/renderer/GeoRenderer;renderRecursively(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lnet/minecraft/client/renderer/RenderType;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V"),
            index = 11, require = 1)
    private float xenopixels$useNpcTailColor1(float original) {
        float[] color = XENOPIXELS_TAIL_COLOR.get();
        return color == null || color.length < 3 ? original : color[1];
    }

    @ModifyArg(
            method = "renderTargetedBone(Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                    "Lnet/minecraft/client/renderer/RenderType;FFFFFI)V",
            at = @At(value = "INVOKE", target =
                    "Lsoftware/bernie/geckolib/renderer/GeoRenderer;renderRecursively(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lnet/minecraft/client/renderer/RenderType;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V"),
            index = 12, require = 1)
    private float xenopixels$useNpcTailColor2(float original) {
        float[] color = XENOPIXELS_TAIL_COLOR.get();
        return color == null || color.length < 3 ? original : color[2];
    }

    @Inject(
            method = "renderTargetedBone(Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/client/player/AbstractClientPlayer;" +
                    "Lnet/minecraft/client/renderer/RenderType;FFFFFI)V",
            at = @At("RETURN"), require = 1)
    private void xenopixels$clearNpcTailColor(
            GeoBone bone, PoseStack pose, MultiBufferSource buffers,
            AbstractClientPlayer player, RenderType type,
            float red, float green, float blue, float alpha, float partialTick, int light,
            CallbackInfo ci) {
        XENOPIXELS_TAIL_COLOR.remove();
    }
}
