package net.bullettrain.xenonpcs.compat.npc;

import java.util.List;

/**
 * Which combat brains an NPC may run - XenoNPCs overlay: only V9.
 *
 * <p>XenoPixels offers every brain version; the public XenoNPCs release offers the V9 brain alone
 * (the DragonMineZ saga tree with its per-action switches). Every stored or requested version reads
 * as V9, the version cycle stays on V9, and the editor shows no version picker.
 */
public final class NpcBrainPolicy {
    private static final List<NpcCombatBrainVersion> CHOICES = List.of(NpcCombatBrainVersion.V9);

    private NpcBrainPolicy() {
    }

    public static List<NpcCombatBrainVersion> choices() {
        return CHOICES;
    }

    public static NpcCombatBrainVersion resolve(NpcCombatBrainVersion requested) {
        return NpcCombatBrainVersion.V9;
    }
}
