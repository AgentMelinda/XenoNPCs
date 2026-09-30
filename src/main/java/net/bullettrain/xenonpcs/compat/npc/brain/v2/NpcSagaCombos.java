package net.bullettrain.xenonpcs.compat.npc.brain.v2;

import net.bullettrain.xenonpcs.compat.npc.NpcCombatMoves;
import net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile;
import net.bullettrain.xenonpcs.compat.npc.NpcDmzAnim;
import net.bullettrain.xenonpcs.compat.npc.NpcGeckoAnim;
import net.bullettrain.xenonpcs.compat.npc.NpcMeleeDamage;
import net.bullettrain.xenonpcs.combat.anim.Bt3AnimationIntent;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Short authored strings for v2 combo roles. Not a port of DMZ gum/absorb/sleep kits.
 */
final class NpcSagaCombos {
    private static final int PRESSURE_HITS = 4;
    private static final int PRESSURE_GAP = 5;

    private record Combo(UUID victim, NpcSagaCombatBrain.ComboRole role, int remaining,
                         int nextTick, int step) {}

    private static final Map<UUID, Combo> COMBOS = new ConcurrentHashMap<>();

    private NpcSagaCombos() {}

    static boolean active(UUID npcId) {
        return npcId != null && COMBOS.containsKey(npcId);
    }

    static void forget(UUID npcId) {
        if (npcId != null) {
            COMBOS.remove(npcId);
        }
    }

    static boolean start(LivingEntity npc, NpcCombatProfile profile, LivingEntity victim,
                         NpcSagaCombatBrain.ComboRole role, int serverTick) {
        if (npc == null || victim == null || role == null) {
            return false;
        }
        if (role == NpcSagaCombatBrain.ComboRole.RECOVERY) {
            NpcCombatMoves.guard(npc, true);
            NpcCombatMoves.backstep(npc, victim);
            return true;
        }
        if (role == NpcSagaCombatBrain.ComboRole.STUN || role == NpcSagaCombatBrain.ComboRole.HEAVY) {
            NpcCombatMoves.vanish(npc, victim, 0);
            // The vanish can fail (cooldown, no landing spot); a finisher thrown from where it
            // stands would be a punch at nothing.
            if (!net.bullettrain.xenonpcs.compat.npc.NpcCombatRanges.withinMelee(npc, victim)) {
                return false;
            }
            float scale = role == NpcSagaCombatBrain.ComboRole.HEAVY ? 1.75f : 1.25f;
            play(npc, role == NpcSagaCombatBrain.ComboRole.HEAVY
                    ? Bt3AnimationIntent.HEAVY_FINISH : Bt3AnimationIntent.UPPERCUT_RIGHT);
            NpcMeleeDamage.hit(npc, victim, scale);
            return true;
        }
        COMBOS.put(npc.getUUID(), new Combo(victim.getUUID(), role, PRESSURE_HITS, serverTick, 0));
        advance(npc, profile, serverTick);
        return true;
    }

    static void advance(LivingEntity npc, NpcCombatProfile profile, int serverTick) {
        Combo combo = npc == null ? null : COMBOS.get(npc.getUUID());
        if (combo == null || serverTick < combo.nextTick()) {
            return;
        }
        LivingEntity victim = npc.getServer() == null ? null
                : net.bullettrain.xenonpcs.compat.npc.NpcEntityLookup
                .findAlive(npc.getServer(), combo.victim());
        if (victim == null || !victim.isAlive()
                || npc.distanceTo(victim) > NpcSagaCombatContext.MELEE + 2.0) {
            COMBOS.remove(npc.getUUID());
            return;
        }
        // Same rule as the v1 combo: the pressure string stays alive out to MELEE + 2 so a target
        // backing off for a moment does not cancel it, but a jab only reaches MELEE. Animating in
        // the gap between the two is a punch thrown at nothing, every PRESSURE_GAP ticks, for the
        // whole approach. Reschedule instead, so the string resumes on contact.
        if (!net.bullettrain.xenonpcs.compat.npc.NpcCombatRanges.withinMelee(npc, victim)) {
            COMBOS.put(npc.getUUID(), new Combo(combo.victim(), combo.role(), combo.remaining(),
                    serverTick + PRESSURE_GAP, combo.step()));
            return;
        }
        play(npc, combo.step() % 2 == 0
                ? Bt3AnimationIntent.JAB_LEFT : Bt3AnimationIntent.JAB_RIGHT);
        NpcMeleeDamage.hit(npc, victim, profile == null ? 1.0f : profile.brainModifier("strike"));
        int left = combo.remaining() - 1;
        if (left <= 0) {
            COMBOS.remove(npc.getUUID());
            return;
        }
        COMBOS.put(npc.getUUID(), new Combo(combo.victim(), combo.role(), left,
                serverTick + PRESSURE_GAP, combo.step() + 1));
    }

    private static void play(LivingEntity npc, Bt3AnimationIntent intent) {
        if (!NpcDmzAnim.play(npc, intent)) swingWithoutClip(npc);
    }

    /**
     * The vanilla arm swing for an NPC that cannot show Xeno clips (a new NPC starts with its DMZ
     * appearance off), as the older brain's {@code swingBeforeHit} does. GeckoLib NPCs are left
     * out: {@code NpcMeleeDamage.hit} plays their own attack clip.
     */
    static void swingWithoutClip(LivingEntity npc) {
        if (!NpcDmzAnim.canAnimate(npc) && !NpcGeckoAnim.canAnimate(npc)) {
            npc.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
        }
    }
}
