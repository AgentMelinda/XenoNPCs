package net.bullettrain.xenonpcs.network.maker;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.client.ClientScreens;
import net.bullettrain.xenonpcs.dmz.race.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;
import java.util.function.Supplier;

/** Maker administration is independent of the existing sequential NPC packet registry. */
public final class MakerNetwork {
    public static final int MAX_JSON = 128 * 1024;
    public enum Operation { CREATE, UPDATE, ADD_BODY, SAVE_HAIR }
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(XenoNpcsMod.MOD_ID, "maker"))
            .networkProtocolVersion(() -> "1").clientAcceptedVersions("1"::equals)
            .serverAcceptedVersions("1"::equals).simpleChannel();
    private MakerNetwork() {}
    public static void register() {
        CHANNEL.messageBuilder(Request.class, 0, NetworkDirection.PLAY_TO_SERVER).decoder(Request::new)
                .encoder(Request::encode).consumerMainThread(Request::handle).add();
        CHANNEL.messageBuilder(Response.class, 1, NetworkDirection.PLAY_TO_CLIENT).decoder(Response::new)
                .encoder(Response::encode).consumerMainThread(Response::handle).add();
    }
    public static void request(UUID id, Operation operation, String race, String json) {
        CHANNEL.sendToServer(new Request(id, operation, race, json));
    }
    public static void send(ServerPlayer player, Response response) { CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player), response); }
    public record Request(UUID id, Operation operation, String race, String json) {
        public Request(FriendlyByteBuf buf) { this(buf.readUUID(), buf.readEnum(Operation.class), buf.readUtf(32), buf.readUtf(MAX_JSON)); }
        public void encode(FriendlyByteBuf buf) { buf.writeUUID(id); buf.writeEnum(operation); buf.writeUtf(race, 32); buf.writeUtf(json, MAX_JSON); }
        public static void handle(Request packet, Supplier<NetworkEvent.Context> supplier) {
            var context = supplier.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player != null) MakerRaceService.execute(player, packet);
            });
            context.setPacketHandled(true);
        }
    }
    public record Response(UUID id, boolean success, String race, String message, byte[] archive) {
        public Response(FriendlyByteBuf buf) { this(buf.readUUID(), buf.readBoolean(), buf.readUtf(32), buf.readUtf(1024), buf.readByteArray(MakerRaceTransfer.MAX_BYTES)); }
        public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(id); buf.writeBoolean(success); buf.writeUtf(race, 32); buf.writeUtf(message, 1024); buf.writeByteArray(archive);
        }
        public static void handle(Response packet, Supplier<NetworkEvent.Context> supplier) {
            var context = supplier.get();
            context.enqueueWork(() -> ClientScreens.receiveMakerRace.accept(packet));
            context.setPacketHandled(true);
        }
    }
}
