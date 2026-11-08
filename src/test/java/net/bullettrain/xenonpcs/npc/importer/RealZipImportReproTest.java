package net.bullettrain.xenonpcs.npc.importer;

import net.bullettrain.xenonpcs.compat.npc.clone.NpcCloneConverter;
import net.bullettrain.xenonpcs.compat.npc.clone.NpcWorldDataConverter;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Feeds the real CustomNPCs / MyNPCs exports through the importer's own conversion code.
 *
 * <p>Skipped when the sample archives are not next to the project, so it never fails a clean
 * checkout. It reads entries straight out of the zip: a few real group names end in a dot, which
 * Windows cannot create on disk, and that is itself part of what this reproduces.
 */
class RealZipImportReproTest {

    private static Path zip(String name) {
        Path here = Path.of("").toAbsolutePath();
        for (Path p = here; p != null; p = p.getParent()) {
            Path candidate = p.resolve("customnpcs-mynpcs").resolve(name);
            if (Files.isRegularFile(candidate)) return candidate;
        }
        return null;
    }

    private static String nl() {
        return System.lineSeparator();
    }

    private static String read(ZipFile zf, ZipEntry entry) throws Exception {
        try (InputStream in = zf.getInputStream(entry)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void reportWhatImportsFromTheRealArchives() throws Exception {
        for (String mod : new String[] {"customnpcs", "mynpcs"}) {
            Path path = zip(mod + ".zip");
            Assumptions.assumeTrue(path != null, "sample archive missing: " + mod + ".zip");
            NpcImportReport report = new NpcImportReport();
            int statsCopied = 0, clones = 0, cloneFail = 0, texBad = 0, skinUrlSet = 0, modelSet = 0, nsLeft = 0;
            Map<String, Map<Integer, CompoundTag>> dialogGroups = new LinkedHashMap<>();
            int questOk = 0, questNull = 0, parseFail = 0;
            StringBuilder detail = new StringBuilder();
            try (ZipFile zf = new ZipFile(path.toFile())) {
                var entries = zf.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry e = entries.nextElement();
                    String name = e.getName();
                    if (e.isDirectory() || !name.endsWith(".json")) continue;
                    String[] parts = name.split("/");
                    try {
                        if (parts[0].equals("clones")) {
                            CompoundTag clone = ForeignSnbt.parse(read(zf, e));
                            clones++;
                            CompoundTag converted = mod.equals("customnpcs")
                                    ? NpcCloneConverter.convert(clone) : clone;
                            CompoundTag profile = PlacedNpcProfileMigrator.profileTag(converted);
                            String tex = profile.getString("ModelTexture");
                            if (tex.isBlank()) tex = profile.getString("modelTexture");
                            if (tex.startsWith("customnpcs:")) nsLeft++;
                            if (!profile.getString("SkinUrl").isBlank()
                                    || !profile.getString("skinUrl").isBlank()) skinUrlSet++;
                            if (!profile.getString("ModelId").isBlank()
                                    && !profile.getString("modelId").isBlank()) modelSet++;
                            if (profile.getInt("Strength") > 0 || profile.getInt("Vitality") > 0) statsCopied++;
                            if (clones <= 3) detail.append("stats ").append(name).append(": race=").append(profile.getString("Race"))
                                    .append(" STR=").append(profile.getInt("Strength")).append(" VIT=").append(profile.getInt("Vitality"))
                                    .append(" PWR=").append(profile.getInt("KiPower")).append(" ENE=").append(profile.getInt("Energy")).append(nl());
                            if (clones == 1) detail.append("profile keys: ").append(profile.getAllKeys()).append('\n')
                                    .append("sample tex=").append(tex).append('\n');
                        } else if (parts[0].equals("dialogs") && parts.length == 3) {
                            String file = parts[2];
                            int slot = Integer.parseInt(file.substring(0, file.lastIndexOf('.')));
                            CompoundTag tag = ForeignSnbt.parse(read(zf, e));
                            if (mod.equals("customnpcs")) tag = NpcWorldDataConverter.convertStructured(tag);
                            dialogGroups.computeIfAbsent(parts[1], g -> new LinkedHashMap<>()).put(slot, tag);
                        } else if (parts[0].equals("quests") && parts.length == 3) {
                            String file = parts[2];
                            int slot = Integer.parseInt(file.substring(0, file.lastIndexOf('.')));
                            CompoundTag tag = ForeignSnbt.parse(read(zf, e));
                            if (mod.equals("customnpcs")) tag = NpcWorldDataConverter.convertStructured(tag);
                            if (QuestImport.convert(slot, "q" + slot, parts[1], tag, report) != null) questOk++;
                            else questNull++;
                        }
                    } catch (Exception ex) {
                        parseFail++;
                        detail.append("FAIL ").append(name).append(" -> ").append(ex.getClass().getSimpleName())
                                .append(": ").append(ex.getMessage()).append('\n');
                    }
                }
            }
            int dialogsOut = 0, dialogsIn = 0;
            for (var g : dialogGroups.entrySet()) {
                dialogsIn += g.getValue().size();
                dialogsOut += DialogTreeImport.convert(g.getValue(), report, g.getKey(), mod).dialogues().size();
            }
            System.out.println("REPRO[" + mod + "] clones=" + clones + " statsCopied=" + statsCopied + " customnpcsNamespaceLeftInTexture=" + nsLeft
                    + " skinUrlSet=" + skinUrlSet + " modelIdSet=" + modelSet
                    + " | dialogsIn=" + dialogsIn + " dialogTreesOut=" + dialogsOut
                    + " | questsOk=" + questOk + " questsRejected=" + questNull
                    + " | parseFailures=" + parseFail);
            System.out.println(detail);
            for (var f : report.failures()) System.out.println("REPRO[" + mod + "] failure: " + f);
            for (String n : report.notes()) System.out.println("REPRO[" + mod + "] note: " + n);
            assertTrue(clones > 0, "no clones read from " + mod);
        }
    }
}
