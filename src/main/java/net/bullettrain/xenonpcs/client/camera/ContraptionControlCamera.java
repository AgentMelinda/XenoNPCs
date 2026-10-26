package net.bullettrain.xenonpcs.client.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;

import java.lang.reflect.Method;

/**
 * True while the local player is in a Create or Sable/Aeronautics control camera, or seated in
 * a pilot seat. DMZ shoulder + first-person offsets must not run in any of these states.
 */
public final class ContraptionControlCamera {

    private static Method CREATE_GET_CONTRAPTION;

    private ContraptionControlCamera() {  }

    public static boolean active() { return false; }

    private static boolean seatedInPilotSeat() { return false; }

    private static boolean createControls() { return false; }

    private static boolean sableSubLevelView() { return false; }
}
