package net.bullettrain.xenonpcs.client.npc.dialog;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.minecraft.resources.ResourceLocation;

/**
 * Textures and colours of the neon dialogue menu.
 *
 * <p>Every sprite is the supplied neon kit's own art, cropped to its opaque bounds and scaled down
 * to a size that suits a GUI (the originals are around 2000 px wide). Nothing is redrawn, so the
 * look is the kit's look. The colours below are the mean of that art's own bright lime and cyan
 * edge pixels and its dark interior, not look-alikes.
 *
 * <p>The pixel sizes here must match the files in
 * {@code assets/xenonpcs/textures/gui/neon_dialogue/}; a test reads the PNG headers to check it.
 */
public final class NeonDialogueStyle {

    private static final String DIR = "textures/gui/neon_dialogue/";

    /** A texture and its pixel size. */
    public record Sprite(ResourceLocation rl, int w, int h) {
    }

    public static final Sprite PANEL = sprite("dialogue_panel", 760, 234);
    public static final Sprite PORTRAIT = sprite("portrait_frame", 360, 403);
    public static final Sprite NAMEPLATE = sprite("speaker_nameplate", 640, 118);
    public static final Sprite DIVIDER = sprite("divider", 640, 38);

    public static final Sprite CHOICE = sprite("choice_row", 473, 96);
    public static final Sprite CHOICE_HOVER = sprite("choice_row_hover", 473, 96);
    public static final Sprite CHOICE_PRESSED = sprite("choice_row_pressed", 473, 96);
    public static final Sprite CHOICE_DISABLED = sprite("choice_row_disabled", 473, 96);

    public static final Sprite PRIMARY = sprite("button_primary", 397, 96);
    public static final Sprite PRIMARY_HOVER = sprite("button_primary_hover", 397, 96);
    public static final Sprite PRIMARY_PRESSED = sprite("button_primary_pressed", 397, 96);
    public static final Sprite PRIMARY_DISABLED = sprite("button_primary_disabled", 397, 96);

    public static final Sprite SECONDARY = sprite("button_secondary", 433, 96);
    public static final Sprite SECONDARY_HOVER = sprite("button_secondary_hover", 433, 96);
    public static final Sprite SECONDARY_PRESSED = sprite("button_secondary_pressed", 433, 96);
    public static final Sprite SECONDARY_DISABLED = sprite("button_secondary_disabled", 433, 96);

    /** Sampled from the kit art: bright lime edge, bright cyan edge, dark interior. */
    public static final int LIME = 0xFF90F55E;
    public static final int CYAN = 0xFF14E0D8;
    public static final int DARK = 0xFF061C1D;
    /** Body text: near-white with a cyan cast so it sits on the dark interior without glare. */
    public static final int TEXT = 0xFFE6FFF9;
    /** Disabled and secondary text: the cyan pulled toward the interior colour. */
    public static final int MUTED = mix(CYAN, DARK, 0.45f);

    private NeonDialogueStyle() {
    }

    /** The sprite for a button kind in a given interaction state. */
    public static Sprite forKind(NeonOptionKind kind, boolean hovered, boolean pressed) {
        return switch (kind) {
            case PRIMARY -> pressed ? PRIMARY_PRESSED : hovered ? PRIMARY_HOVER : PRIMARY;
            case SECONDARY -> pressed ? SECONDARY_PRESSED : hovered ? SECONDARY_HOVER : SECONDARY;
            case DISABLED -> CHOICE_DISABLED;
            case CHOICE -> pressed ? CHOICE_PRESSED : hovered ? CHOICE_HOVER : CHOICE;
        };
    }

    /** Linear blend of two ARGB colours; {@code t} 0 gives {@code a}, 1 gives {@code b}. */
    static int mix(int a, int b, float t) {
        int out = 0xFF000000;
        for (int shift = 16; shift >= 0; shift -= 8) {
            int ca = (a >> shift) & 0xFF;
            int cb = (b >> shift) & 0xFF;
            out |= Math.round(ca + (cb - ca) * t) << shift;
        }
        return out;
    }

    private static Sprite sprite(String name, int w, int h) {
        return new Sprite(new ResourceLocation(XenoNpcsMod.MOD_ID,
                DIR + name + ".png"), w, h);
    }
}
