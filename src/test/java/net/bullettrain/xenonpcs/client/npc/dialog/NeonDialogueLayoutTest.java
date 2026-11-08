package net.bullettrain.xenonpcs.client.npc.dialog;

import net.bullettrain.xenonpcs.client.npc.dialog.NeonDialogueLayout.Layout;
import net.bullettrain.xenonpcs.client.npc.dialog.NeonDialogueLayout.Rect;
import net.bullettrain.xenonpcs.client.npc.dialog.NeonDialogueLayout.Slice;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The menu geometry, checked without opening the game. */
class NeonDialogueLayoutTest {

    private static final int[][] SCREENS = {{320, 240}, {427, 240}, {480, 270}, {640, 360}, {854, 480}};

    private static boolean inside(Rect r, int w, int h) {
        return r.x() >= 0 && r.y() >= 0 && r.right() <= w && r.bottom() <= h;
    }

    private static List<Rect> all(Layout l) {
        List<Rect> rects = new ArrayList<>(l.rows());
        rects.add(l.close());
        rects.add(l.panel());
        rects.add(l.portrait());
        return rects;
    }

    @Test
    void everythingStaysOnScreenAtEveryUsableSize() {
        for (int[] s : SCREENS) {
            for (int options : new int[] {0, 1, 3, 5, 12}) {
                Layout l = NeonDialogueLayout.compute(s[0], s[1], options, 0);
                for (Rect r : all(l)) {
                    assertTrue(inside(r, s[0], s[1]), r + " off screen " + s[0] + "x" + s[1] + " options=" + options);
                }
            }
        }
    }

    @Test
    void nothingOverlapsAnythingElse() {
        for (int[] s : SCREENS) {
            for (int options : new int[] {0, 2, 5, 9}) {
                List<Rect> rects = all(NeonDialogueLayout.compute(s[0], s[1], options, 0));
                for (int i = 0; i < rects.size(); i++) {
                    for (int j = i + 1; j < rects.size(); j++) {
                        assertFalse(rects.get(i).overlaps(rects.get(j)),
                                rects.get(i) + " overlaps " + rects.get(j) + " at " + s[0] + "x" + s[1]);
                    }
                }
            }
        }
    }

    @Test
    void textAndNameAreInsideThePanel() {
        Layout l = NeonDialogueLayout.compute(320, 240, 3, 0);
        assertTrue(l.panel().x() <= l.text().x() && l.text().right() <= l.panel().right());
        assertTrue(l.panel().y() <= l.text().y() && l.text().bottom() <= l.panel().bottom());
        assertTrue(l.panel().x() <= l.nameplate().x() && l.nameplate().right() <= l.panel().right());
        assertTrue(l.nameplate().bottom() <= l.text().y());
    }

    @Test
    void closeRowIsNearestThePanelAndOptionsStackAboveIt() {
        Layout l = NeonDialogueLayout.compute(427, 240, 3, 0);
        assertEquals(3, l.rows().size());
        assertTrue(l.rows().get(2).bottom() < l.close().y());
        assertTrue(l.close().bottom() < l.panel().y());
        assertTrue(l.rows().get(0).y() < l.rows().get(1).y());
    }

    @Test
    void longListsScrollInsteadOfRunningOffTheTop() {
        Layout l = NeonDialogueLayout.compute(320, 240, 20, 0);
        assertTrue(l.rows().size() < 20);
        assertTrue(l.more());
        assertTrue(l.rows().get(0).y() >= 0);
        Layout none = NeonDialogueLayout.compute(320, 240, 2, 0);
        assertFalse(none.more());
    }

    @Test
    void scrollWindowIsClamped() {
        assertEquals(0, NeonDialogueLayout.clampFirst(-3, 10, 4));
        assertEquals(6, NeonDialogueLayout.clampFirst(99, 10, 4));
        assertEquals(0, NeonDialogueLayout.clampFirst(5, 3, 4));
        assertEquals(2, NeonDialogueLayout.scrolled(1, 1, 10, 4));
        assertEquals(0, NeonDialogueLayout.scrolled(0, -1, 10, 4));
        Layout l = NeonDialogueLayout.compute(320, 240, 10, 99);
        assertEquals(10 - l.rows().size(), l.first());
    }

    @Test
    void keyboardFocusIsKeptVisibleWithTheSmallestMove() {
        assertEquals(3, NeonDialogueLayout.revealing(3, 4, 4));
        assertEquals(2, NeonDialogueLayout.revealing(3, 2, 4));
        assertEquals(5, NeonDialogueLayout.revealing(3, 8, 4));
        assertEquals(0, NeonDialogueLayout.revealing(0, 0, 0));
    }

    @Test
    void threeSliceTilesTheDestinationAndTheSourceExactly() {
        for (int[] d : new int[][] {{200, 18}, {80, 18}, {31, 18}, {473, 96}}) {
            List<Slice> s = NeonDialogueLayout.threeSlice(473, 96, 10, 20, d[0], d[1]);
            assertEquals(3, s.size());
            assertEquals(10, s.get(0).dx());
            assertEquals(s.get(0).dx() + s.get(0).dw(), s.get(1).dx());
            assertEquals(s.get(1).dx() + s.get(1).dw(), s.get(2).dx());
            assertEquals(10 + d[0], s.get(2).dx() + s.get(2).dw());
            assertEquals(0f, s.get(0).u());
            assertEquals(s.get(0).uw(), (int) s.get(1).u());
            assertEquals(473, (int) s.get(1).u() + s.get(1).uw() + s.get(2).uw());
            assertEquals(473 - s.get(2).uw(), (int) s.get(2).u());
            for (Slice slice : s) {
                assertTrue(slice.dw() >= 0 && slice.uw() > 0);
                assertEquals(d[1], slice.dh());
            }
        }
    }

    @Test
    void hitTestUsesHalfOpenBounds() {
        Rect r = new Rect(10, 10, 20, 10);
        assertTrue(r.contains(10, 10));
        assertTrue(r.contains(29.9, 19.9));
        assertFalse(r.contains(30, 15));
        assertFalse(r.contains(9.9, 15));
    }
}
