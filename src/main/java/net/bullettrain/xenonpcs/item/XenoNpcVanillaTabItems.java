package net.bullettrain.xenonpcs.item;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/** Makes the NPC tools and bank currency discoverable in vanilla tabs as well as XenoNPCs. */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class XenoNpcVanillaTabItems {
    private XenoNpcVanillaTabItems() {}

    @SubscribeEvent
    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModsItems.XENO_NPC_WAND.get());
            event.accept(ModsItems.XENO_NPC_PATH_TOOL.get());
            event.accept(ModsItems.XENO_NPC_CLONER.get());
            event.accept(ModsItems.XENO_NPC_JAR.get());
            event.accept(ModsItems.XENO_NPC_MOUNTER.get());
            event.accept(ModsItems.XENO_NPC_TELEPORTER.get());
            event.accept(ModsItems.XENO_NPC_SCRIPT_TOOL.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModsItems.ZENI_1.get());
            event.accept(ModsItems.ZENI_10.get());
            event.accept(ModsItems.ZENI_25.get());
            event.accept(ModsItems.ZENI_50.get());
            event.accept(ModsItems.ZENI_100.get());
            event.accept(ModsItems.ZENI_200.get());
            event.accept(ModsItems.ZENI_250.get());
            event.accept(ModsItems.ZENI_500.get());
            event.accept(ModsItems.ZENI_1000.get());
            event.accept(ModsItems.ZENI_NOTE_10000.get());
            event.accept(ModsItems.ZENI_NOTE_100000.get());
            event.accept(ModsItems.ZENI_NOTE_1000000.get());
        }
    }
}
