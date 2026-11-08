package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.compat.npc.clone.NpcCloneConverter;
import net.bullettrain.xenonpcs.npc.XenoNpcData;
import net.bullettrain.xenonpcs.npc.XenoNpcRole;
import net.bullettrain.xenonpcs.npc.store.XenoNpcClones;
import net.bullettrain.xenonpcs.npc.store.XenoNpcStoreCategory;
import net.bullettrain.xenonpcs.npc.store.XenoNpcWorldStore;
import net.minecraft.nbt.CompoundTag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Brings My NPCs / CustomNPCs clone files across as native Xeno clone templates.
 *
 * <p>A clone file is where a source NPC's appearance lives: skin, skin URL, model, texture path,
 * and the dialogue links its NPC carries. The import used to read factions, quests and dialogues
 * and skip every clone root outright, so an export imported its text and none of the NPCs that
 * speak it. This runs the same field mapping a placed NPC gets, but without a world entity, and
 * stores the result where {@code /xenonpcclone} and the Cloner read their templates.
 *
 * <p>Must run after the dialogues and quests of the same mod have been imported, because the
 * dialogue links are resolved through the report's slot maps.
 */
final class ForeignCloneImport {

    private static final long MAX_BYTES = 2L * 1024 * 1024;

    private ForeignCloneImport() {
    }

    /**
     * Converts one clone to the payload a native template stores, or null with the reason on the
     * report. Pure with respect to the world, so it can be tested against real clone files.
     */
    static CompoundTag toPayload(CompoundTag clone, String sourceMod, String name, NpcImportReport report) {
        CompoundTag original = clone.copy();
        CompoundTag converted = clone.copy();
        if (sourceMod.equals("customnpcs")
                && !NpcCloneConverter.migrateEntityInPlace(converted)) {
            report.failed("clones", name, "CustomNPC clone did not match the verified source format");
            return null;
        }
        XenoNpcRole role = PlacedNpcImport.role(converted.getInt("Role"));
        converted.putString("Role", role.id());
        converted.putInt("NpcSchema", XenoNpcData.SCHEMA_VERSION);
        String display = converted.getString("Name");
        converted.putString("Name", display.isBlank() ? name : display);
        converted.putString("SourceMod", sourceMod);
        if (converted.contains("FactionID")) {
            String faction = report.faction(sourceMod, converted.getInt("FactionID"));
            converted.putString("Faction", faction == null ? "" : faction);
            converted.remove("FactionID");
        }
        PlacedNpcImport.mapDialogSlots(converted, sourceMod, name, report);

        CompoundTag payload = XenoNpcData.fromTag(converted, role).toTag();
        payload.put("Profile", PlacedNpcProfileMigrator.profileTag(converted));
        NpcSourceKeys.audit(original, sourceMod + "/" + name, report);
        return payload;
    }

    /** Imports every {@code <tab>/<name>.json} under a clones root. */
    static void run(ForeignNpcRoots.Root root, XenoNpcWorldStore store, boolean dryRun,
                    NpcImportReport report) {
        String mod = root.mod().endsWith("-clones")
                ? root.mod().substring(0, root.mod().length() - "-clones".length()) : root.mod();
        List<Path> files;
        try (var walk = Files.walk(root.path(), 2)) {
            files = walk.filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(Path::toString)).toList();
        } catch (IOException e) {
            report.failed("clones", root.mod(), "could not list clone folder: " + e.getMessage());
            return;
        }
        for (Path file : files) {
            String entry = file.toString();
            try {
                if (Files.size(file) > MAX_BYTES) throw new IOException("file exceeds 2 MiB limit");
                Path relative = root.path().relativize(file);
                int tab = 1;
                if (relative.getParent() != null) {
                    try {
                        tab = Integer.parseInt(relative.getParent().toString());
                    } catch (NumberFormatException ignored) {
                        // a non-numeric folder is imported into the first tab
                    }
                }
                String filename = file.getFileName().toString();
                String name = filename.substring(0, filename.lastIndexOf('.'));
                CompoundTag clone = ForeignSnbt.parse(Files.readString(file, StandardCharsets.UTF_8));
                CompoundTag payload = toPayload(clone, mod, name, report);
                if (payload == null) continue;
                String id = new SlotIndex().assign(0, name);
                String group = XenoNpcClones.group(tab);
                if (store.get(XenoNpcStoreCategory.CLONES, group, id) != null) {
                    report.failed("clones", group + "/" + id, "Xeno entry already exists; left unchanged");
                    continue;
                }
                if (dryRun) {
                    report.imported("clones (dry run)", group + "/" + id);
                    continue;
                }
                String refusal = store.put(XenoNpcStoreCategory.CLONES, group, id, payload);
                if (refusal == null) report.imported("clones", group + "/" + id);
                else report.failed("clones", group + "/" + id, refusal);
            } catch (IOException | RuntimeException e) {
                report.failed("clones", entry, e.getMessage() == null ? e.toString() : e.getMessage());
            }
        }
    }
}
