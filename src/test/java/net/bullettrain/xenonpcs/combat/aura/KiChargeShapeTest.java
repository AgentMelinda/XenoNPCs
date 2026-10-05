package net.bullettrain.xenonpcs.combat.aura;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class KiChargeShapeTest {
    @Test void kiChargeKeepsHeightAtEveryRampAndRetainsWidth() {
        float[] base = {2, 3, 4};
        for (float ramp : new float[]{0, .1f, .5f, 1}) {
            var shape = AuraScaleCurve.applyCharge(base, 1.5f, 0, ramp, 1.8, .25, .25);
            assertEquals(4.5f, shape[1]);
            if (ramp > 0) assertTrue(shape[0] > 3);
        }
        assertArrayEquals(new float[]{2, 3, 4}, base);
    }
    @Test void transformationAndNormalScaleArePreserved() {
        var shape = AuraScaleCurve.applyCharge(new float[]{2, 3, 4}, 2, 1, 1, 1.8, .25, .25);
        assertEquals(16.8f, shape[1], .0001);
        assertEquals(5f, shape[0]); assertEquals(10f, shape[2]);
    }
}
