package net.bullettrain.xenonpcs.compat.npc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 1.20.1: before the NPC stats host existed, every NPC synced its profile into a DragonMineZ blob
 * that dropped the values (DMZ 1.20.1 keeps stats only in the host's attributes) and still stored
 * the sync fingerprint. With the same fingerprint the sync is skipped forever, so the stats would
 * stay 0 on every NPC saved before the fix. The fingerprint carries a host-version term so each of
 * those NPCs re-syncs exactly once.
 */
class NpcStatHostResyncTest {
    private static int preHostFingerprint(int authority, boolean authoritative) {
        return 31 * authority + Boolean.hashCode(authoritative);
    }

    @Test
    void anNpcSyncedBeforeTheHostExistedSyncsAgain() {
        for (int authority : new int[]{0, 1, -7, 123456789}) {
            for (boolean authoritative : new boolean[]{true, false}) {
                assertNotEquals(preHostFingerprint(authority, authoritative),
                        NpcCounterpartSync.statFingerprint(authority, authoritative));
            }
        }
    }

    @Test
    void theFingerprintStaysStableSoItSyncsOnlyOnce() {
        assertEquals(NpcCounterpartSync.statFingerprint(42, true), NpcCounterpartSync.statFingerprint(42, true));
        assertNotEquals(NpcCounterpartSync.statFingerprint(42, true), NpcCounterpartSync.statFingerprint(43, true));
    }
}
