package net.bullettrain.xenonpcs.block.entity;

import net.neoforged.neoforge.energy.IEnergyStorage;

/** Receive-only, server-configurable FE buffer for copycat glowstone. */
public final class CopycatEnergyStorage implements IEnergyStorage {
    private Runnable changed;
    private int stored;

    CopycatEnergyStorage(Runnable changed) {  }

    @Override
    public int receiveEnergy(int amount, boolean simulate) { return 0; }

    @Override public int extractEnergy(int amount, boolean simulate) { return 0; }
    @Override public int getEnergyStored() { return 0; }
    @Override public int getMaxEnergyStored() { return 0; }
    @Override public boolean canExtract() { return false; }
    @Override public boolean canReceive() { return false; }

    boolean consume(int amount) { return false; }

    void setStored(int amount) { }
}
