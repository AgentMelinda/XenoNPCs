package net.bullettrain.xenonpcs.npc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.EnumMap;
import java.util.Map;

@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class XenoNpcRoleDefinitions extends SimpleJsonResourceReloadListener {
    private static final String DIRECTORY = "xeno_npcs/roles";
    private static final Gson GSON = new GsonBuilder().create();
    public static final XenoNpcRoleDefinitions INSTANCE = new XenoNpcRoleDefinitions();
    private static volatile Map<XenoNpcRole, XenoNpcRoleDefinition> definitions = defaults();

    private XenoNpcRoleDefinitions() {
        super(GSON, DIRECTORY);
    }

    public static XenoNpcRoleDefinition get(XenoNpcRole role) {
        XenoNpcRole safeRole = role == null ? XenoNpcRole.HUMANOID : role;
        return definitions.getOrDefault(safeRole, XenoNpcRoleDefinition.defaults(safeRole));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
                         ProfilerFiller profiler) {
        EnumMap<XenoNpcRole, XenoNpcRoleDefinition> loaded = new EnumMap<>(XenoNpcRole.class);
        for (XenoNpcRole role : XenoNpcRole.values()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(XenoNpcsMod.MOD_ID, role.id());
            JsonElement json = resources.get(id);
            if (json == null) {
                loaded.put(role, XenoNpcRoleDefinition.defaults(role));
                XenoNpcsMod.LOGGER.warn("Missing native NPC role definition {}; using safe defaults", id);
                continue;
            }
            try {
                loaded.put(role, XenoNpcRoleDefinition.fromJson(json, role));
            } catch (IllegalArgumentException exception) {
                loaded.put(role, XenoNpcRoleDefinition.defaults(role));
                XenoNpcsMod.LOGGER.error("Invalid native NPC role definition {}; using safe defaults: {}",
                        id, exception.getMessage());
            }
        }
        definitions = Map.copyOf(loaded);
        XenoNpcsMod.LOGGER.info("Loaded {} native NPC role definitions", definitions.size());
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    private static Map<XenoNpcRole, XenoNpcRoleDefinition> defaults() {
        EnumMap<XenoNpcRole, XenoNpcRoleDefinition> result = new EnumMap<>(XenoNpcRole.class);
        for (XenoNpcRole role : XenoNpcRole.values()) result.put(role, XenoNpcRoleDefinition.defaults(role));
        return Map.copyOf(result);
    }
}
