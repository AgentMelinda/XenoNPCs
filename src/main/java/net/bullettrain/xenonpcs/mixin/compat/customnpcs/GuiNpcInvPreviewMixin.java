package net.bullettrain.xenonpcs.mixin.compat.customnpcs;

import net.bullettrain.xenonpcs.client.compat.npc.gui.NpcWandPreview;
import net.minecraft.client.gui.GuiGraphics;
import noppes.npcs.client.gui.mainmenu.GuiNPCInv;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** DMZ visualizer on the CustomNPCs Inventory tab (a container screen, not {@code GuiNPCInterface2}). */
@Mixin(value = GuiNPCInv.class, remap = false)
public abstract class GuiNpcInvPreviewMixin {

    @Inject(method = "m_88315_", at = @At("RETURN"), require = 1)
    private void xenopixels$renderInvPreview(GuiGraphics graphics, int mouseX, int mouseY,
                                             float partialTick, CallbackInfo ci) {
        GuiNPCInv screen = (GuiNPCInv) (Object) this;
        NpcWandPreview.render(screen, screen.npc, graphics, screen.guiLeft, screen.guiTop, partialTick);
    }

}
