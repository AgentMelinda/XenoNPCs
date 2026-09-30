package net.bullettrain.xenonpcs.client;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.missile.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.client.event.EntityRenderersEvent;

/** XenoNPCs client registrations: the NPC bank/inventory screens and the NPC renderer. */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerMenuScreens(
            net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.gui.screens.MenuScreens.register(net.bullettrain.xenonpcs.npc.bank.ModMenus.NPC_BANK.get(),
                    net.bullettrain.xenonpcs.client.npc.bank.XenoNpcBankScreen::new);
            net.minecraft.client.gui.screens.MenuScreens.register(net.bullettrain.xenonpcs.npc.bank.ModMenus.NPC_INVENTORY.get(),
                    net.bullettrain.xenonpcs.client.npc.inventory.XenoNpcInventoryScreen::new);
        });
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
