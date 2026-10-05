package net.bullettrain.xenonpcs.dmz.race;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.network.race.RaceLabelNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID)
public final class RaceLabelEvents {
    private RaceLabelEvents() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        RaceLabelRegistry.loadFromDisk();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaceLabelNetwork.sendSnapshot(player);
            MakerRaceService.sendSnapshots(player);
        }
    }
}
