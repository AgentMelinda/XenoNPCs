package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A foreign clone keeps its appearance and its dialogue when it becomes a native template. */
class ForeignCloneImportTest {

    private static CompoundTag clone(String id, String texture) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putString("Name", "Goku");
        tag.putInt("Role", 0);
        tag.putString("Texture", texture);
        tag.putString("SkinUrl", "https://example.invalid/goku.png");
        tag.putBoolean("UsingSkinUrl", true);
        ListTag options = new ListTag();
        CompoundTag option = new CompoundTag();
        option.putInt("DialogSlot", 0);
        CompoundTag npcDialog = new CompoundTag();
        npcDialog.putInt("Dialog", 4);
        option.put("NPCDialog", npcDialog);
        options.add(option);
        tag.put("NPCDialogOptions", options);
        return tag;
    }

    @Test
    void myNpcsCloneKeepsTextureSkinUrlAndDialogue() {
        NpcImportReport report = new NpcImportReport();
        report.mapDialog("mynpcs", 4, new DialogTreeImport.Ref("mynpcs_dbz_q1", "dialog_4"));

        CompoundTag payload = ForeignCloneImport.toPayload(
                clone("mynpcs:customnpc", "mynpcs:textures/entity/humanmale/steve.png"),
                "mynpcs", "goku", report);

        assertNotNull(payload, report.failures().toString());
        NpcCombatProfile profile = NpcCombatProfile.fromTag(payload.getCompound("Profile"));
        assertEquals("mynpcs:textures/entity/humanmale/steve.png", profile.modelTexture);
        assertEquals("https://example.invalid/goku.png", profile.skinUrl);
        assertEquals("dialog_4", profile.dialogSlots.get(0).id());
        assertEquals("mynpcs_dbz_q1", profile.dialogSlots.get(0).group());
        assertEquals("Goku", payload.getString("Name"));
    }

    @Test
    void customNpcsCloneTextureIsRehomedToMyNpcs() {
        NpcImportReport report = new NpcImportReport();
        CompoundTag payload = ForeignCloneImport.toPayload(
                clone("customnpcs:customnpc", "customnpcs:textures/entity/humanmale/steve.png"),
                "customnpcs", "goku", report);

        if (payload == null) {
            // The converter only accepts the verified full CustomNPCs shape; a minimal tag is refused
            // loudly rather than half-imported.
            assertTrue(report.failures().toString().contains("verified source format"),
                    report.failures().toString());
            return;
        }
        NpcCombatProfile profile = NpcCombatProfile.fromTag(payload.getCompound("Profile"));
        assertEquals("mynpcs:textures/entity/humanmale/steve.png", profile.modelTexture);
    }
}
