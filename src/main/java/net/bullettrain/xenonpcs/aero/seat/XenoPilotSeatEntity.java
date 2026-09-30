package net.bullettrain.xenonpcs.aero.seat;

import net.bullettrain.xenonpcs.aero.AeroControlHost;
import net.bullettrain.xenonpcs.block.entity.PilotSeatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * The thing a pilot actually sits on.
 *
 * <p>It is deliberately almost inert: no physics, no collision, no gravity, no AI. Its whole job
 * is to be a mount whose rider is recognisably "the pilot of that controller", and to forward
 * that pilot's control frames to {@link AeroActionDispatcher} — the same choke point the GUI,
 * the physical panel and the ComputerCraft peripheral all go through. It never touches a physics
 * body and never moves a ship itself.
 *
 * <p><b>Why an entity and not just a piloting flag.</b> Riding is what makes the player's body
 * move with the vessel, third-person and first-person cameras behave, and other players see who
 * is flying. Sable carries and rotates entities on moving sub-levels, so the seat needs no
 * transform code of its own.
 *
 * <p><b>The seat must be tagged {@code sable:retain_in_sub_level}</b> — see
 * {@code data/sable/tags/entity_type/}. Sable treats any entity type outside that tag as
 * something to eject from a sub-level, so without the tag the seat is pushed out into world
 * space: it stops moving with the hull and the rider is drawn world-upright no matter how the
 * ship is banked or inverted. This is the same integration Create's own seat uses, which is why
 * {@code create:seat} appears in Sable's copy of the tag. The seat is also tagged
 * {@code sable:destroy_with_sub_level} so it does not outlive the craft it belongs to.
 *
 * <p>Because the seat lives in the sub-level's own coordinate space, its rotation is stored
 * hull-relative (the seat block's facing) and Sable applies the hull's orientation at render
 * time. Do not add world-space rotation here — it would be applied twice.
 *
 * <p><b>Safety.</b> The seat commands idle whenever its input goes stale (see
 * {@link AeroSeatInput#STALE_TICKS}) or its rider leaves, so a pilot who disconnects mid-throttle
 * does not leave a ship climbing forever.
 */
public class XenoPilotSeatEntity extends Entity {

    /** How often the pilot is sent the authoritative controller snapshot for their HUD. */
    private static final int STATE_PUSH_INTERVAL = 4;
    /** Ticks with no rider before the seat cleans itself up. */
    private static final int EMPTY_GRACE_TICKS = 20;
    /** How far from the seat block a flight controller may be and still be flyable from it. */
    private static final int CONTROLLER_SEARCH_RADIUS = 8;

    private @Nullable BlockPos seatBlock;
    private @Nullable BlockPos controllerPos;

    private AeroSeatInput input = null;
    private int emptyTicks;
    private int statePushTicks;
    /** Set while the seat is holding the controller at idle, so it stops re-sending it. */
    private boolean idleCommanded;
    /**
     * Set when this seat itself was the one that engaged a bound controller's flight subsystem
     * on mount, so it knows to disengage it again on dismount. An operator's own engagement
     * (from the GUI, mid-autopilot) must never be touched by someone merely sitting down or
     * getting up — only what this seat turned on, this seat turns back off.
     */
    private boolean engagedByThisSeat;

    public XenoPilotSeatEntity(EntityType<?> type, Level level) { super(type, level); }

    public void bind(BlockPos seatBlock, @Nullable BlockPos controllerPos) { }

    public @Nullable BlockPos controllerPos() { return null; }

    public AeroSeatInput input() { return null; }

    /**
     * Whoever this seat is flying: the linked flight controller if there is one, otherwise the
     * seat block itself, which is a control host in its own right.
     *
     * <p>Resolved fresh each time rather than cached. On a ship the controller can be broken,
     * replaced or unloaded underneath us, and a stale reference would be a reference to a block
     * entity that is no longer part of the world.
     */
    public @Nullable AeroControlHost resolveHost() { return null; }

    /** The seat block's own control host, used when there is no console to fly. */
    private @Nullable PilotSeatBlockEntity standaloneHost() { return null; }

    /**
     * Nearest flight controller to a seat block, or null when there is none in range.
     *
     * <p>Only a real console counts. The seat block is itself a control host, so accepting any
     * host would match the seat under the pilot and no seat would ever be considered linked.
     */
    public static @Nullable BlockPos findController(Level level, BlockPos seatBlock) { return null; }

    public void tick() { }

    /**
     * Hand the pilot's control frame to the dispatcher, one action per axis group.
     *
     * <p>{@code dispatchQuiet} is used because this runs every tick a pilot is flying; the seat
     * pushes the authoritative snapshot back on its own slower cadence instead of making each
     * axis trigger a block update.
     */
    private void applyInput(AeroControlHost host, @Nullable ServerPlayer actor) { }

    private void commandIdle() { }

    /** Cut thrust exactly once when the pilot stops flying, then leave the controller alone. */
    private void commandIdle(AeroControlHost host) { }

    protected void addPassenger(Entity passenger) { }

    /**
     * Tell the pilot what they just sat down in.
     *
     * <p>Without this the seat is silent about everything that decides whether the controls will
     * do anything: whether it found a console or is flying itself, whether that console is in
     * missile mode (in which case the dispatcher refuses every control frame — correctly, but
     * invisibly), and how many engines are actually paired. A pilot pressing W on a seat with no
     * engines and a seat that is fundamentally broken look identical, which is most of why "the
     * thruster doesn't connect to the chair" was so hard to pin down. The mode is reported, never
     * silently changed: switching a shared console out of missile mode is an operator's call.
     */
    private void reportMountState(ServerPlayer pilot) { }

    protected void removePassenger(Entity passenger) { }

    /**
     * Get a bound controller ready to fly, without ever deciding anything the operator did not
     * already decide.
     *
     * <p>A controller's {@link ControllerMode} is left alone entirely — switching a bound
     * console from missile to flight mode is an operator's call, not something sitting down
     * should do silently. Within flight mode, the seat is allowed to (a) claim nearby engines if
     * none are paired yet, exactly as the GUI's own "pair nearby" button would, and (b) engage
     * the flight subsystem if it is not already engaged, remembering that it did so.
     */
    private void engageBoundController() { }

    /** Undo exactly what {@link #engageBoundController} did, and only if it did it. */
    private void disengageBoundController() { }

    /**
     * Explicitly bind this seat to the nearest flight controller, or drop back to standalone.
     *
     * <p>Mounting already resolves a link automatically, but only once, at sit-down — a seat
     * mounted before a controller existed nearby stays standalone until the pilot gets up and
     * sits again. This lets an already-seated pilot pick up a controller that just appeared, or
     * shed one that broke, going through exactly the same engage/disengage path a fresh mount
     * would take.
     */
    public void toggleBinding(ServerPlayer requester) { }

    public @Nullable LivingEntity getControllingPassenger() { return null; }

    protected boolean canAddPassenger(Entity passenger) { return false; }

    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) { return null; }

    public boolean isPickable() { return false; }

    public boolean isPushable() { return false; }

    public boolean canBeCollidedWith() { return false; }

    public boolean isInvisible() { return false; }

    protected void defineSynchedData() { }

    protected void readAdditionalSaveData(CompoundTag tag) { }

    protected void addAdditionalSaveData(CompoundTag tag) { }
}
