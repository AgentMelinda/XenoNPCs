package net.bullettrain.xenonpcs.block.entity;

import net.bullettrain.xenonpcs.aero.AeroBus;
import net.bullettrain.xenonpcs.aero.AeroControlHost;
import net.bullettrain.xenonpcs.aero.AeroLinkManager;
import net.bullettrain.xenonpcs.aero.control.AeroFlightCore;
import net.bullettrain.xenonpcs.aero.v2.GuidanceV2Core;
import net.bullettrain.xenonpcs.aero.v2.GuidanceV2State;
import net.bullettrain.xenonpcs.aero.power.AeroEnergyStorage;
import net.bullettrain.xenonpcs.missile.BallisticFlightPlan;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A pilot seat that can fly a ship on its own.
 *
 * <p>A seat next to a flight controller simply operates that controller. This block entity is
 * the other case: a small craft with a seat, some engines and no console. It is a second
 * {@link AeroControlHost}, so it owns a bus, a power buffer and its own engine pairings, and the
 * pilot's input reaches it through the same dispatcher with the same validation.
 *
 * <p><b>What it deliberately cannot do:</b> autopilot, routes and targets. Those belong to the
 * flight controller, which has the planner, the waypoints and the missile stack behind them. A
 * standalone seat is manual flight only — the setpoint always comes from a live pilot — which is
 * also why it engages when someone sits down and disengages when they leave. An unattended seat
 * should not be flying.
 */
public class PilotSeatBlockEntity extends BlockEntity  implements AeroControlHost {

    /** How far the seat looks for engines to claim. */
    private static final int ENGINE_SEARCH_RADIUS = 12;

    private AeroBus aeroBus = null;
    private AeroEnergyStorage aeroEnergy = null;
    private AeroFlightCore aeroCore = null;
    private GuidanceV2Core guidanceV2Core = null;
    private GuidanceV2State guidanceV2 = null;

    private Set<BlockPos> pairedThrusters = null;
    private Set<BlockPos> linkedPanels = null;
    private List<AeroLinkManager.Link> aeroLinks = null;
    private int aeroLinkCooldown;
    private boolean aeroCommanding;

    private long cachedShipId = -1L;
    private int shipCacheCooldown;

    private transient Vector3d bodyNose = null;
    private transient Vector3d bodyUp = null;

    public PilotSeatBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.PILOT_SEAT.get(), pos, state); }

    public AeroBus aeroBus() { return null; }

    public BlockPos hostPos() { return null; }

    /** Exposed for the {@code Capabilities.EnergyStorage.BLOCK} provider. */
    public AeroEnergyStorage aeroEnergy() { return null; }

    public void syncAero() { }

    public GuidanceV2State guidanceV2() { return null; }

    public boolean isCommandingFlight() { return false; }

    public List<BlockPos> getPairedThrusters() { return java.util.List.of(); }

    public int pairNearbyThrusters() { return 0; }

    public int clearPairedThrusters() { return 0; }

    public boolean pairOneThruster(BlockPos pos) { return false; }

    public boolean unpairOneThruster(BlockPos pos) { return false; }

    public void refreshAeroLinks() { }

    public @Nullable BlockPos getTarget() { return null; }

    public BallisticFlightPlan.Settings getPlannerSettings() { return null; }

    public Set<BlockPos> getLinkedPanels() { return java.util.Set.of(); }

    public boolean linkPanel(BlockPos pos) { return false; }

    public boolean unlinkPanel(BlockPos pos) { return false; }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state,
                                  PilotSeatBlockEntity seat) { }

    private void tick(ServerLevel serverLevel) { }

    /**
     * Which way the craft points, in ship-local space.
     *
     * <p>The seat's own facing is the nose: you fly the way the pilot is sitting. That is both
     * the obvious reading for a builder and the only ship-local direction the seat actually has.
     */
    private Vector3dc noseVector() { return null; }

    /** Called by the seat entity when a pilot sits down on a seat with no controller of its own. */
    public void onPilotMounted() { }

    /**
     * Called when the pilot leaves. An unattended standalone seat must not keep flying.
     *
     * <p>{@link AeroFlightCore#release} is called directly here rather than left for {@link #tick}
     * to notice {@code active=false} on its own next pass — an abrupt dismount (a forced teleport,
     * not a normal shift-key exit) leaves a one-tick window where this ship's continuous
     * force/torque systems could still write a force computed from now-stale, discontinuous
     * inputs into its rigid body. Sable deletes a sub-level outright if its physics pipeline ever
     * reports a NaN pose for it, so that window is not just a cosmetic stutter — closing it here,
     * synchronously, is what actually stops a teleport from being able to destroy the ship.
     */
    public void onPilotDismounted() { }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return null; }

    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() { return null; }
}
