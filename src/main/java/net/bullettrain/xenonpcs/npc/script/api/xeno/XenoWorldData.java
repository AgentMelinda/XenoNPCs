package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * XenoAPI world data. Stored data persists in one overworld SavedData. Temp data is one
 * server-wide map, "the same cross dimension" as the reference documents, cleared at server stop.
 */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID)
public final class XenoWorldData extends SavedData {
    static final String NAME = "xenonpcs_xenoapi_world";
    private static final Map<String, Object> TEMP = new HashMap<>();
    private CompoundTag stored = new CompoundTag();

    static XenoWorldData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(XenoWorldData::new, XenoWorldData::load), NAME);
    }

    private static XenoWorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        XenoWorldData data = new XenoWorldData();
        data.stored = tag.getCompound("Stored").copy();
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("Stored", stored.copy());
        return tag;
    }

    CompoundTag stored() { return stored; }

    void storedChanged(CompoundTag tag) {
        stored = tag;
        setDirty();
    }

    static Map<String, Object> temp() { return TEMP; }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) { TEMP.clear(); }
}
