package net.bullettrain.xenonpcs.client.npc.speech;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * The same local-to-world operation used by the camera-facing drawing pose: 1.20.1 vanilla
 * nameplates and DragonMineZ's ki-sense BP meter scale by (-s, -s, s) after the camera orientation
 * (1.21.1's camera has an extra half turn and scales X positive). The renderers draw with
 * {@link #X_SIGN} so drawing and dialogue click boxes cannot disagree.
 */
public final class BubbleBillboardGeometry {
    /** Sign of the billboard's X scale on 1.20.1. */
    public static final float X_SIGN = -1.0f;

    private BubbleBillboardGeometry() {}
    public static Vector3f offset(float x, float y, float scale, Quaternionf camera) {
        return new Vector3f(X_SIGN * x * scale, -y * scale, 0).rotate(camera);
    }
    public static float height(float boundsHeight, float hitboxScale, float displayScale, float above) {
        return boundsHeight / Math.max(0.05f, hitboxScale) * displayScale + Math.max(0, above);
    }
}
