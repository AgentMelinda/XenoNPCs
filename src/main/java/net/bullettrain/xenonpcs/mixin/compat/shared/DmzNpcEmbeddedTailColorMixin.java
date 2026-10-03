package net.bullettrain.xenonpcs.mixin.compat.shared;

import com.dragonminez.client.render.DMZPlayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.bullettrain.xenonpcs.client.compat.npc.NpcFullDmzRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** Colors embedded tail bones without requiring Mixin's generated Args classes on Forge. */
@Mixin(value = GeoEntityRenderer.class, remap = false)
public abstract class DmzNpcEmbeddedTailColorMixin {
    @ModifyArg(
            method = "renderRecursively(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/world/entity/Entity;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lnet/minecraft/client/renderer/RenderType;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            at = @At(value = "INVOKE", target =
                    "Lsoftware/bernie/geckolib/renderer/GeoEntityRenderer;renderCubesOfBone(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 5, require = 1)
    private float xenopixels$colorEmbeddedRaceTail0(
            PoseStack pose, GeoBone bone, VertexConsumer consumer,
            int light, int overlay, float red, float green, float blue, float alpha) {
        return xenopixels$tailComponent(red, bone, 0);
    }

    @ModifyArg(
            method = "renderRecursively(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/world/entity/Entity;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lnet/minecraft/client/renderer/RenderType;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            at = @At(value = "INVOKE", target =
                    "Lsoftware/bernie/geckolib/renderer/GeoEntityRenderer;renderCubesOfBone(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 6, require = 1)
    private float xenopixels$colorEmbeddedRaceTail1(
            PoseStack pose, GeoBone bone, VertexConsumer consumer,
            int light, int overlay, float red, float green, float blue, float alpha) {
        return xenopixels$tailComponent(green, bone, 1);
    }

    @ModifyArg(
            method = "renderRecursively(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/world/entity/Entity;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lnet/minecraft/client/renderer/RenderType;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            at = @At(value = "INVOKE", target =
                    "Lsoftware/bernie/geckolib/renderer/GeoEntityRenderer;renderCubesOfBone(" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lsoftware/bernie/geckolib/cache/object/GeoBone;" +
                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 7, require = 1)
    private float xenopixels$colorEmbeddedRaceTail2(
            PoseStack pose, GeoBone bone, VertexConsumer consumer,
            int light, int overlay, float red, float green, float blue, float alpha) {
        return xenopixels$tailComponent(blue, bone, 2);
    }

    @Unique
    private float xenopixels$tailComponent(float original, GeoBone bone, int component) {
        if (!((Object) this instanceof DMZPlayerRenderer<?>)) return original;
        float[] color = NpcFullDmzRenderer.tailColorOverride();
        if (color == null || color.length < 3 || bone == null
                || !NpcFullDmzRenderer.isEmbeddedRaceTailBone(bone.getName())) return original;
        return Math.max(0.0f, Math.min(1.0f, color[component]));
    }
}
