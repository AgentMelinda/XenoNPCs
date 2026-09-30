package net.bullettrain.xenonpcs.missile;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.npc.XenoNpcEntity;
import net.bullettrain.xenonpcs.npc.XenoNpcRole;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/**
 * XenoNPCs entity types: the eight native NPC roles. Kept at this path (XenoPixels registers its
 * NPCs next to its missiles here) so the exported NPC code resolves it unchanged.
 */
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, XenoNpcsMod.MOD_ID);

    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_HUMANOID = npc("xeno_npc_humanoid", XenoNpcRole.HUMANOID);
    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_CREATURE = npc("xeno_npc_creature", XenoNpcRole.CREATURE);
    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_TRADER = npc("xeno_npc_trader", XenoNpcRole.TRADER);
    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_GUARD = npc("xeno_npc_guard", XenoNpcRole.GUARD);
    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_COMPANION = npc("xeno_npc_companion", XenoNpcRole.COMPANION);
    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_QUEST = npc("xeno_npc_quest", XenoNpcRole.QUEST);
    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_TRANSPORTER = npc("xeno_npc_transporter", XenoNpcRole.TRANSPORTER);
    public static final RegistryObject<EntityType<XenoNpcEntity>> XENO_NPC_BANK = npc("xeno_npc_bank", XenoNpcRole.BANK);

    private ModEntities() {}

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        bus.addListener(ModEntities::registerAttributes);
    }

    private static void registerAttributes(
            net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        var attributes = XenoNpcEntity.createAttributes().build();
        event.put(XENO_NPC_HUMANOID.get(), attributes);
        event.put(XENO_NPC_CREATURE.get(), attributes);
        event.put(XENO_NPC_TRADER.get(), attributes);
        event.put(XENO_NPC_GUARD.get(), attributes);
        event.put(XENO_NPC_COMPANION.get(), attributes);
        event.put(XENO_NPC_QUEST.get(), attributes);
        event.put(XENO_NPC_TRANSPORTER.get(), attributes);
        event.put(XENO_NPC_BANK.get(), attributes);
    }

    private static RegistryObject<EntityType<XenoNpcEntity>> npc(String id, XenoNpcRole role) {
        return ENTITIES.register(id, () -> EntityType.Builder
                .of((EntityType<XenoNpcEntity> type, net.minecraft.world.level.Level level) ->
                                new XenoNpcEntity(type, level, role), MobCategory.CREATURE)
                .sized(role.creature() ? 0.9f : 0.6f, role.creature() ? 1.4f : 1.8f)
                .clientTrackingRange(10)
                .updateInterval(3)
                .build(XenoNpcsMod.MOD_ID + ":" + id));
    }

    public static EntityType<XenoNpcEntity> xenoNpcType(XenoNpcRole role) {
        return switch (role == null ? XenoNpcRole.HUMANOID : role) {
            case HUMANOID -> XENO_NPC_HUMANOID.get();
            case CREATURE -> XENO_NPC_CREATURE.get();
            case TRADER -> XENO_NPC_TRADER.get();
            case GUARD -> XENO_NPC_GUARD.get();
            case COMPANION -> XENO_NPC_COMPANION.get();
            case QUEST -> XENO_NPC_QUEST.get();
            case TRANSPORTER -> XENO_NPC_TRANSPORTER.get();
            case BANK -> XENO_NPC_BANK.get();
        };
    }
}
