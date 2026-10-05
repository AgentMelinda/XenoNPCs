package net.bullettrain.xenonpcs.features.taotto;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.network.taotto.TaottoNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID)
public final class TaottoEvents {
    private TaottoEvents() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TaottoNetwork.sendTo(player, player);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer viewer
                && event.getTarget() instanceof ServerPlayer subject) {
            TaottoNetwork.sendTo(viewer, subject);
        }
    }
}
