package net.bullettrain.xenonpcs.client.npc.dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Where everything in the neon dialogue menu goes, in the screen's UI space.
 *
 * <p>Pure arithmetic - no Minecraft classes - so the layout can be checked by tests rather than by
 * opening the game: nothing overlaps, everything stays on screen, and a long option list scrolls
 * instead of running off the top.
 *
 * <p>Shape: the dialogue panel sits along the bottom, the options stack upward above its right
 * half with the Close row nearest the panel, and the portrait frame sits above its left half.
 */
public final class NeonDialogueLayout {

    public static final int ROW_H = 18;
    public static final int ROW_GAP = 3;
    public static final int MAX_ROWS = 5;
    private static final int MARGIN = 8;

    // Aspect ratios of the supplied sprites (see NeonDialogueStyle).
    private static final float PANEL_ASPECT = 234f / 760f;
    private static final float PORTRAIT_ASPECT = 403f / 360f;

    private NeonDialogueLayout() {
    }

    public record Rect(int x, int y, int w, int h) {
        public boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }

        public int right() {
            return x + w;
        }

        public int bottom() {
            return y + h;
        }

        public boolean overlaps(Rect other) {
            return x < other.right() && other.x < right() && y < other.bottom() && other.y < bottom();
        }
    }

    /** One blit of a sprite slice: destination rectangle plus the source region. */
    public record Slice(int dx, int dy, int dw, int dh, float u, float v, int uw, int vh) {
    }

    /**
     * @param rows    the visible option rows, top to bottom
     * @param first   index of the option shown in {@code rows.get(0)}
     * @param more    whether options exist beyond the visible window (above or below)
     */
    public record Layout(Rect panel, Rect nameplate, Rect text, Rect portrait, List<Rect> rows,
                         Rect close, int first, boolean more) {
    }

    public static Layout compute(int uiW, int uiH, int optionCount, int firstVisible) {
        int pw = clamp(uiW - 2 * MARGIN, 120, 340);
        int ph = Math.round(pw * PANEL_ASPECT);
        int px = (uiW - pw) / 2;
        int py = uiH - ph - 10;
        Rect panel = new Rect(px, py, pw, ph);

        // The art has a tab at the top left (about a fifth of the height); the name goes inside
        // it. The text area is the dark interior below the tab, inside the bottom border.
        Rect nameplate = new Rect(px + Math.round(pw * 0.10f), py + Math.round(ph * 0.04f),
                Math.round(pw * 0.25f), Math.max(10, Math.round(ph * 0.15f)));
        Rect text = new Rect(px + Math.round(pw * 0.06f), py + Math.round(ph * 0.27f),
                Math.round(pw * 0.88f), Math.round(ph * 0.50f));

        int portraitW = clamp(Math.round(pw * 0.17f), 36, 64);
        int portraitH = Math.round(portraitW * PORTRAIT_ASPECT);

        int fit = (py - 2 * MARGIN) / (ROW_H + ROW_GAP) - 1;
        int maxRows = clamp(fit, 1, MAX_ROWS);
        int shown = Math.min(Math.max(optionCount, 0), maxRows);
        int first = clampFirst(firstVisible, optionCount, shown);

        int rowW = clamp(pw - portraitW - 10, 80, 230);
        int rowX = px + pw - rowW;
        int total = shown + 1;
        int top = py - 4 - total * (ROW_H + ROW_GAP) + ROW_GAP;
        List<Rect> rows = new ArrayList<>(shown);
        for (int i = 0; i < shown; i++) {
            rows.add(new Rect(rowX, top + i * (ROW_H + ROW_GAP), rowW, ROW_H));
        }
        Rect close = new Rect(rowX, top + shown * (ROW_H + ROW_GAP), rowW, ROW_H);

        Rect portrait = new Rect(px, py - portraitH - 4, portraitW, portraitH);
        return new Layout(panel, nameplate, text, portrait, List.copyOf(rows), close, first,
                optionCount > shown);
    }

    /** Keeps the scroll window inside the option list. */
    public static int clampFirst(int first, int optionCount, int shown) {
        int maxFirst = Math.max(0, optionCount - shown);
        return clamp(first, 0, maxFirst);
    }

    /** The window start after scrolling {@code delta} rows (positive = down). */
    public static int scrolled(int first, int delta, int optionCount, int shown) {
        return clampFirst(first + delta, optionCount, shown);
    }

    /**
     * The window start that keeps {@code focus} visible, moving as little as possible.
     */
    public static int revealing(int first, int focus, int shown) {
        if (shown <= 0) {
            return 0;
        }
        if (focus < first) {
            return Math.max(0, focus);
        }
        if (focus >= first + shown) {
            return focus - shown + 1;
        }
        return first;
    }

    /**
     * Three horizontal slices that draw a sprite at any width without stretching its corners.
     *
     * <p>The caps keep the sprite's own proportions (scaled to the destination height); only the
     * middle stretches. When the destination is too narrow for two caps the caps shrink to share it
     * rather than overlapping.
     */
    public static List<Slice> threeSlice(int srcW, int srcH, int x, int y, int w, int h) {
        int capDst = Math.min(h, w / 2);
        int capSrc = Math.max(1, Math.min(srcW / 2, Math.round(capDst * (float) srcH / h)));
        List<Slice> slices = new ArrayList<>(3);
        slices.add(new Slice(x, y, capDst, h, 0f, 0f, capSrc, srcH));
        slices.add(new Slice(x + capDst, y, w - 2 * capDst, h, capSrc, 0f, srcW - 2 * capSrc, srcH));
        slices.add(new Slice(x + w - capDst, y, capDst, h, srcW - capSrc, 0f, capSrc, srcH));
        return List.copyOf(slices);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
