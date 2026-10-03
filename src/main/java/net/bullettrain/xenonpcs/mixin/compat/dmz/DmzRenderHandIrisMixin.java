package net.bullettrain.xenonpcs.mixin.compat.dmz;

import com.dragonminez.client.render.util.ModRenderTypes;
import net.bullettrain.xenonpcs.client.compat.dmz.KiWeaponRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The first-person half of the Iris ki-weapon fix.
 *
 * <p>{@code DMZRenderHand.renderKiPartTex} draws the held weapon through
 * {@code ModRenderTypes.kiblast}, which uses {@code RENDERTYPE_BEACON_BEAM_SHADER} rather than
 * the eyes shader the third-person layer uses — a different program, but the same class of
 * shader-pack problem, so it gets the same compat render type.
 *
 * <p>Separate from {@code DmzWeaponsLayerIrisMixin} because the two live on different classes;
 * both require a match so a changed DMZ target is visible during release validation.
 */
@Mixin(targets = "com.dragonminez.client.render.DMZRenderHand", remap = false)
public abstract class DmzRenderHandIrisMixin {
    @Redirect(
            method = "renderKiPartTex",
            at = @At(value = "INVOKE", target =
                    "Lcom/dragonminez/client/render/util/ModRenderTypes;kiblast("
                    + "Lnet/minecraft/resources/ResourceLocation;)"
                    + "Lnet/minecraft/client/renderer/RenderType;"),
            require = 1)
    private RenderType xenopixels$shaderSafeHandWeaponType(ResourceLocation texture) {
        return KiWeaponRenderTypes.select(ModRenderTypes.kiblast(texture), texture);
    }
}
