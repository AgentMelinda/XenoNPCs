package net.bullettrain.xenonpcs.aero.power;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * The flight controller's FE buffer.
 *
 * <p><b>Receive-only.</b> {@link #canExtract()} is false and {@link #extractEnergy} is a
 * no-op, so no adjacent machine can siphon the controller's reserve. A ship whose flight
 * authority can be drained by a neighbouring cable is a griefing vector, and the buffer exists
 * to survive load spikes rather than to act as a battery for the rest of the base.
 *
 * <p>Capacity and input rate come from {@link AeroConfig} and are read live, so
 * {@code /reload}-style config edits take effect without rebuilding the block entity. Stored
 * energy is clamped on every read of the limits in case an operator lowers capacity below the
 * current charge.
 */
public final class AeroEnergyStorage implements IEnergyStorage {
    private int stored;

    public AeroEnergyStorage() {  }

    public int receiveEnergy(int toReceive, boolean simulate) { return 0; }

    /** Always zero — see the class javadoc. */
    public int extractEnergy(int toExtract, boolean simulate) { return 0; }

    public int getEnergyStored() { return 0; }

    public int getMaxEnergyStored() { return 0; }

    public boolean canExtract() { return false; }

    public boolean canReceive() { return false; }

    private int maxReceive() { return 0; }

    private int clampedStored() { return 0; }

    /**
     * Internal draw for our own tick. Not reachable through the capability, so other mods
     * cannot call it.
     *
     * @return how much was actually consumed, which may be less than requested
     */
    public int consume(int amount) { return 0; }

    /** True when the buffer could sustain {@code amount} this tick. */
    public boolean canAfford(int amount) { return false; }

    public void setStored(int value) { }
}
