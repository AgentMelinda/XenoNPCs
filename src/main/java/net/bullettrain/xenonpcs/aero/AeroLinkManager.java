package net.bullettrain.xenonpcs.aero;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Typed, health-aware view over a controller's paired devices.
 *
 * <p>This deliberately does <b>not</b> own storage. The guidance block entity already
 * persists its pairings under the {@code PairedThrusters} NBT key and the missile code reads
 * that set directly; duplicating it into a second store would break the save contract and
 * risk the two copies diverging. The link manager surveys that existing set and adds the
 * typing, thrust geometry, and health information the flight stack needs.
 */
public final class AeroLinkManager {

    public enum LinkType {
        /** This mod's own thruster block. */
        OWN_THRUSTER,
        /** A thruster from another mod recognized by {@link ExternalThrusterCompat}. */
        EXTERNAL_ENGINE,
        /** Paired position no longer holds a usable engine. */
        MISSING
    }

    /**
     * One paired device.
     *
     * @param pos    block position in the same space as the controller (ship-local on a hull)
     * @param type   what kind of engine is actually there right now
     * @param thrust direction thrust is applied in, or null if unknown
     * @param maxForce per-engine force scale; 0 for external engines whose scale is unknown
     */
    public record Link(BlockPos pos, LinkType type, @Nullable Direction thrust, double maxForce) {
        public boolean healthy() { return false; }
    }

    private AeroLinkManager() {  }

    /**
     * Visit every loaded block entity within {@code radius} of {@code origin}, cheaply.
     *
     * <p>The obvious way to find "the nearest X block" is to walk every position in a cube and
     * ask the level what is there. That cost is cubic in the radius and completely divorced from
     * how much is actually out there: a radius of 48 is nine hundred thousand lookups to find a
     * handful of block entities, which on a right-click is a visible server stall. Chunks already
     * keep a map of exactly the block entities they contain, so walking those maps costs what the
     * answer costs instead of what the search volume costs.
     *
     * <p>Unloaded chunks are skipped rather than generated — a search for something to link to
     * has no business dragging terrain into memory.
     *
     * <p>The visitor must not place or break blocks: it is iterating a chunk's live block-entity
     * map, and mutating the world during that walk is a concurrent modification. Collect the
     * positions and act afterwards, as {@code AeroFlightCore} does for the same reason.
     */
    public static void forEachNearbyBlockEntity(@Nullable Level level, BlockPos origin, int radius,
                                                java.util.function.BiConsumer<BlockPos, BlockEntity> visitor) { }

    /**
     * Classify every paired position. Positions in unloaded chunks are reported
     * {@link LinkType#MISSING} rather than force-loading the world — the caller decides
     * whether that is worth acting on.
     */
    public static List<Link> survey(@Nullable Level level, Collection<BlockPos> paired) { return java.util.List.of(); }

    private static Link classify(Level level, BlockPos pos) { return null; }

    /**
     * Survey the paired set and publish the result onto the bus.
     *
     * <p>Lives here rather than on the host block entity so that {@link AeroBus}'s mutators
     * can stay package-private — only the aero package writes controller state.
     *
     * @return the surveyed links, for callers that need the detail (the mixer does)
     */
    public static List<Link> refresh(@Nullable Level level, Collection<BlockPos> paired, AeroBus bus) { return java.util.List.of(); }

    public static int healthyCount(Collection<Link> links) { return 0; }

    /**
     * Claim every compatible engine near {@code owner} that is on the same ship.
     *
     * <p>The same-ship test is what stops a controller on one hull grabbing the engines of a
     * vessel parked beside it. When the owner is not on a ship at all the test is skipped, so
     * a rig built on the ground still pairs.
     *
     * <p>Ownership is recorded on the engine as the owner's block position, which is why any
     * host with a position — a flight controller or a pilot seat — can own engines.
     *
     * @param paired mutated in place with the newly claimed positions
     * @return how many engines were newly claimed
     */
    public static int pairNearby(@Nullable Level level, BlockPos owner, int radius,
                                 Collection<BlockPos> paired) { return 0; }

    /**
     * Link exactly one engine to this owner — the ship target tool's explicit per-block linker,
     * as opposed to {@link #pairNearby}'s radius sweep.
     *
     * @return true if the target is a compatible engine and was newly added to {@code paired}
     */
    public static boolean pairOne(@Nullable Level level, BlockPos owner, BlockPos target,
                                  Collection<BlockPos> paired) { return false; }

    /**
     * Unlink exactly one engine from this owner, matching the ownership check
     * {@link #clearPaired} uses — a thruster already reclaimed by another controller is not
     * released out from under it.
     *
     * @return true if the target was paired and is now removed
     */
    public static boolean unpairOne(@Nullable Level level, BlockPos owner, BlockPos target,
                                    Collection<BlockPos> paired) { return false; }

    /**
     * Release every engine this owner claimed and forget them.
     *
     * <p>Only engines still pointing at this owner are released: a thruster that has since been
     * claimed by another controller belongs to that one now, and stealing it back would leave
     * the other controller commanding an engine it no longer owns.
     *
     * @return how many pairings were dropped
     */
    public static int clearPaired(@Nullable Level level, BlockPos owner, Collection<BlockPos> paired) { return 0; }

    /** Sub-level id at a position, or -1 when it is not on a ship (or Sable is unhappy). */
    private static long shipIdAt(Level level, BlockPos pos) { return 0L; }

    /**
     * Drop commanded thrust on every link. Used when disengaging flight, losing power, or
     * on emergency stop. External engines are throttled to zero through their compat hook;
     * own thrusters release guidance ownership, which already zeroes their physics entry.
     */
    public static void releaseAll(@Nullable Level level, Collection<BlockPos> paired) { }
}
