package net.bullettrain.xenonpcs.probe;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@net.neoforged.fml.common.EventBusSubscriber(modid = "xenonpcs_release_probe", value = Dist.CLIENT)
public final class ClientProbe {
    private static boolean finished;
    private static java.util.List<net.minecraft.client.gui.screens.Screen> makers;
    private static net.minecraft.client.gui.screens.Screen title;
    private static int index;
    private static int screenTicks;

    private static void verifyAtlas(Minecraft game) {
        int count = 0;
        for (String shape : net.bullettrain.xenonpcs.client.ui.atlas.XenoAtlasSprites.shapes()) {
            for (var theme : net.bullettrain.xenonpcs.client.ui.atlas.XenoAtlasSprites.Theme.values()) {
                var sprite = net.bullettrain.xenonpcs.client.ui.atlas.XenoAtlasSprites.get(shape, theme);
                try (var stream = game.getResourceManager().getResourceOrThrow(sprite.rl()).open()) {
                    var image = javax.imageio.ImageIO.read(stream);
                    if (image == null || image.getWidth() != sprite.width() || image.getHeight() != sprite.height())
                        throw new IllegalStateException("Invalid atlas PNG: " + sprite.rl());
                    count++;
                } catch (java.io.IOException e) { throw new IllegalStateException("Missing atlas PNG: " + sprite.rl(), e); }
            }
        }
        LogUtils.getLogger().info("XENONPCS_PROBE_ATLAS_PASS textures={}", count);
    }

    private static void renderEvidence(Minecraft game) {
        if (++screenTicks < 30) return;
        var maker = makers.get(index);
        try {
            var directory = game.gameDirectory.toPath().resolve("maker-screenshots");
            java.nio.file.Files.createDirectories(directory);
            try (var image = net.minecraft.client.Screenshot.takeScreenshot(game.getMainRenderTarget())) {
                image.writeToFile(directory.resolve(maker.getClass().getSimpleName() + ".png"));
            }
        } catch (java.io.IOException e) { throw new IllegalStateException("Maker screenshot", e); }
        LogUtils.getLogger().info("XENONPCS_PROBE_MAKER_RENDER_PASS screen={}", maker.getClass().getSimpleName());
        if (++index < makers.size()) {
            screenTicks = 0;
            game.setScreen(makers.get(index));
        } else {
            finished = true;
            game.setScreen(title);
            LogUtils.getLogger().info("XENONPCS_PROBE_CLIENT_PASS screen={}", game.screen.getClass().getName());
            game.stop();
        }
    }
    @SubscribeEvent
    public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        Minecraft game = Minecraft.getInstance();
        if (finished || game.getOverlay() != null || game.screen == null) return;
        if (makers != null) { renderEvidence(game); return; }
        if (game.screen.getClass().getName().contains("LoadingError")) throw new IllegalStateException("NeoForge loading error screen");
        ReleaseProbe.forceTargets("mixins");
        ReleaseProbe.forceTargets("client");
        // Give the isolated capture enough GUI pixels for the inherited DMZ minimum scale.
        game.options.guiScale().set(1);
        game.resizeDisplay();
        verifyAtlas(game);
        title = game.screen;
        makers = java.util.List.of(
                new net.bullettrain.xenonpcs.client.maker.XenoMakerHubScreen(title),
                new net.bullettrain.xenonpcs.client.maker.RaceCharacterMakerScreen(title),
                new net.bullettrain.xenonpcs.client.maker.FormMakerScreen(title),
                new net.bullettrain.xenonpcs.client.maker.HairMakerScreen(title),
                new net.bullettrain.xenonpcs.client.maker.TaottoMakerScreen(title));
        game.setScreen(makers.get(0));
    }
}
