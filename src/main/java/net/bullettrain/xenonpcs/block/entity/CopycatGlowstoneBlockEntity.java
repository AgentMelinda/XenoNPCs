package net.bullettrain.xenonpcs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

/** Stores and synchronizes the complete state used as the copycat material. */
public final class CopycatGlowstoneBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity {
    public static ModelProperty<BlockState> MATERIAL_MODEL_PROPERTY = null;
    private static final String MATERIAL_TAG = "Material";
    private static final String ENERGY_TAG = "Energy";
    private BlockState material;
    private boolean energyDirty;
    private CopycatEnergyStorage energy = null;

    public CopycatGlowstoneBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.COPYCAT_GLOWSTONE.get(), pos, state); }

    public BlockState getMaterial() { return null; }

    public boolean hasCustomMaterial() { return false; }

    public CopycatEnergyStorage energy() { return null; }

    /** Draws configured FE and mirrors active lighting into block state for vanilla light updates. */
    public void serverTick(Level level, BlockPos pos, BlockState state) { }

    /** Applies a new block type, or cycles its orientation when the same type is clicked again. */
    public boolean applyMaterial(BlockState newMaterial) { return false; }

    public boolean setMaterial(BlockState newMaterial) { return false; }

    public boolean resetMaterial() { return false; }

    private void markMaterialChanged() { }

    /** One local chunk redraw after authoritative BE data arrives; no neighbour packet storm. */
    private void redrawClient() { }

    public ModelData getModelData() { return null; }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { }

    public void onLoad() { }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return null; }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return null; }
}
