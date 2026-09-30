package net.bullettrain.xenonpcs.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = null;

    public static RegistryObject<BlockEntityType<MissileChunkLoaderBlockEntity>> MISSILE_CHUNK_LOADER = null;

    public static RegistryObject<BlockEntityType<CopycatGlowstoneBlockEntity>> COPYCAT_GLOWSTONE = null;

    public static RegistryObject<BlockEntityType<ShipVlsGuidanceBlockEntity>> SHIP_VLS_GUIDANCE = null;

    public static RegistryObject<BlockEntityType<ShipThrusterBlockEntity>> SHIP_THRUSTER = null;

    public static RegistryObject<BlockEntityType<MissileTubeBlockEntity>> MISSILE_TUBE = null;

    public static RegistryObject<BlockEntityType<PilotSeatBlockEntity>> PILOT_SEAT = null;

    /** Shared by {@code WING_PANEL} and its two fixed-orientation subclasses
     * ({@code WingFlapHorizontalBlock}/{@code WingFlapVerticalBlock}) — same block entity, same
     * renderer (which matches on {@code instanceof WingPanelBlock}, true for all three), only
     * where each is allowed to mount differs. */
    public static RegistryObject<BlockEntityType<WingPanelBlockEntity>> WING_PANEL = null;

    private ModBlockEntities() {  }

    public static void register(IEventBus bus) { }
}
