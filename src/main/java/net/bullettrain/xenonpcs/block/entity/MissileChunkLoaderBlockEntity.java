package net.bullettrain.xenonpcs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Force-load tickets for missile corridors. Sparse tick; redstone cached.
 */
public class MissileChunkLoaderBlockEntity extends BlockEntity {
    private boolean alwaysOn = true;
    private int radius = 1; // default 1 (was 2) — cheaper forced chunks
    private boolean cachedRedstone;
    private static final int TICK_INTERVAL = 80; // 4s

    public MissileChunkLoaderBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.MISSILE_CHUNK_LOADER.get(), pos, state); }

    public void onRedstoneChanged(boolean powered) { }

    public void serverTick() { }

    public void toggleAlwaysOn() { }

    public boolean isAlwaysOn() { return false; }

    public int getRadius() { return 0; }

    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) { }

    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) { }
}
