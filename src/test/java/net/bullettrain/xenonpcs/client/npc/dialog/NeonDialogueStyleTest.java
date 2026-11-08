package net.bullettrain.xenonpcs.client.npc.dialog;

import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The sprite sizes in {@link NeonDialogueStyle} are what the three-slice maths and the panel
 * aspect assume, so they are checked against the PNG files themselves.
 */
class NeonDialogueStyleTest {

    private static int[] pngSize(NeonDialogueStyle.Sprite sprite) throws IOException {
        String path = "/assets/" + sprite.rl().getNamespace() + "/" + sprite.rl().getPath();
        try (InputStream in = NeonDialogueStyleTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "missing texture " + path);
            DataInputStream data = new DataInputStream(in);
            data.skipBytes(16); // PNG signature + IHDR length + type
            return new int[] {data.readInt(), data.readInt()};
        }
    }

    @Test
    void declaredSpriteSizesMatchTheFiles() throws IOException {
        NeonDialogueStyle.Sprite[] all = {
                NeonDialogueStyle.PANEL, NeonDialogueStyle.PORTRAIT, NeonDialogueStyle.NAMEPLATE,
                NeonDialogueStyle.DIVIDER,
                NeonDialogueStyle.CHOICE, NeonDialogueStyle.CHOICE_HOVER,
                NeonDialogueStyle.CHOICE_PRESSED, NeonDialogueStyle.CHOICE_DISABLED,
                NeonDialogueStyle.PRIMARY, NeonDialogueStyle.PRIMARY_HOVER,
                NeonDialogueStyle.PRIMARY_PRESSED, NeonDialogueStyle.PRIMARY_DISABLED,
                NeonDialogueStyle.SECONDARY, NeonDialogueStyle.SECONDARY_HOVER,
                NeonDialogueStyle.SECONDARY_PRESSED, NeonDialogueStyle.SECONDARY_DISABLED};
        for (NeonDialogueStyle.Sprite sprite : all) {
            int[] size = pngSize(sprite);
            assertEquals(sprite.w(), size[0], sprite.rl() + " width");
            assertEquals(sprite.h(), size[1], sprite.rl() + " height");
        }
    }

    @Test
    void everyKindAndStateHasASprite() {
        for (NeonOptionKind kind : NeonOptionKind.values()) {
            for (boolean hovered : new boolean[] {false, true}) {
                for (boolean pressed : new boolean[] {false, true}) {
                    assertNotNull(NeonDialogueStyle.forKind(kind, hovered, pressed));
                }
            }
        }
    }

    @Test
    void mixBlendsAndKeepsTheColourOpaque() {
        assertEquals(0xFF000000, NeonDialogueStyle.mix(0xFF000000, 0xFFFFFFFF, 0f));
        assertEquals(0xFFFFFFFF, NeonDialogueStyle.mix(0xFF000000, 0xFFFFFFFF, 1f));
        assertEquals(0xFF808080, NeonDialogueStyle.mix(0xFF000000, 0xFFFFFFFF, 0.5f));
        assertEquals(0xFF000000, NeonDialogueStyle.MUTED & 0xFF000000);
    }
}
