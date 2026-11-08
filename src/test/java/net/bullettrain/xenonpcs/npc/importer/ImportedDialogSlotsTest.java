package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile;
import net.bullettrain.xenonpcs.npc.dialog.NpcDialogSlots;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The importer maps a source NPC's dialogue links onto the converted entity tag, but the runtime
 * reads them from the native profile. A link that only exists on the entity tag is an NPC that
 * imports "successfully" and then has nothing to say.
 */
class ImportedDialogSlotsTest {

    @Test
    void dialogueLinksMappedOntoTheEntityReachTheNativeProfile() {
        CompoundTag entity = new CompoundTag();
        NpcDialogSlots slots = new NpcDialogSlots();
        slots.set(0, "mynpcs_dbz_q1", "dialog_4");
        slots.set(2, "mynpcs_dbz_q1", "dialog_8");
        slots.saveTo(entity);

        NpcCombatProfile profile = NpcCombatProfile.fromTag(PlacedNpcProfileMigrator.profileTag(entity));

        assertEquals("dialog_4", profile.dialogSlots.get(0).id());
        assertEquals("mynpcs_dbz_q1", profile.dialogSlots.get(0).group());
        assertEquals("dialog_8", profile.dialogSlots.get(2).id());
    }
}
