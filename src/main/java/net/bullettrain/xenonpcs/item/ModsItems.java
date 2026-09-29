package net.bullettrain.xenonpcs.item;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.item.custom.ZeniCashItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** XenoNPCs items: the NPC tools, the Zeni the NPC banks pay in, and the creative-tab icon. */
public class ModsItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, XenoNpcsMod.MOD_ID);

    /** Only the creative tab's icon: not listed in the tab, no recipe. */
    public static final DeferredHolder<Item, Item> TAB_ICON = ITEMS.register("tab_icon",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> XENO_NPC_WAND = ITEMS.register("xeno_npc_wand",
            () -> new net.bullettrain.xenonpcs.item.custom.XenoNpcWandItem(
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> XENO_NPC_PATH_TOOL = ITEMS.register("xeno_npc_path_tool",
            () -> new net.bullettrain.xenonpcs.item.custom.XenoNpcPathToolItem(
                    new Item.Properties().stacksTo(1)));

    /** Copy a configured NPC and place it again. */
    public static final DeferredHolder<Item, Item> XENO_NPC_CLONER = ITEMS.register("xeno_npc_cloner",
            () -> new net.bullettrain.xenonpcs.item.custom.XenoNpcClonerItem(
                    new Item.Properties().stacksTo(1)));

    /** Take an NPC out of the world and put it back somewhere else. */
    public static final DeferredHolder<Item, Item> XENO_NPC_JAR = ITEMS.register("xeno_npc_jar",
            () -> new net.bullettrain.xenonpcs.item.custom.XenoNpcJarItem(
                    new Item.Properties().stacksTo(1)));

    /** Sit an NPC on a mob, a boat, or on you. */
    public static final DeferredHolder<Item, Item> XENO_NPC_MOUNTER = ITEMS.register("xeno_npc_mounter",
            () -> new net.bullettrain.xenonpcs.item.custom.XenoNpcMounterItem(
                    new Item.Properties().stacksTo(1)));

    /** Move an NPC, including across dimensions. */
    public static final DeferredHolder<Item, Item> XENO_NPC_TELEPORTER = ITEMS.register("xeno_npc_teleporter",
            () -> new net.bullettrain.xenonpcs.item.custom.XenoNpcTeleporterItem(
                    new Item.Properties().stacksTo(1)));

    /** Opens the script screen bound to the NPC it is used on. */
    public static final DeferredHolder<Item, Item> XENO_NPC_SCRIPT_TOOL = ITEMS.register("xeno_npc_script_tool",
            () -> new net.bullettrain.xenonpcs.item.custom.XenoNpcScriptToolItem(
                    new Item.Properties().stacksTo(1)));

    private static DeferredHolder<Item, Item> zeni(String id, long value) {
        return ITEMS.register(id, () -> new ZeniCashItem(new Item.Properties().stacksTo(64), value));
    }

    public static final DeferredHolder<Item, Item> ZENI_1 = zeni("zeni_1", 1);
    public static final DeferredHolder<Item, Item> ZENI_10 = zeni("zeni_10", 10);
    public static final DeferredHolder<Item, Item> ZENI_25 = zeni("zeni_25", 25);
    public static final DeferredHolder<Item, Item> ZENI_50 = zeni("zeni_50", 50);
    public static final DeferredHolder<Item, Item> ZENI_100 = zeni("zeni_100", 100);
    public static final DeferredHolder<Item, Item> ZENI_200 = zeni("zeni_200", 200);
    public static final DeferredHolder<Item, Item> ZENI_250 = zeni("zeni_250", 250);
    public static final DeferredHolder<Item, Item> ZENI_500 = zeni("zeni_500", 500);
    public static final DeferredHolder<Item, Item> ZENI_1000 = zeni("zeni_1000", 1_000);
    public static final DeferredHolder<Item, Item> ZENI_NOTE_10000 = zeni("zeni_note_10000", 10_000);
    public static final DeferredHolder<Item, Item> ZENI_NOTE_100000 = zeni("zeni_note_100000", 100_000);
    public static final DeferredHolder<Item, Item> ZENI_NOTE_1000000 = zeni("zeni_note_1000000", 1_000_000);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
