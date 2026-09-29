package net.bullettrain.xenonpcs.combat;

import net.bullettrain.xenonpcs.api.registry.Bt3RushDefinition;

import java.util.Collection;

/**
 * Resolves cinematic rush choreography with form, race, then universal fallback precedence.
 *
 * <p>The catalogue itself moved to {@link RushRegistry} so addons can add to it. This stays as the
 * name the combat code already calls, and as the seam a test can reach without touching the public
 * API surface.
 */
public final class Bt3RushResolver {

    private Bt3RushResolver() {  }

    public static Bt3RushDefinition resolve(String race, String activeForm) { return null; }

    public static Bt3RushDefinition byId(String id) { return null; }

    public static Collection<Bt3RushDefinition> all() { return java.util.List.of(); }
}
