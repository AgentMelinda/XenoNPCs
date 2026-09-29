package net.bullettrain.xenonpcs.fx.effek;

import net.minecraft.world.phys.Vec3;

import net.bullettrain.xenonpcs.combat.fx.CombatFx;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Which hits get a punch effect, and which one. */
public final class PunchEffectRules {
    private PunchEffectRules() {  }

    /** Plain melee damage: a player's or a mob's own attack (not arrows, blasts, magic). */
    public static boolean isMeleeType(ResourceKey<DamageType> type) { return false; }

    /** A DMZ punch: direct melee from a player or a DMZ-profile NPC (native or MyNPCs/CustomNPCs). */
    public static boolean isDmzMelee(DamageSource source, LivingEntity attacker) { return false; }

    public static EffectSlot slotFor(CombatFx.Weight weight) { return null; }

    public static float scaleFor(CombatFx.Weight weight) { return 0.0f; }

    /**
     * The height the attacker's crosshair meets the target at: the eye ray followed out to the
     * target's horizontal distance, kept between the target's feet and head (2026-09-29 owner:
     * punch effects at crosshair level). A ray with no horizontal reach (aiming straight up or
     * down) uses eye height.
     */
    public static double crosshairY(Vec3 eye, Vec3 look, Vec3 targetCenter, double minY, double maxY) { return 0.0; }
}
