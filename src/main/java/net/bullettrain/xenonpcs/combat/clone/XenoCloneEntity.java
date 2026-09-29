package net.bullettrain.xenonpcs.combat.clone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.UUID;

/**
 * One of a fighter's other bodies.
 *
 * <p>Serves both Shi Shin No Ken and Zanzoken, because they are the same idea used twice: a copy of
 * you standing somewhere you are not. A Multi-Form copy holds a slot in formation and turns as you
 * turn; a Zanzoken copy holds its ground in a ring around whoever swung at you.
 *
 * <p><b>A living body, not a prop.</b> An earlier version was a plain {@link Entity}, which was
 * enough for a decoy but wrong for what a copy has to be. Two things forced this: DragonMineZ's
 * deferred aura queue de-duplicates by entity id, so copies sharing the fighter's identity never
 * got an aura or an animation of their own; and the NPC combat dispatchers this mod already owns
 * are written against {@code LivingEntity}, so being living is what will let a copy throw DMZ ki
 * attacks and strikes rather than needing a second combat system written for it.
 *
 * <p>Health is a share of the fighter's, matching the power split — dividing spreads you thinner,
 * it does not hand out extra bodies for free.
 */
public class XenoCloneEntity extends LivingEntity {

    private static EntityDataAccessor<Integer> OWNER_ID = null;
    /** Formation slot, or {@link #SLOT_STATIONARY} for a copy that holds its ground. */
    private static EntityDataAccessor<Integer> SLOT = null;
    /** 0 while still bursting out of the fighter, 1 once the body has reached its place. */
    private static EntityDataAccessor<Float> TRAVEL = null;
    private static EntityDataAccessor<Integer> LIFETIME = null;

    /** A Zanzoken image: it does not follow, it stays exactly where it was placed. */
    public static final int SLOT_STATIONARY = -1;
    /** Punching dummy: player-shaped, see-through, not a Zanzoken ring copy. */
    public static final int SLOT_TRAINING = -3;
    /** Fighting shadow: player look + ki/melee at the trainer. */
    public static final int SLOT_SHADOW_FIGHT = -4;

    /** How far a Multi-Form copy stands from its fighter when config is at default. */
    public static final double FORMATION_RADIUS = 2.2;

    /** Live spacing; copies re-read this every formation tick. */
    public static double formationRadius() { return 0.0; }

    /** Ticks a body spends travelling out of the fighter before it settles into formation. */
    public static final int TRAVEL_TICKS = 6;

    private CloneCombatBridge combat = null;
    /**
     * The copies' own brain, used by {@code /xenomultiform ai multiform}.
     *
     * <p>An instance per copy rather than static state keyed by UUID, so it is collected with the
     * copy instead of leaking a little on every split.
     */
    private MultiFormCombatBrain multiFormBrain = null;

    public net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile combatProfile() { return null; }

    /**
     * A hit from this copy actually landed.
     *
     * <p>Told to both brains rather than to whichever is active: this arrives from a damage event
     * that does not know the AI mode, and a stale whiff streak left on the inactive one would make
     * it abandon early the moment a player switched modes mid-fight.
     */
    public void noteCombatHit() { }

    private int lifetime = 20;
    private int age;
    private int travelTicks;
    /** Counts down while a body is flying home to be reabsorbed; it is discarded on arrival. */
    private int recallTicks;
    private UUID ownerUuid;
    private int formationBodies = 1;
    private float healthCapacity;
    private Vec3 travelOrigin = null;
    private Vec3 recallOrigin = null;

    public XenoCloneEntity(EntityType<? extends XenoCloneEntity> type, Level level) { super(type, level); }

    /** Copies never wear or wield anything of their own; the renderer draws the fighter. */
    public static AttributeSupplier.Builder createAttributes() { return null; }

    public void configure(Player owner, int slot, int lifetimeTicks, float health) { }

    /**
     * Starts this body flying home to be reabsorbed.
     *
     * <p>The reverse of the split: bodies are drawn back into the fighter and vanish on arrival,
     * rather than being deleted where they stand.
     */
    public void recall() { }

    public boolean isRecalling() { return false; }

    UUID ownerUuid() { return null; }

    /**
     * Whether a player destroyed this image, as opposed to it expiring or being cleaned up.
     *
     * <p>Deliberately not saved: it is a decision made and consumed within the tick the image dies,
     * and a ring never survives a reload anyway.
     *
     * @see net.bullettrain.xenonpcs.combat.ZanzokenRing
     */
    private boolean revealedByPlayer;

    public boolean revealedByPlayer() { return false; }

    public void markRevealedByPlayer() { }

    void setFormationBodies(int bodies) { }

    @Override
    public void setHealth(float health) { }

    public int ownerId() { return 0; }

    public int slot() { return 0; }

    /** 0 while bursting out of the fighter, 1 once settled — the renderer can lean on this too. */
    public float travelProgress() { return 0.0f; }

    public int lifetimeTicks() { return 0; }

    @Override
    public void tick() { }

    private void tickFightOrFormation(net.minecraft.server.level.ServerPlayer owner,
                                      LivingEntity focus, Vec3 slotTarget) { }

    private double ringCenterX;
    private double ringCenterZ;
    private double ringRadius;

    public void setRing(double centerX, double centerZ, double radius) { }

    private void mimicOwner(Player owner) { }

    private void aimRingCopy(Player owner) { }

    /** These bodies belong to a live session, never to a saved world or a reused entity id. */
    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public void remove(RemovalReason reason) { }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource source) { }

    @Override
    public Iterable<ItemStack> getArmorSlots() { return java.util.List.of(); }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) { return null; }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) { }

    @Override
    public HumanoidArm getMainArm() { return null; }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) { }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) { }

}
