package net.bullettrain.xenonpcs.features.progression;

import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Slim combat skill tree — 4 skills, max level 3 each.
 * Unlocked with skill points from quests / dummy milestones.
 */
public final class CombatSkills {
    public static final String POWER = "power";       // outgoing damage
    public static final String GUARD = "guard";       // better guard reduction
    public static final String SPARKING = "sparking"; // faster meter build
    public static final String ULTIMATE = "ultimate"; // ultimate damage
    public static final String BEAM = "beam";         // sustained beam ceiling and ramp
    public static final String BARRAGE = "barrage";   // longer ki volley window
    public static final String GUIDE = "guide";       // lock-on homing range / turn
    /** Gates whether Hakai can be used at all -- not a stacking buff like the others. */
    public static final String HAKAI = "hakai";
    /** Gates whether Zanzoken can be used at all -- a one-shot unlock, like {@link #HAKAI}. */
    public static final String ZANZOKEN = "zanzoken";
    /** Gates whether Shi Shin No Ken (Multiform) can be used at all -- a one-shot unlock. */
    public static final String MULTIFORM = "multiform";
    /** Gates the grant-only rush-combo DMZ strike. */
    public static final String RUSHCOMBO = "rushcombo";
    /** Gates the grant-only lift-combo DMZ strike. */
    public static final String LIFTCOMBO = "liftcombo";
    /** Gates the four existing Xeno rush strikes when {@code rushAutoUnlock} is off. */
    public static final String RUSH = "rush";

    public static Map<String, SkillDef> DEFS = null;

    private CombatSkills() {  }

    /** @param maxLevel a one-shot unlock (like Hakai) uses 1; stacking buffs use 3. */
    public record SkillDef(String id, String title, String desc, int pointCostPerLevel, int maxLevel) {}

    public static int level(ServerPlayer player, String skillId) { return 0; }

    public static float powerMult(ServerPlayer player) { return 0.0f; }

    public static float guardBonus(ServerPlayer player) { return 0.0f; }

    public static float sparkingBuildMult(ServerPlayer player) { return 0.0f; }

    public static float ultimateMult(ServerPlayer player) { return 0.0f; }

    public static boolean hakaiUnlocked(ServerPlayer player) { return false; }

    public static boolean zanzokenUnlocked(ServerPlayer player) { return false; }

    public static boolean multiFormUnlocked(ServerPlayer player) { return false; }

    public static boolean exclusive(String skillId) { return false; }

    /**
     * Pure self-unlock gate used by {@link #tryUnlock} and unit tests.
     *
     * <p>Returns {@code null} when a non-admin may spend skill points on {@code skillId}.
     * Exclusive combo/rush skills always refuse unless {@code administrator} is true.
     * Permission API is applied by the caller so this stays Minecraft-free.
     */
    public static String selfUnlockRefusal(String skillId, boolean administrator) { return ""; }

    /**
     * Exclusive skills refuse only the first unlock. After an administrator grant, further
     * levels are ordinary mastery the player can buy with skill points.
     */
    public static String selfUnlockRefusal(String skillId, boolean administrator, int currentLevel) { return ""; }

    private static String unknownSkillMessage() { return ""; }

    /**
     * Progress toward the next Wave Mastery level, awarded for sustaining a beam.
     *
     * <p>Granted as a skill point rather than a hidden XP bar so it flows through the same
     * unlock the other four skills use - the player spends it when they choose, and mastery
     * cannot silently level mid-fight and change how a beam behaves under them.
     */
    public static void awardBeamProgress(ServerPlayer player) { }

    public static void awardBarrageProgress(ServerPlayer player) { }

    private static void awardSkillProgress(ServerPlayer player, String skillId) { }

    /** @return null on success, error message otherwise */
    public static String tryUnlock(ServerPlayer player, String skillId) { return ""; }

    public static String grant(ServerPlayer player, String skillId) { return ""; }

    /** Revokes every level of a skill without refunding points. */
    public static String revoke(ServerPlayer player, String skillId) { return ""; }

    private static String tryUnlock(ServerPlayer player, String skillId, boolean administrator) { return ""; }
}
