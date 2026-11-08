package net.bullettrain.xenonpcs.compat.npc;

import java.util.List;

/**
 * Which combat brains an NPC may run - XenoNPCs overlay: only V9.
 *
 * <p>The public editor exposes the pure DragonMineZ saga brain (V8) and the configurable V9
 * variant. Other stored versions retain the historical V9 fallback.
 */
public final class NpcBrainPolicy {
    private static final List<NpcCombatBrainVersion> CHOICES =
            List.of(NpcCombatBrainVersion.V8, NpcCombatBrainVersion.V9);

    private NpcBrainPolicy() {
    }

    public static List<NpcCombatBrainVersion> choices() {
        return CHOICES;
    }

    public static NpcCombatBrainVersion resolve(NpcCombatBrainVersion requested) {
        return requested == NpcCombatBrainVersion.V8 || requested == NpcCombatBrainVersion.V9
                ? requested : NpcCombatBrainVersion.V9;
    }
}
