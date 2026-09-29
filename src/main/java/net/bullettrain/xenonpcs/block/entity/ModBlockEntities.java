package net.bullettrain.xenonpcs.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = null;

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MissileChunkLoaderBlockEntity>> MISSILE_CHUNK_LOADER = null;

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<CopycatGlowstoneBlockEntity>> COPYCAT_GLOWSTONE = null;

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ShipVlsGuidanceBlockEntity>> SHIP_VLS_GUIDANCE = null;

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ShipThrusterBlockEntity>> SHIP_THRUSTER = null;

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MissileTubeBlockEntity>> MISSILE_TUBE = null;

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<PilotSeatBlockEntity>> PILOT_SEAT = null;

    /** Shared by {@code WING_PANEL} and its two fixed-orientation subclasses
     * ({@code WingFlapHorizontalBlock}/{@code WingFlapVerticalBlock}) — same block entity, same
     * renderer (which matches on {@code instanceof WingPanelBlock}, true for all three), only
     * where each is allowed to mount differs. */
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<WingPanelBlockEntity>> WING_PANEL = null;

    private ModBlockEntities() {  }

    public static void register(IEventBus bus) { }
}
