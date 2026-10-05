package net.bullettrain.xenonpcs.probe;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@net.neoforged.fml.common.EventBusSubscriber(modid = "xenonpcs_release_probe", value = Dist.CLIENT)
public final class ClientProbe {
    private static boolean finished;
    @SubscribeEvent
    public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        Minecraft game = Minecraft.getInstance();
        if (finished || game.getOverlay() != null || game.screen == null) return;
        finished = true;
        if (game.screen.getClass().getName().contains("LoadingError")) throw new IllegalStateException("NeoForge loading error screen");
        ReleaseProbe.forceTargets("mixins");
        ReleaseProbe.forceTargets("client");
        var title = game.screen;
        for (var maker : java.util.List.of(
                new net.bullettrain.xenonpcs.client.maker.XenoMakerHubScreen(title),
                new net.bullettrain.xenonpcs.client.maker.RaceCharacterMakerScreen(title),
                new net.bullettrain.xenonpcs.client.maker.FormMakerScreen(title),
                new net.bullettrain.xenonpcs.client.maker.HairMakerScreen(title),
                new net.bullettrain.xenonpcs.client.maker.TaottoMakerScreen(title))) {
            game.setScreen(maker);
            LogUtils.getLogger().info("XENONPCS_PROBE_MAKER_INIT_PASS screen={}", maker.getClass().getSimpleName());
        }
        game.setScreen(title);
        LogUtils.getLogger().info("XENONPCS_PROBE_CLIENT_PASS screen={}", game.screen.getClass().getName());
        game.stop();
    }
}
