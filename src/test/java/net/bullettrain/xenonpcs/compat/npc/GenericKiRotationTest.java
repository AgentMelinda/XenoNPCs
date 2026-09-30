package net.bullettrain.xenonpcs.compat.npc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenericKiRotationTest {
    private NpcCombatProfile profile() {
        var p = new NpcCombatProfile();
        p.brainKiWave = p.brainKiBlast = p.brainKiDisk = true;
        return p;
    }
    @Test void readyBandPredicateAlsoAppliesToGenericWaves() {
        assertNull(NpcBrainKiRotation.pick(profile(), 0, id -> false));
        assertEquals("kiblast", NpcBrainKiRotation.pick(profile(), 0, "kiblast"::equals));
    }
    @Test void GenericAttacksRotateInsteadOfAlwaysUsingOneFallback() {
        assertEquals("kiwave", NpcBrainKiRotation.pick(profile(), 0, id -> true));
        assertEquals("kiblast", NpcBrainKiRotation.pick(profile(), 1, id -> true));
        assertEquals("kienzan", NpcBrainKiRotation.pick(profile(), 2, id -> true));
    }
    @Test void DisabledOrCoolingDownWaveDoesNotPreventOtherAttacks() {
        var p = profile();
        p.brainKiWave = false;
        assertEquals("kiblast", NpcBrainKiRotation.pick(p, 0, id -> true));
        assertEquals("kienzan", NpcBrainKiRotation.pick(profile(), 0, "kienzan"::equals));
    }
}
