package net.bullettrain.xenonpcs.client.maker;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Minecraft 1.20.1 inventory preview signature, verified against the mapped Forge artifact. */
public final class ForgeMakerPreviewRender {
    private ForgeMakerPreviewRender() {}
    public static void renderEntityInInventory(GuiGraphics graphics, int x, int y, float scale,
            Vector3f translation, Quaternionf pose, Quaternionf camera, LivingEntity entity) {
        InventoryScreen.renderEntityInInventory(graphics, x, y, Math.round(scale), pose, camera, entity);
    }
}
