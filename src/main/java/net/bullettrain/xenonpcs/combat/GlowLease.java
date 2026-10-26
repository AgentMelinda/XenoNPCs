package net.bullettrain.xenonpcs.combat;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Owns only the glow this channel actually enabled; release is idempotent. */
final class GlowLease {
    private Consumer<Boolean> setter;
    private boolean owned;

    GlowLease(BooleanSupplier getter, Consumer<Boolean> setter, boolean enabled) {  }

    void release() { }
}
