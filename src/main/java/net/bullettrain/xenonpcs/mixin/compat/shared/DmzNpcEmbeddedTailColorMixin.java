package net.bullettrain.xenonpcs.mixin.compat.shared;

import com.dragonminez.client.render.DMZPlayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.bullettrain.xenonpcs.client.compat.npc.NpcFullDmzRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;

/** Colors the tail bones embedded in Bio-Android and Frost Demon body models. */
@Mixin(value = GeoEntityRenderer.class, remap = false)
public abstract class DmzNpcEmbeddedTailColorMixin {
    private static final ThreadLocal<float[]> XENOPIXELS_EMBEDDED_TAIL_COLOR = new ThreadLocal<>();
    private static final String RENDER = "renderRecursively(" +
            "Lcom/mojang/blaze3d/vertex/PoseStack;" +
            "Lnet/minecraft/world/entity/Entity;" +
            "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
            "Lnet/minecraft/client/renderer/RenderType;" +
            "Lnet/minecraft/client/renderer/MultiBufferSource;" +
            "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V";
    private static final String CUBES = "Lsoftware/bernie/geckolib/renderer/GeoEntityRenderer;renderCubesOfBone(" +
            "Lcom/mojang/blaze3d/vertex/PoseStack;" +
            "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
            "Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V";

    @Inject(method = RENDER, at = @At("HEAD"), require = 1)
    private void xenopixels$captureEmbeddedTail(PoseStack pose, Entity entity, GeoBone bone,
            RenderType type, MultiBufferSource buffers, VertexConsumer consumer,
            boolean reRender, float partialTick, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha, CallbackInfo ci) {
        float[] color = ((Object) this instanceof DMZPlayerRenderer<?>)
                && bone != null && NpcFullDmzRenderer.isEmbeddedRaceTailBone(bone.getName())
                ? NpcFullDmzRenderer.tailColorOverride() : null;
        if (color != null && color.length >= 3) XENOPIXELS_EMBEDDED_TAIL_COLOR.set(color);
        else XENOPIXELS_EMBEDDED_TAIL_COLOR.remove();
    }

    @ModifyArg(
            method = RENDER, at = @At(value = "INVOKE", target = CUBES), index = 5, require = 1)
    private float xenopixels$embeddedTailRed(float original) {
        float[] color = XENOPIXELS_EMBEDDED_TAIL_COLOR.get();
        return color == null ? original : color[0];
    }

    @ModifyArg(method = RENDER, at = @At(value = "INVOKE", target = CUBES), index = 6, require = 1)
    private float xenopixels$embeddedTailGreen(float original) {
        float[] color = XENOPIXELS_EMBEDDED_TAIL_COLOR.get();
        return color == null ? original : color[1];
    }

    @ModifyArg(method = RENDER, at = @At(value = "INVOKE", target = CUBES), index = 7, require = 1)
    private float xenopixels$embeddedTailBlue(float original) {
        float[] color = XENOPIXELS_EMBEDDED_TAIL_COLOR.get();
        return color == null ? original : color[2];
    }

    @Inject(method = RENDER, at = @At("RETURN"), require = 1)
    private void xenopixels$clearEmbeddedTail(PoseStack pose, Entity entity, GeoBone bone,
            RenderType type, MultiBufferSource buffers, VertexConsumer consumer,
            boolean reRender, float partialTick, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha, CallbackInfo ci) {
        XENOPIXELS_EMBEDDED_TAIL_COLOR.remove();
    }
}
