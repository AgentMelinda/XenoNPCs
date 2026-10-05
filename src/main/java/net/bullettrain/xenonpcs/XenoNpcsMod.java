package net.bullettrain.xenonpcs;

import com.mojang.logging.LogUtils;
import net.bullettrain.xenonpcs.capability.XenoCapabilities;
import net.bullettrain.xenonpcs.effect.ModEffects;
import net.bullettrain.xenonpcs.item.ModCreativeModTabs;
import net.bullettrain.xenonpcs.item.ModsItems;
import net.bullettrain.xenonpcs.network.ModNetwork;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * XenoNPCs: the Xeno NPC system on its own - native NPCs, the wand and tools, the editor,
 * DragonMineZ integration and scripting.
 *
 * <p>Generated from XenoPixels by {@code tools/xenonpcs/export_xenonpcs.py}; this main class and the
 * NPC-only registries are the hand-written overlay part. Only the V9 combat brain is offered
 * ({@code compat.npc.NpcBrainPolicy}).
 */
@Mod(XenoNpcsMod.MOD_ID)
public class XenoNpcsMod {
    public static final String MOD_ID = "xenonpcs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public XenoNpcsMod(IEventBus modEventBus) {
        ModsItems.register(modEventBus);
        net.bullettrain.xenonpcs.missile.ModEntities.register(modEventBus);
        net.bullettrain.xenonpcs.npc.bank.ModMenus.register(modEventBus);
        ModEffects.register(modEventBus);
        net.bullettrain.xenonpcs.sound.ModSounds.register(modEventBus);
        XenoCapabilities.register(modEventBus);
        net.bullettrain.xenonpcs.compat.npc.NpcDmzStats.register(modEventBus);
        ModNetwork.register();
        net.bullettrain.xenonpcs.network.taotto.TaottoNetwork.register();
        net.bullettrain.xenonpcs.network.maker.MakerNetwork.register();
        net.bullettrain.xenonpcs.network.race.RaceLabelNetwork.register();
        // XenoAPI (xenoapi.npcs.api) over native NPCs; holds no world, resolves the server per call.
        net.bullettrain.xenonpcs.npc.script.api.xeno.NativeNpcApi.register();
        net.bullettrain.xenonpcs.network.form.FormEditorNetwork.register();
        net.bullettrain.xenonpcs.network.AnimClipsNetwork.register();
        ModCreativeModTabs.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            net.bullettrain.xenonpcs.config.XenoServerConfig.load();
            // CustomNPCs and My NPCs each ship their own ScriptContainer; install the bridge only
            // for the one that is present, reflectively, so neither is loaded on a server without it.
            installScriptApi("mynpcs", "compat.npc.mynpcs.NpcXenoScriptApi", "My NPCs");
            installScriptApi("customnpcs", "compat.npc.NpcXenoScriptApi", "CustomNPCs");
        });
    }

    private static void installScriptApi(String modId, String bridge, String label) {
        try {
            if (!net.neoforged.fml.ModList.get().isLoaded(modId)) return;
            Class.forName("net.bullettrain.xenonpcs." + bridge).getMethod("install").invoke(null);
            LOGGER.info("{} XenoNPCs scripting API installed", label);
        } catch (Throwable t) {
            LOGGER.warn("{} XenoNPCs scripting API unavailable: {}", label, t.toString());
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetup {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                net.bullettrain.xenonpcs.client.config.XenoClientConfig.load();
                net.bullettrain.xenonpcs.client.maker.MakerClientBindings.bind();
                net.bullettrain.xenonpcs.client.ClientScreens.openXenoNpcEditor = data -> {
                    // The editor payload carries the full server profile: it is the save baseline.
                    net.bullettrain.xenonpcs.client.npc.ClientNpcProfiles.accept(
                            data.entityId(), data.data().getCompound("Profile"));
                    net.minecraft.client.Minecraft.getInstance().setScreen(
                            new net.bullettrain.xenonpcs.client.npc.XenoNpcEditorScreen(
                                    data.entityId(), data.data()));
                };
                net.bullettrain.xenonpcs.client.ClientScreens.receiveNpcProfile =
                        net.bullettrain.xenonpcs.client.npc.ClientNpcProfiles::accept;
                net.bullettrain.xenonpcs.client.ClientScreens.openNpcNearby = entries -> {
                    var mc = net.minecraft.client.Minecraft.getInstance();
                    if (mc.screen instanceof net.bullettrain.xenonpcs.client.npc.XenoNpcNearbyScreen open) {
                        open.update(entries);
                    } else {
                        mc.setScreen(new net.bullettrain.xenonpcs.client.npc.XenoNpcNearbyScreen(entries));
                    }
                };
                net.bullettrain.xenonpcs.client.ClientScreens.openScriptHub = () ->
                        net.minecraft.client.Minecraft.getInstance().setScreen(
                                new net.bullettrain.xenonpcs.client.npc.XenoScriptHubScreen());
                net.bullettrain.xenonpcs.client.ClientScreens.openXenoNpcScript = data ->
                        net.minecraft.client.Minecraft.getInstance().setScreen(
                                net.bullettrain.xenonpcs.client.npc.XenoNpcScriptScreen.forTool(
                                        net.minecraft.client.Minecraft.getInstance().screen,
                                        data.entityId(), data.container()));
            });
        }
    }
}
