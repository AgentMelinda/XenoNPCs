package net.bullettrain.xenonpcs.block.entity;

import net.minecraftforge.energy.IEnergyStorage;

/** Receive-only, server-configurable FE buffer for copycat glowstone. */
public final class CopycatEnergyStorage implements IEnergyStorage {
    private Runnable changed;
    private int stored;

    CopycatEnergyStorage(Runnable changed) {  }

    public int receiveEnergy(int amount, boolean simulate) { return 0; }

    public int extractEnergy(int amount, boolean simulate) { return 0; }
    public int getEnergyStored() { return 0; }
    public int getMaxEnergyStored() { return 0; }
    public boolean canExtract() { return false; }
    public boolean canReceive() { return false; }

    boolean consume(int amount) { return false; }

    void setStored(int amount) { }
}
