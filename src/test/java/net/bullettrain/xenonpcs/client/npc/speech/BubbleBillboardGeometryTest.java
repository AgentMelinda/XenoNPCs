package net.bullettrain.xenonpcs.client.npc.speech;

import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BubbleBillboardGeometryTest {
    @Test void pitchedCameraRotatesVerticalCornersIntoDepth() {
        var corner = BubbleBillboardGeometry.offset(0, -40, 0.025f,
                new Quaternionf().rotationX((float) Math.PI / 2));
        assertEquals(0, corner.x, 0.0001);
        assertEquals(0, corner.y, 0.0001);
        assertEquals(1, corner.z, 0.0001);
    }
    @Test void yawAndPitchKeepTheArtAndHitboxDimensionsIdentical() {
        var rotation = new Quaternionf().rotationYXZ(1.1f, 0.7f, 0);
        var left = BubbleBillboardGeometry.offset(-50, -30, 0.025f, rotation);
        var right = BubbleBillboardGeometry.offset(50, -30, 0.025f, rotation);
        var bottom = BubbleBillboardGeometry.offset(-50, 0, 0.025f, rotation);
        assertEquals(2.5, left.distance(right), 0.0001);
        assertEquals(0.75, left.distance(bottom), 0.0001);
    }
    /**
     * 2026-09-30 owner: "bubles are not showing on right click an npc". 1.20.1's camera is
     * rotationYXZ(-yaw, pitch, 0) and vanilla nameplates / DMZ's ki-sense BP meter scale by
     * (-0.025, -0.025, 0.025) there; 1.21.1's camera carries an extra half turn and its nameplate
     * scales X positive. Built the 1.21.1 way, the 1.20.1 quad faced away and was culled.
     * Text reading left to right must run towards the camera's screen right.
     */
    @Test void textRunsTowardsScreenRightForTheForgeCamera() {
        // Yaw 0 looks south (+Z); screen right is west (-X).
        var south = BubbleBillboardGeometry.offset(1, 0, 1, new Quaternionf().rotationYXZ(0, 0, 0));
        assertEquals(-1, south.x, 0.0001);
        // Yaw 90 looks west (-X); screen right is north (-Z).
        float yaw = (float) Math.toRadians(90);
        var west = BubbleBillboardGeometry.offset(1, 0, 1, new Quaternionf().rotationYXZ(-yaw, 0, 0));
        assertEquals(-1, west.z, 0.0001);
        // Text rows run down the screen.
        assertEquals(-1, BubbleBillboardGeometry.offset(0, 1, 1, new Quaternionf()).y, 0.0001);
    }
    @Test void CollisionSizeDoesNotMoveTheBubbleAwayFromTheVisualHead() {
        assertEquals(1.8f, BubbleBillboardGeometry.height(3.6f, 2, 1, 0), 0.0001);
        assertEquals(3.85f, BubbleBillboardGeometry.height(3.6f, 2, 2, 0.25f), 0.0001);
    }
}
