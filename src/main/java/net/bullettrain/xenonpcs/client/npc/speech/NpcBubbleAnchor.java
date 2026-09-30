package net.bullettrain.xenonpcs.client.npc.speech;

import net.bullettrain.xenonpcs.client.compat.npc.NpcAppearanceClient;
import net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile;
import net.bullettrain.xenonpcs.npc.XenoNpcEntity;
import net.minecraft.world.entity.LivingEntity;

/** Centered above the rendered NPC, independent of the NPC's facing direction. */
public final class NpcBubbleAnchor {
    private NpcBubbleAnchor() {}
    public static double height(LivingEntity npc) {
        var profile = NpcAppearanceClient.bubbleProfile(npc);
        float hitbox = npc instanceof XenoNpcEntity ? Math.max(0.05f, Math.min(24, profile.hitboxScale)) : 1;
        float size = NpcCombatProfile.visualSizeScale(profile.baseSize);
        return BubbleBillboardGeometry.height(npc.getBbHeight(), hitbox, size, profile.bubbleHeight);
    }
}
