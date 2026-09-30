package net.bullettrain.xenonpcs.item;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/** The XenoNPCs creative tab, with the XenoNPCs icon. */
public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, XenoNpcsMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> XENONPCS_TAB =
            CREATIVE_MODE_TABS.register("xenonpcs_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModsItems.TAB_ICON.get()))
                    .title(Component.translatable("itemGroup.xenonpcs"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModsItems.XENO_NPC_WAND.get());
                        output.accept(ModsItems.XENO_NPC_PATH_TOOL.get());
                        output.accept(ModsItems.XENO_NPC_CLONER.get());
                        output.accept(ModsItems.XENO_NPC_JAR.get());
                        output.accept(ModsItems.XENO_NPC_MOUNTER.get());
                        output.accept(ModsItems.XENO_NPC_TELEPORTER.get());
                        output.accept(ModsItems.XENO_NPC_SCRIPT_TOOL.get());
                        output.accept(ModsItems.ZENI_1.get());
                        output.accept(ModsItems.ZENI_10.get());
                        output.accept(ModsItems.ZENI_25.get());
                        output.accept(ModsItems.ZENI_50.get());
                        output.accept(ModsItems.ZENI_100.get());
                        output.accept(ModsItems.ZENI_200.get());
                        output.accept(ModsItems.ZENI_250.get());
                        output.accept(ModsItems.ZENI_500.get());
                        output.accept(ModsItems.ZENI_1000.get());
                        output.accept(ModsItems.ZENI_NOTE_10000.get());
                        output.accept(ModsItems.ZENI_NOTE_100000.get());
                        output.accept(ModsItems.ZENI_NOTE_1000000.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
