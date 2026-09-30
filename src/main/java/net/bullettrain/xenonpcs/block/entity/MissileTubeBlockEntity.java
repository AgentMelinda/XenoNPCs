package net.bullettrain.xenonpcs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Launch tube — no continuous server tick when idle (cooldown uses scheduled block ticks).
 * Slot 0 is the missile body; slot 1 is an optional warhead.
 */
public class MissileTubeBlockEntity extends BlockEntity {
    private int cooldown;
    private boolean armed = true;
    /** 0 = inherit speed from the firing guidance computer. */
    private int speedLevel;
    private int siloClearance = 0;
    /** 0 = unused; else keep ejecting until world Y reaches this. */
    private int launchWorldY;
    private ItemStack missile = null;
    private ItemStack warhead = null;

    public MissileTubeBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.MISSILE_TUBE.get(), pos, state); }

    public static boolean canLaunch(boolean armed, int cooldown, ItemStack body) { return false; }

    public boolean isArmed() { return false; }

    public void setArmed(boolean armed) { }

    public void toggleArmed() { }

    public int getCooldown() { return 0; }

    public boolean isOnCooldown() { return false; }

    public int getSpeedLevel() { return 0; }

    public void setSpeedLevel(int speedLevel) { }

    public int getSiloClearance() { return 0; }

    public void setSiloClearance(int siloClearance) { }

    public int getLaunchWorldY() { return 0; }

    public void setLaunchWorldY(int launchWorldY) { }

    public ItemStack getMissile() { return null; }

    public ItemStack getWarhead() { return null; }

    public boolean isLoaded() { return false; }

    public void tickCooldown() { }

    public boolean tryInsert(Player player, ItemStack held) { return false; }

    public boolean tryExtract(Player player) { return false; }

    public void dropContents() { }

    public Component statusLine(Direction facing) { return null; }

    /**
     * @return true if a missile was spawned
     */
    public boolean tryLaunch(BlockPos target, double boostAccel, int boostTicks,
                             boolean terminal, float yield) { return false; }

    public boolean tryLaunch(BlockPos target, double boostAccel, int boostTicks,
                             boolean terminal, float yield, double gravitySi, double dragCoefficient) { return false; }

    public boolean tryLaunch(BlockPos target, double boostAccel, int boostTicks,
                             boolean terminal, float yield, double gravitySi, double dragCoefficient,
                             int apexY, int cruiseY) { return false; }

    private void markAndSync() { }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { }

    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { }

    private static ItemStack stackFrom(CompoundTag tag, String key, HolderLookup.Provider registries) { return null; }

    public void onLoad() { }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return null; }

    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() { return null; }
}
