package net.bullettrain.xenonpcs.npc.script;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.network.ModNetwork;
import net.bullettrain.xenonpcs.network.packet.SyncNpcStoreIndexPacket;
import net.bullettrain.xenonpcs.npc.store.XenoNpcScripts;
import net.bullettrain.xenonpcs.npc.store.XenoNpcStoreCategory;
import net.bullettrain.xenonpcs.npc.store.XenoNpcStores;
import net.bullettrain.xenonpcs.npc.store.XenoNpcWorldStore;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * Script files deleted, added or edited on disk (in {@code <world>/XenoNpcs/scripts},
 * {@code player_scripts}, {@code forge_scripts}) take effect within two seconds, the same way a save
 * from the scripter does: scripts rebuilt, player and forge scripts restarted, editors told.
 * 2026-09-29 owner: a removed player script kept running until the next login.
 */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID)
public final class ScriptFolderWatch {
    static final int INTERVAL_TICKS = 40;
    private static final XenoNpcStoreCategory[] WATCHED = {XenoNpcStoreCategory.SCRIPTS,
            XenoNpcStoreCategory.PLAYER_SCRIPTS, XenoNpcStoreCategory.FORGE_SCRIPTS};
    private static int countdown = INTERVAL_TICKS;

    private ScriptFolderWatch() {
    }

    @SubscribeEvent
    public static void onServerTick(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        if (--countdown > 0) return;
        countdown = INTERVAL_TICKS;
        check(event.getServer());
    }

    static void check(MinecraftServer server) {
        XenoNpcWorldStore store = XenoNpcStores.get();
        if (store == null || server == null) return;
        boolean any = false;
        for (XenoNpcStoreCategory category : WATCHED) {
            if (!store.reloadIfChangedOnDisk(category)) continue;
            any = true;
            XenoNpcsMod.LOGGER.info("NPC store: {} changed on disk, reloaded", category.folder());
            XenoNpcScripts.changed();
            if (category == XenoNpcStoreCategory.PLAYER_SCRIPTS) PlayerScriptHost.reload(server);
            if (category == XenoNpcStoreCategory.FORGE_SCRIPTS) ForgeScriptHost.reload(server);
        }
        if (any) ModNetwork.sendToAll(SyncNpcStoreIndexPacket.current());
    }
}
