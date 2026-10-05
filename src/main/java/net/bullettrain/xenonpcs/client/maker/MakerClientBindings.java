package net.bullettrain.xenonpcs.client.maker;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.client.ClientScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, value = Dist.CLIENT)
public final class MakerClientBindings {
    private MakerClientBindings() {}
    public static void bind() {
        ClientScreens.receiveTaotto = TaottoClientOverlays::accept;
        ClientScreens.receiveMakerRace = MakerRaceClient::accept;
        net.bullettrain.xenonpcs.client.config.XenoAuraConfig.load();
    }
    @SubscribeEvent public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("xenomaker")
                .executes(ctx -> open("hub"))
                .then(Commands.literal("race").executes(ctx -> open("race")))
                .then(Commands.literal("forms").executes(ctx -> open("forms")))
                .then(Commands.literal("hair").executes(ctx -> open("hair")))
                .then(Commands.literal("tattoo").executes(ctx -> open("tattoo")))
                .then(Commands.literal("taotto").executes(ctx -> open("tattoo"))));
        event.getDispatcher().register(Commands.literal("xenohairui").executes(ctx -> open("hair")));
    }
    private static int open(String kind) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        var parent = mc.screen;
        mc.setScreen(switch (kind) {
            case "race" -> new RaceCharacterMakerScreen(parent);
            case "forms" -> new FormMakerScreen(parent);
            case "hair" -> new HairMakerScreen(parent);
            case "tattoo" -> new TaottoMakerScreen(parent);
            default -> new XenoMakerHubScreen(parent);
        });
        return 1;
    }
    @SubscribeEvent public static void hideHud(net.minecraftforge.client.event.RenderGuiOverlayEvent.Pre event) {
        var id = event.getOverlay().id();
        if (MakerHudGate.hideWorldHud() && id.getNamespace().equals("dragonminez")
                && java.util.Set.of("xenoversehud", "alternativehud", "techniquehud", "technique_charge_hud").contains(id.getPath()))
            event.setCanceled(true);
    }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        TaottoClientOverlays.clearAll(); MakerRaceClient.clear();
        net.bullettrain.xenonpcs.client.combat.aura.XenoAuraScaling.clear();
    }
}
