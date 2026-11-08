package net.bullettrain.xenonpcs.client.npc.dialog;

import net.bullettrain.xenonpcs.client.config.XenoClientConfig;

/**
 * Which presentation an NPC conversation uses.
 *
 * <p>Three exist and all three stay reachable, so they can be compared in play instead of one being
 * deleted on the strength of a preview: the neon menu (default), the world bubbles, and the older
 * full-screen dialogue.
 */
public enum NeonDialogueMode {
    /** DragonMineZ's native screen, textures, portrait renderer and option widgets. */
    DMZ,
    /** Interactive menu drawn from the neon source kit. */
    NEON,
    /** Bubbles above the NPC, answered at the crosshair or through the invisible cursor screen. */
    BUBBLES,
    /** The older full-screen dialogue. */
    CLASSIC;

    /** Pure decision, kept apart from the config so it can be tested. */
    public static NeonDialogueMode choose(boolean neonMenu, boolean bubbles) {
        if (neonMenu) {
            return NEON;
        }
        return bubbles ? BUBBLES : CLASSIC;
    }

    /** The mode in force right now. Read every call so a config change applies to the next talk. */
    public static NeonDialogueMode current() {
        if (XenoClientConfig.dialogueDmzNativeTextures) return DMZ;
        return choose(XenoClientConfig.dialogueNeonMenu, XenoClientConfig.dialogueBubbles);
    }
}
