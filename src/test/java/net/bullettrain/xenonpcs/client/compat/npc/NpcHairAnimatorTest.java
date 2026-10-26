package net.bullettrain.xenonpcs.client.compat.npc;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NpcHairAnimatorTest {
    @Test
    void simulationHeadPoseMatchesDmzInverseBaseForChangingCameras() {
        Vec3 anchor = new Vec3(120.5, 70.0, -23.0);
        Matrix4f localHead = new Matrix4f().rotateY(1.2f).translate(0.0f, 1.6f, 0.0f)
                .rotateX(-0.4f).scale(1.3f);
        for (float yaw : new float[]{0.0f, 0.7f, -2.0f}) {
            Quaternionf cameraRotation = new Quaternionf().rotationYXZ(yaw, 0.3f, 0.0f);
            Vec3 camera = new Vec3(113.0, 74.0, -28.0);
            Matrix4f baseView = new Matrix4f().rotation(new Quaternionf(cameraRotation).conjugate())
                    .translate((float) (anchor.x - camera.x), (float) (anchor.y - camera.y),
                            (float) (anchor.z - camera.z));
            Matrix4f headView = new Matrix4f(baseView).mul(localHead);
            Matrix4f expected = new Matrix4f(baseView).invert().mul(headView);
            Matrix4f actual = NpcHairAnimator.headToAnchor(headView, cameraRotation, camera, anchor);
            assertTrue(expected.equals(actual, 0.0001f));
            assertTrue(localHead.equals(actual, 0.0001f));
        }
    }
}
