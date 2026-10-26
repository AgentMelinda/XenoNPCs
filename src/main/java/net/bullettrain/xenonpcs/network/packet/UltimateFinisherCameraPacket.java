package net.bullettrain.xenonpcs.network.packet;

import com.dragonminez.compat.network.NetworkEvent;
import net.bullettrain.xenonpcs.combat.v2.UltimateFinisherRules;
import net.minecraft.network.FriendlyByteBuf;
import java.util.function.Supplier;

/** Bounded, server-to-caster cinematic state; no client authority over the attack. */
public record UltimateFinisherCameraPacket(int targetId, int sequence, UltimateFinisherRules.Phase phase) {
    public UltimateFinisherCameraPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), buf.readEnum(UltimateFinisherRules.Phase.class));
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(targetId);
        buf.writeVarInt(sequence);
        buf.writeEnum(phase);
    }
    public static void handle(UltimateFinisherCameraPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> net.bullettrain.xenonpcs.client.camera.UltimateFinisherCamera.apply(packet));
        context.get().setPacketHandled(true);
    }
}
