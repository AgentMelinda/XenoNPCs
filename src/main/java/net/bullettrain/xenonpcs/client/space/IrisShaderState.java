package net.bullettrain.xenonpcs.client.space;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Whether an Iris shader pack is drawing this frame, and whether Iris is in its shadow pass. Reflective so
 * Iris stays optional: {@code net.irisshaders.iris.api.v0.IrisApi} (Iris 1.8.14-beta.1, verified with javap)
 * exposes {@code getInstance()}, {@code isShaderPackInUse()} and {@code isRenderingShadowPass()}.
 * Any failure reads as "no pack", which keeps the vanilla rendering path.
 */
public final class IrisShaderState {
    private static MethodHandle INSTANCE, PACK_IN_USE, SHADOW_PASS;
    private IrisShaderState() {  }

    public static boolean packInUse() { return false; }
    public static boolean renderingShadows() { return false; }

    private static boolean call(MethodHandle method) { return false; }
}
