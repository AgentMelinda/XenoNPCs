package net.bullettrain.xenonpcs.fx.effek;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Set;

/**
 * Server entry point for Effekseer effects (Hakai, missiles, DMZ punches).
 *
 * <p>{@link Outcome#UNAVAILABLE} (config off, cap reached, library failure) means the caller draws
 * its vanilla particles instead, so something always shows. {@link Outcome#DUPLICATE} means this
 * target already has a punch effect this tick: draw nothing extra.
 */
public final class XenoEffects {
    public enum Outcome { PLAYED, DUPLICATE, UNAVAILABLE }

    private static EffectGate GATE = null;
    private static Set<EffectSlot> FAILED_ONCE = null;
    /** Created on first use, so nothing loads AAA Particles classes until an effect is sent. */
    private static EffekSender sender;

    private XenoEffects() {  }

    /** A one-shot effect at a point, facing {@code forward}; {@code targetId} -1 when none. */
    public static boolean play(ServerLevel level, EffectSlot slot, Vec3 pos, Vec3 forward, float scale, int targetId) { return false; }

    /**
     * A large effect (an area Hakai's veil): sent {@code extraRange} blocks further than the slot's
     * usual range, because its anchor can be far from a player who is well inside it.
     */
    public static boolean playWide(ServerLevel level, EffectSlot slot, Vec3 pos, Vec3 forward, float scale,
                                   double extraRange) { return false; }

    public static Outcome attempt(ServerLevel level, EffectSlot slot, Vec3 pos, Vec3 forward, float scale,
                                  int targetId) { return Outcome.UNAVAILABLE; }

    /**
     * An effect that rides on an entity: AAA moves it with the entity every frame, so it stays
     * with a moving player or missile. Directional slots also turn along the entity's velocity
     * (the missile plume); upright ones (the Sparking aura) only follow it. {@code pos} is the entity's position now,
     * used for the range check.
     */
    public static boolean playBound(ServerLevel level, EffectSlot slot, Vec3 pos, int entityId, float scale) { return false; }

    /**
     * Bound to the entity's eyes and turned along its look every frame: local +Z is where it
     * looks. For effects that lie along a flyer's body (DMZ flight points the body where you look).
     */
    public static boolean playBoundLook(ServerLevel level, EffectSlot slot, Vec3 pos, int entityId, float scale) { return false; }

    /** As {@link #attempt}, for a hit recorded at {@code gameTick} (the punch hook plays later). */
    static Outcome attemptAt(ServerLevel level, long gameTick, EffectSlot slot, Vec3 pos, Vec3 forward, float scale,
                             int targetId) { return Outcome.UNAVAILABLE; }

    private static Outcome attemptAt(ServerLevel level, long gameTick, EffectSlot slot, Vec3 pos, Vec3 forward,
                                     float scale, int targetId, int boundEntity, EffekSender.Follow follow) { return Outcome.UNAVAILABLE; }

    private static Outcome attemptAt(ServerLevel level, long gameTick, EffectSlot slot, Vec3 pos, Vec3 forward,
                                     float scale, int targetId, int boundEntity, EffekSender.Follow follow,
                                     double extraRange) { return Outcome.UNAVAILABLE; }

    private static float categoryScale(EffectGate.Category category) { return 0.0f; }

    /**
     * Blocks behind the eyes that a look-bound effect is anchored at: the body's centre for a
     * flyer, so the effect grows around the body when scaled. AAA's head-space offset points
     * against the look (measured, AaaHeadSpaceOffsetTest), so +0.9 is behind the eyes.
     */
    static final double LOOK_ANCHOR = 0.9;

    private static boolean send(ServerLevel level, EffectSlot slot, EffekSender.Request request) { return false; }

    static ResourceLocation id(EffectSlot slot) { return null; }

    static void useSender(EffekSender testSender) { }

    static void resetForTest() { }
}
