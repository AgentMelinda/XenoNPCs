package net.bullettrain.xenonpcs.item;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Makes the NPC tools and bank currency discoverable in vanilla tabs as well as XenoNPCs. */
@Mod.EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class XenoNpcVanillaTabItems {
    private XenoNpcVanillaTabItems() {}

    @SubscribeEvent
    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModsItems.XENO_NPC_WAND);
            event.accept(ModsItems.XENO_NPC_PATH_TOOL);
            event.accept(ModsItems.XENO_NPC_CLONER);
            event.accept(ModsItems.XENO_NPC_JAR);
            event.accept(ModsItems.XENO_NPC_MOUNTER);
            event.accept(ModsItems.XENO_NPC_TELEPORTER);
            event.accept(ModsItems.XENO_NPC_SCRIPT_TOOL);
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModsItems.ZENI_1);
            event.accept(ModsItems.ZENI_10);
            event.accept(ModsItems.ZENI_25);
            event.accept(ModsItems.ZENI_50);
            event.accept(ModsItems.ZENI_100);
            event.accept(ModsItems.ZENI_200);
            event.accept(ModsItems.ZENI_250);
            event.accept(ModsItems.ZENI_500);
            event.accept(ModsItems.ZENI_1000);
            event.accept(ModsItems.ZENI_NOTE_10000);
            event.accept(ModsItems.ZENI_NOTE_100000);
            event.accept(ModsItems.ZENI_NOTE_1000000);
        }
    }
}
