package net.bullettrain.xenonpcs.compat.npc;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** XenoNPCs offers the DMZ saga brain and the configurable V9 brain. */
class BrainPolicyTest {

    @Test
    void dmzAndV9BrainsAreOffered() {
        assertEquals(List.of(NpcCombatBrainVersion.V8, NpcCombatBrainVersion.V9), NpcBrainPolicy.choices());
        for (NpcCombatBrainVersion v : NpcCombatBrainVersion.values()) {
            NpcCombatBrainVersion expected = v == NpcCombatBrainVersion.V8
                    || v == NpcCombatBrainVersion.V9 ? v : NpcCombatBrainVersion.V9;
            assertSame(expected, NpcBrainPolicy.resolve(v));
        }
        assertSame(NpcCombatBrainVersion.V9, NpcBrainPolicy.resolve(null));
    }

    @Test
    void profileCanSelectBothBrainsAndPersistsDifficulty() {
        NpcCombatProfile p = new NpcCombatProfile();
        assertSame(NpcCombatBrainVersion.V9, p.brainVersion, "a new profile");
        p.setBrainVersion(NpcCombatBrainVersion.V1);
        assertSame(NpcCombatBrainVersion.V9, p.brainVersion, "asking for another brain");
        p.setBrainVersion(NpcCombatBrainVersion.V8);
        p.setDmzAiDifficulty(4);
        NpcCombatProfile restored = NpcCombatProfile.fromTag(p.toTag());
        assertSame(NpcCombatBrainVersion.V8, restored.brainVersion);
        assertEquals(4, restored.dmzAiDifficulty);
        CompoundTag old = new CompoundTag();
        old.putInt("BrainVersion", 3);
        assertSame(NpcCombatBrainVersion.V9, NpcCombatProfile.fromTag(new CompoundTag()).brainVersion,
                "a save without a brain");
        assertSame(NpcCombatBrainVersion.V9, NpcCombatBrainVersion.V9.next(), "unsupported versions resolve to V9");
    }
}
