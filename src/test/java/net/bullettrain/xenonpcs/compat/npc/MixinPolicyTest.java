package net.bullettrain.xenonpcs.compat.npc;

import net.bullettrain.xenonpcs.mixin.SharedNpcMixinPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MixinPolicyTest {
    private static final String SWING_SUPPRESS =
            "net.bullettrain.xenonpcs.mixin.compat.shared.NpcSwingSuppressMixin";
    private static final String OPTIONAL_SHARED =
            "net.bullettrain.xenonpcs.mixin.compat.shared.DmzNpcAuraScaleMixin";

    @Test
    void nativeXenoNpcSwingSuppressionDoesNotRequireEitherLegacyNpcMod() {
        assertTrue(SharedNpcMixinPolicy.shouldApply(SWING_SUPPRESS, false, false));
    }

    @Test
    void otherSharedMixinsRemainGatedByTheirLegacyNpcDependencies() {
        assertFalse(SharedNpcMixinPolicy.shouldApply(OPTIONAL_SHARED, false, false));
        assertTrue(SharedNpcMixinPolicy.shouldApply(OPTIONAL_SHARED, true, false));
        assertTrue(SharedNpcMixinPolicy.shouldApply(OPTIONAL_SHARED, false, true));
    }
}
