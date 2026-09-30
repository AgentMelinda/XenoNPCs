package net.bullettrain.xenonpcs.client.npc;

import com.mojang.authlib.GameProfile;
import net.bullettrain.xenonpcs.client.compat.npc.NpcAppearanceClient;
import net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile;
import net.bullettrain.xenonpcs.npc.XenoNpcEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/** Resolves a native NPC's player-name skin from the server-synced account UUID. */
final class NpcPlayerSkinClient {
    private NpcPlayerSkinClient() {
    }

    static ResourceLocation texture(XenoNpcEntity entity, NpcCombatProfile profile) {
        if (entity == null || profile == null || profile.skinPlayer == null
                || profile.skinPlayer.isBlank()) return null;
        NpcAppearanceClient.State state = NpcAppearanceClient.get(entity.getUUID());
        if (state == null || state.skinUuid() == null || state.skinUuid().isBlank()
                || !profile.skinPlayer.equalsIgnoreCase(state.skinPlayer())) return null;
        try {
            UUID id = UUID.fromString(state.skinUuid());
            return Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(
                    new GameProfile(id, profile.skinPlayer));
        } catch (IllegalArgumentException ignored) {
            // The server has not resolved this name yet; keep the configured texture meanwhile.
            return null;
        }
    }
}
