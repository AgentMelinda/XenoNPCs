package net.bullettrain.xenonpcs.client;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.missile.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** XenoNPCs client registrations: the NPC bank/inventory screens and the NPC renderer. */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerMenuScreens(
            net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(net.bullettrain.xenonpcs.npc.bank.ModMenus.NPC_BANK.get(),
                net.bullettrain.xenonpcs.client.npc.bank.XenoNpcBankScreen::new);
        event.register(net.bullettrain.xenonpcs.npc.bank.ModMenus.NPC_INVENTORY.get(),
                net.bullettrain.xenonpcs.client.npc.inventory.XenoNpcInventoryScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.XENO_NPC_HUMANOID.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.XENO_NPC_CREATURE.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.XENO_NPC_TRADER.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.XENO_NPC_GUARD.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.XENO_NPC_COMPANION.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.XENO_NPC_QUEST.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.XENO_NPC_TRANSPORTER.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
        event.registerEntityRenderer(ModEntities.XENO_NPC_BANK.get(), net.bullettrain.xenonpcs.client.npc.XenoNpcRenderer::new);
    }
}
