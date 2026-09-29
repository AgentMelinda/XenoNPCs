package net.bullettrain.xenonpcs.compat.npc;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** XenoNPCs offers only the V9 combat brain. */
class BrainPolicyTest {

    @Test
    void onlyV9IsOffered() {
        assertEquals(List.of(NpcCombatBrainVersion.V9), NpcBrainPolicy.choices());
        for (NpcCombatBrainVersion v : NpcCombatBrainVersion.values()) {
            assertSame(NpcCombatBrainVersion.V9, NpcBrainPolicy.resolve(v));
        }
        assertSame(NpcCombatBrainVersion.V9, NpcBrainPolicy.resolve(null));
    }

    @Test
    void everyProfileRunsV9() {
        NpcCombatProfile p = new NpcCombatProfile();
        assertSame(NpcCombatBrainVersion.V9, p.brainVersion, "a new profile");
        p.setBrainVersion(NpcCombatBrainVersion.V1);
        assertSame(NpcCombatBrainVersion.V9, p.brainVersion, "asking for another brain");
        CompoundTag old = new CompoundTag();
        old.putInt("BrainVersion", 3);
        assertSame(NpcCombatBrainVersion.V9, NpcCombatProfile.fromTag(new CompoundTag()).brainVersion,
                "a save without a brain");
        assertSame(NpcCombatBrainVersion.V9, NpcCombatBrainVersion.V9.next(), "the cycle stays on V9");
    }
}
