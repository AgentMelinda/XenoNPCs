package net.bullettrain.xenonpcs.network.race;

import com.dragonminez.compat.network.NetworkDirection;
import com.dragonminez.compat.network.NetworkEvent;
import com.dragonminez.compat.network.NetworkRegistry;
import com.dragonminez.compat.network.simple.SimpleChannel;
import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.dmz.race.RaceLabelRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

/**
 * Syncs operator-authored race picker literals. Dedicated-server clients never see
 * {@code config/dragonminez/races/<id>/xeno_labels.json} otherwise.
 */
public final class RaceLabelNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(XenoNpcsMod.MOD_ID, "race_labels"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();
    private static boolean registered;

    private RaceLabelNetwork() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }
        registered = true;
        CHANNEL.messageBuilder(SnapshotPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SnapshotPacket::new).encoder(SnapshotPacket::encode)
                .consumerMainThread(SnapshotPacket::handle).add();
    }

    public static void sendSnapshot(ServerPlayer player) {
        CHANNEL.sendToPlayer(new SnapshotPacket(RaceLabelRegistry.snapshotJson()), player);
    }

    public static void broadcast(ServerPlayer source) {
        if (source == null || source.getServer() == null) {
            return;
        }
        SnapshotPacket packet = new SnapshotPacket(RaceLabelRegistry.snapshotJson());
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            CHANNEL.sendToPlayer(packet, player);
        }
    }

    public static void broadcast(net.minecraft.server.MinecraftServer server) {
        if (server == null) {
            return;
        }
        SnapshotPacket packet = new SnapshotPacket(RaceLabelRegistry.snapshotJson());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            CHANNEL.sendToPlayer(packet, player);
        }
    }

    public record SnapshotPacket(String json) {
        public SnapshotPacket(FriendlyByteBuf buffer) {
            this(buffer.readUtf(RaceLabelRegistry.MAX_JSON_CHARS));
        }

        public void encode(FriendlyByteBuf buffer) {
            String payload = json == null ? "{}" : json;
            if (payload.length() > RaceLabelRegistry.MAX_JSON_CHARS) {
                payload = "{}";
            }
            buffer.writeUtf(payload, RaceLabelRegistry.MAX_JSON_CHARS);
        }

        public static void handle(SnapshotPacket packet, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> RaceLabelRegistry.applySnapshot(packet.json));
            context.setPacketHandled(true);
        }
    }
}
