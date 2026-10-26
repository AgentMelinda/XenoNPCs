package net.bullettrain.xenonpcs.network.packet;

import net.minecraftforge.network.NetworkEvent;
import net.bullettrain.xenonpcs.client.combat.ClientChaseFlightState;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/** Server to owner-client chase state, used to pause DMZ's local flight velocity writer. */
public record ChaseFlightStatePacket(boolean active) {
    public ChaseFlightStatePacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(active);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> ClientChaseFlightState.setActive(active));
        ctx.setPacketHandled(true);
    }
}
