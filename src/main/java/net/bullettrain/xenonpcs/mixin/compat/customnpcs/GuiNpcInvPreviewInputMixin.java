package net.bullettrain.xenonpcs.mixin.compat.customnpcs;

import net.bullettrain.xenonpcs.client.compat.npc.gui.NpcWandPreview;
import noppes.npcs.client.gui.mainmenu.GuiNPCInv;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The Forge inventory screen inherits its click handler from this container base. */
@Mixin(targets = "noppes.npcs.client.gui.util.GuiContainerNPCInterface2", remap = false)
public abstract class GuiNpcInvPreviewInputMixin {
    @Inject(method = "mouseClicked(DDI)Z", remap = true, at = @At("HEAD"), cancellable = true, require = 1)
    private void xenopixels$clickInvPreview(double mouseX, double mouseY, int button,
                                           CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof GuiNPCInv screen
                && NpcWandPreview.mouseClicked(screen, screen.npc, screen.guiLeft, screen.guiTop,
                mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }
}
