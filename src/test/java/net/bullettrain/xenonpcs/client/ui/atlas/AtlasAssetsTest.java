package net.bullettrain.xenonpcs.client.ui.atlas;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import static org.junit.jupiter.api.Assertions.*;

class AtlasAssetsTest {
    @Test void everyRegisteredShapeHasAllPalettesAtItsNativeDimensions() throws Exception {
        for (String shape : XenoAtlasSprites.shapes()) {
            for (var theme : XenoAtlasSprites.Theme.values()) {
                var sprite = XenoAtlasSprites.get(shape, theme);
                String path = "/assets/" + sprite.rl().getNamespace() + "/" + sprite.rl().getPath();
                try (var stream = AtlasAssetsTest.class.getResourceAsStream(path)) {
                    assertNotNull(stream, "Missing packaged atlas texture: " + path);
                    var image = ImageIO.read(stream);
                    assertNotNull(image, "Invalid PNG: " + path);
                    assertEquals(sprite.width(), image.getWidth(), "Width: " + path);
                    assertEquals(sprite.height(), image.getHeight(), "Height: " + path);
                }
            }
        }
    }
}
