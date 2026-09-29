package net.bullettrain.xenonpcs.npc.script.api;

import net.bullettrain.xenonpcs.capability.XenoCapabilities;
import net.bullettrain.xenonpcs.features.progression.ActiveQuest;
import net.bullettrain.xenonpcs.features.progression.ParallelQuests;
import net.bullettrain.xenonpcs.npc.store.XenoNpcStoreCategory;
import net.bullettrain.xenonpcs.npc.store.XenoNpcStores;
import net.bullettrain.xenonpcs.npc.store.XenoNpcWorldStore;
import net.minecraft.server.level.ServerPlayer;

/** Narrow counterpart for the MyNPCs quest lookup used by the bundled examples. */
public final class ScriptApi {
    public Quests getQuests() { return new Quests(); }

    public static String questId(int sourceSlot) {
        XenoNpcWorldStore store = XenoNpcStores.get();
        if (store == null) return null;
        String match = null;
        for (XenoNpcWorldStore.Entry entry : store.list(XenoNpcStoreCategory.QUESTS)) {
            var tag = entry.tag();
            boolean mapped = tag.contains("SourceSlot") && tag.getInt("SourceSlot") == sourceSlot
                    && ("mynpcs".equalsIgnoreCase(tag.getString("SourceMod"))
                    || "customnpcs".equalsIgnoreCase(tag.getString("SourceMod")));
            if (!mapped && !tag.contains("SourceSlot")) {
                mapped = entry.id().matches(".+_q" + sourceSlot);
            }
            if (!mapped) continue;
            if (match != null && !match.equals(entry.id())) return null;
            match = entry.id();
        }
        return match;
    }

    public static final class Quests {
        public Quest get(int sourceSlot) {
            String id = questId(sourceSlot);
            return id == null || ParallelQuests.definition(id) == null ? null : new Quest(id);
        }
    }

    public record Quest(String id) {
        public Objective[] getObjectives(ScriptPlayer player) {
            if (player == null) return new Objective[0];
            ParallelQuests.QuestDef def = ParallelQuests.definition(id);
            if (def == null) return new Objective[0];
            ActiveQuest active = XenoCapabilities.get((ServerPlayer) player.unwrap())
                    .map(data -> data.quests().active(id)).orElse(null);
            Objective[] out = new Objective[def.steps().size()];
            for (int i = 0; i < out.length; i++) {
                int index = i;
                boolean complete = active != null && active.stepProgress(index) >= def.steps().get(index).target();
                out[i] = new Objective(complete);
            }
            return out;
        }
    }

    public record Objective(boolean completed) {
        public boolean isCompleted() { return completed; }
    }
}
