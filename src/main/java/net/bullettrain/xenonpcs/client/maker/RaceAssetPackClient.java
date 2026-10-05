package net.bullettrain.xenonpcs.client.maker;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.dmz.race.RaceAssetPack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.AddPackFindersEvent;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Registers the generated race-catalog folder pack so TextureCounter sees authored body PNGs.
 */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RaceAssetPackClient {
    private RaceAssetPackClient() {
    }

    public static java.util.concurrent.CompletableFuture<Void> reload() {
        return net.minecraft.client.Minecraft.getInstance().reloadResourcePacks().thenRun(() -> {
            com.dragonminez.client.util.TextureCounter.clearCache();
            com.dragonminez.client.render.layer.DMZSkinLayer.clearValidatedTexturesCache();
        });
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        Path root = RaceAssetPack.packRoot();
        try {
            RaceAssetPack.ensureMcmeta(root);
        } catch (Exception e) {
            XenoNpcsMod.LOGGER.warn("Could not write race catalog pack.mcmeta", e);
            return;
        }
        if (!Files.isDirectory(root)) {
            return;
        }
        event.addRepositorySource(consumer -> {
            Pack pack = Pack.readMetaAndCreate(RaceAssetPack.PACK_ID,
                    Component.literal("XenoNPCs Race Catalogs"), true,
                    (id) -> new PathPackResources(id, root, true),
                    PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }
}
