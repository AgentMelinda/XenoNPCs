package net.bullettrain.xenonpcs.compat.npc;

import net.bullettrain.xenonpcs.npc.importer.NpcImportReport;
import net.bullettrain.xenonpcs.npc.importer.NpcSourceKeys;
import net.bullettrain.xenonpcs.npc.importer.PlacedNpcProfileMigrator;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NpcImportedMovementTest {
    @Test
    void absentSourceAndOlderProfilesKeepNativeMovementAndSprint() {
        var profile = NpcCombatProfile.fromTag(new CompoundTag());
        assertEquals(-1, profile.importedMoveSpeed);
        assertTrue(profile.aiCanSprint);
        var imported = NpcCombatProfile.fromTag(PlacedNpcProfileMigrator.profileTag(new CompoundTag()));
        assertEquals(-1, imported.importedMoveSpeed);
        assertTrue(imported.aiCanSprint);
        assertFalse(imported.toTag().contains("ImportedMoveSpeed"));
    }

    @Test
    void authoredSourceFieldsRoundTripAndUseSourceFloatAttributeFormula() {
        CompoundTag source = new CompoundTag();
        source.putInt("MoveSpeed", 7);
        source.putBoolean("CanSprint", false);
        var profile = NpcCombatProfile.fromTag(PlacedNpcProfileMigrator.profileTag(source));
        assertEquals(7, profile.importedMoveSpeed);
        assertFalse(profile.aiCanSprint);
        assertEquals((double) (7 / 20.0f), NpcImportedMovement.baseSpeed(profile.importedMoveSpeed));
        var reopened = NpcCombatProfile.fromTag(profile.toTag());
        assertEquals(7, reopened.importedMoveSpeed);
        assertFalse(reopened.aiCanSprint);
        var report = new NpcImportReport();
        NpcSourceKeys.audit(source, "npc", report);
        assertTrue(report.notes().isEmpty(), report.notes().toString());
    }

    @Test
    void zeroIsAnExplicitStopAndOutOfRangeSourceSpeedIsBounded() {
        for (int sourceSpeed : new int[] {-50, 0, 100, 500}) {
            CompoundTag source = new CompoundTag();
            source.putInt("MoveSpeed", sourceSpeed);
            var profile = NpcCombatProfile.fromTag(PlacedNpcProfileMigrator.profileTag(source));
            assertEquals(Math.max(0, Math.min(100, sourceSpeed)), profile.importedMoveSpeed);
        }
        assertEquals(0.0, NpcImportedMovement.baseSpeed(0));
        assertEquals(5.0, NpcImportedMovement.baseSpeed(100));
    }
}
