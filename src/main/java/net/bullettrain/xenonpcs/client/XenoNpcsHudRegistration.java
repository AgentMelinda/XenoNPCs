package net.bullettrain.xenonpcs.client;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** The HUD layers XenoNPCs draws (Forge 1.20.1): the quest toast, above everything else. */
@Mod.EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class XenoNpcsHudRegistration {
    private XenoNpcsHudRegistration() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("xeno_quest_toast",
                (gui, graphics, partialTick, width, height) ->
                        net.bullettrain.xenonpcs.client.npc.quest.QuestToastOverlay.render(graphics, null));
    }
}
