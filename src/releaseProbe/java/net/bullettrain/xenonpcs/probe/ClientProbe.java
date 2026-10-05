package net.bullettrain.xenonpcs.probe;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "xenonpcs_release_probe", value = Dist.CLIENT)
public final class ClientProbe {
    private static boolean finished;
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Minecraft game = Minecraft.getInstance();
        if (finished || event.phase != TickEvent.Phase.END || game.getOverlay() != null || game.screen == null) return;
        finished = true;
        if (game.screen instanceof net.minecraftforge.client.gui.LoadingErrorScreen screen) {
            try {
                var errors = screen.getClass().getDeclaredField("modLoadErrors");
                var warnings = screen.getClass().getDeclaredField("modLoadWarnings");
                errors.setAccessible(true);
                warnings.setAccessible(true);
                LogUtils.getLogger().info("XENONPCS_PROBE_LOADING_MESSAGES errors={} warnings={}", errors.get(screen), warnings.get(screen));
                if (!((java.util.List<?>) errors.get(screen)).isEmpty()) throw new IllegalStateException("Forge mod loading errors");
                game.setScreen(new net.minecraft.client.gui.screens.TitleScreen());
            } catch (ReflectiveOperationException e) { throw new IllegalStateException("Loading screen probe", e); }
        }
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
