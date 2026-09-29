package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import xenoapi.npcs.api.IContainer;
import xenoapi.npcs.api.INbt;
import xenoapi.npcs.api.IPos;
import xenoapi.npcs.api.IWorld;
import xenoapi.npcs.api.block.IBlock;
import xenoapi.npcs.api.entity.IEntityLiving;
import xenoapi.npcs.api.entity.data.IData;

import java.util.Objects;

/** A block position as XenoAPI's IBlock, read-mostly in sub-project 1. The state is read live. */
public final class XenoBlockAdapter implements IBlock {
    private final ServerLevel level;
    private final BlockPos pos;

    public XenoBlockAdapter(ServerLevel level, BlockPos pos) {
        this.level = Objects.requireNonNull(level);
        this.pos = Objects.requireNonNull(pos).immutable();
    }

    /** Reads can load chunks, so they too stay on the server thread. */
    private BlockState state() {
        XenoApiAdapters.requireServerThread(level);
        return level.getBlockState(pos);
    }

    private BlockEntity blockEntity() {
        XenoApiAdapters.requireServerThread(level);
        return level.getBlockEntity(pos);
    }

    @Override public int getX() { return pos.getX(); }
    @Override public int getY() { return pos.getY(); }
    @Override public int getZ() { return pos.getZ(); }
    @Override public IPos getPos() { return new XenoPosAdapter(pos); }
    @Override public String getName() { return BuiltInRegistries.BLOCK.getKey(state().getBlock()).toString(); }
    @Override public String getDisplayName() { return state().getBlock().getName().getString(); }
    @Override public boolean isAir() { return state().isAir(); }
    /** True once the block at this position is gone (air). */
    @Override public boolean isRemoved() { return state().isAir(); }
    @Override public IWorld getWorld() { return XenoApiAdapters.wrap(level); }
    @Override public boolean hasTileEntity() { return blockEntity() != null; }
    @Override public boolean isContainer() { return blockEntity() instanceof Container; }

    @Override
    public IContainer getContainer() {
        return blockEntity() instanceof Container container ? XenoContainerAdapter.of(container) : null;
    }

    /** The property's current value as text, or null for a property this block does not have. */
    @Override
    public Object getProperty(String name) {
        BlockState state = state();
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals(name)) return String.valueOf(state.getValue(property));
        }
        return null;
    }

    @Override
    public String[] getProperties() {
        return state().getProperties().stream().map(Property::getName).toArray(String[]::new);
    }

    /** A detached snapshot of the block entity's save data; null without a block entity. */
    @Override
    public INbt getBlockEntityNBT() {
        BlockEntity be = blockEntity();
        return be == null ? null : XenoApiAdapters.wrap(be.saveWithFullMetadata(level.registryAccess()));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof XenoBlockAdapter block && block.level == level && block.pos.equals(pos);
    }

    @Override public int hashCode() { return pos.hashCode(); }

    // ------------------------------------------------------------------ unsupported until sub-project 4

    @Override public void setProperty(String name, Object val) { throw XenoApiAdapters.unsupported("IBlock.setProperty"); }
    @Override public void remove() { throw XenoApiAdapters.unsupported("IBlock.remove (use IWorld.removeBlock)"); }
    @Override public IBlock setBlock(String name) { throw XenoApiAdapters.unsupported("IBlock.setBlock"); }
    @Override public IBlock setBlock(IBlock block) { throw XenoApiAdapters.unsupported("IBlock.setBlock"); }
    @Override public IData getTempdata() { throw XenoApiAdapters.unsupported("IBlock.getTempdata"); }
    @Override public IData getStoreddata() { throw XenoApiAdapters.unsupported("IBlock.getStoreddata"); }
    @Override public void setTileEntityNBT(INbt nbt) { throw XenoApiAdapters.unsupported("IBlock.setTileEntityNBT"); }
    @Override public BlockEntity getMCTileEntity() { throw XenoApiAdapters.unsupported("IBlock.getMCTileEntity (raw handles are not exposed)"); }
    @Override public Block getMCBlock() { throw XenoApiAdapters.unsupported("IBlock.getMCBlock (raw handles are not exposed)"); }
    @Override public BlockState getMCBlockState() { throw XenoApiAdapters.unsupported("IBlock.getMCBlockState (raw handles are not exposed)"); }
    @Override public void blockEvent(int type, int data) { throw XenoApiAdapters.unsupported("IBlock.blockEvent"); }
    @Override public void interact(int side, IEntityLiving entity) { throw XenoApiAdapters.unsupported("IBlock.interact"); }
    @Override public void setChanged() { throw XenoApiAdapters.unsupported("IBlock.setChanged"); }
}
