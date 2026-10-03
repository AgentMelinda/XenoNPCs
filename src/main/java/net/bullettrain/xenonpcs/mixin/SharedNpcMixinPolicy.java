package net.bullettrain.xenonpcs.mixin;

/** Pure dependency gate for mixins shared by native XenoNPC and legacy NPC integrations. */
public final class SharedNpcMixinPolicy {
    private SharedNpcMixinPolicy() {}

    public static boolean shouldApply(String mixinClassName,
                                      boolean customNpcsLoaded, boolean myNpcsLoaded) {
        // Native XenoNPC attacks use the same LivingEntity swing hook but do not depend on either
        // legacy NPC mod. Its handler exits immediately for every other entity.
        if (mixinClassName != null && mixinClassName.endsWith(".NpcSwingSuppressMixin")) {
            return true;
        }
        // DMZ is required by XenoNPCs. These client hooks also serve its native synthetic players;
        // gating them on CustomNPCs silently disables native tails, forms, hair and aura overrides.
        if (mixinClassName != null && mixinClassName.startsWith(
                "net.bullettrain.xenonpcs.mixin.compat.shared.DmzNpc")) {
            return true;
        }
        // Other shared mixins back redirects in the optional NPC integrations.
        return customNpcsLoaded || myNpcsLoaded;
    }
}
