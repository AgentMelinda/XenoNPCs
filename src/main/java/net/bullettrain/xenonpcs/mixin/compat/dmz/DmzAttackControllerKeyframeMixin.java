package net.bullettrain.xenonpcs.mixin.compat.dmz;

import net.bullettrain.xenonpcs.client.combat.anim.Bt3KeyframeHandlers;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animation.AnimatableManager;

/** Adds Xeno cosmetic keyframe callbacks to DragonMineZ's existing attack controller. */
@Mixin(value = AbstractClientPlayer.class, priority = 900)
public abstract class DmzAttackControllerKeyframeMixin {

    @Inject(method = "registerControllers", at = @At("RETURN"), remap = false, require = 0)
    private void xeno$attachBt3KeyframeHandlers(AnimatableManager.ControllerRegistrar controllers,
                                                CallbackInfo ci) {
        try {
            java.lang.reflect.Field field = AnimatableManager.ControllerRegistrar.class.getDeclaredField("controllers");
            field.setAccessible(true);
            for (Object controller : (java.util.List<?>) field.get(controllers)) {
                Bt3KeyframeHandlers.attach((software.bernie.geckolib.core.animation.AnimationController<?>) controller);
            }
        } catch (ReflectiveOperationException ignored) {
            // A GeckoLib without that field: the cosmetic keyframe callbacks are skipped.
        }
    }
}
