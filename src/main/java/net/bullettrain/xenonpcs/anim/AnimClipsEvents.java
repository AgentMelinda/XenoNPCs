package net.bullettrain.xenonpcs.anim;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.network.AnimClipsNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;

/** Forge lifecycle counterpart of XenoPixelsMod's clip-library subscriber. */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public final class AnimClipsEvents {
    private AnimClipsEvents() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        XenoClipLibrary.load();
        XenoTechniqueAnimBindings.load();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) AnimClipsNetwork.sendTo(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CombatStateAnim.forget(event.getEntity().getUUID());
    }
}
