package net.bullettrain.xenonpcs.client;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

/**
 * The HUD layers XenoNPCs draws. XenoPixels registers its NPC quest toast with the rest of its HUD
 * in {@code XenoHudRegistration}, which is not part of XenoNPCs; this registers the toast alone,
 * above DragonMineZ's own layers exactly as XenoPixels places it.
 */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class XenoNpcsHudRegistration {
    /** Last DMZ overlay in their registration order. */
    private static final ResourceLocation DMZ_TOP = ResourceLocation.fromNamespaceAndPath("dragonminez", "beam_clash_hud");

    private XenoNpcsHudRegistration() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(DMZ_TOP, ResourceLocation.fromNamespaceAndPath(XenoNpcsMod.MOD_ID, "xeno_quest_toast"),
                net.bullettrain.xenonpcs.client.npc.quest.QuestToastOverlay::render);
    }
}
