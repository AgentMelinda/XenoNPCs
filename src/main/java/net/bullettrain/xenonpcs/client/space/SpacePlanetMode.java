package net.bullettrain.xenonpcs.client.space;

/** Client presentation policy for physical Sable planets and optional far chart projections. */
public enum SpacePlanetMode {
    /** Draw HD surfaces only from replicated Sable bodies. */
    BODIES("bodies"),
    /** No planets at all. */
    OFF("off");

    public String id;

    SpacePlanetMode(String id) {  }

    /** Resolves old projection settings as body-only mode, so client config upgrades cannot restore fake planets. */
    public static SpacePlanetMode byName(String name) { return null; }

    /** Chart projections can be independently toggled by the client config/command. */
    public boolean drawsProxies() { return false; }

    /**
     * Chart projections belong to the canonical Voidsea chart view. The disposable ORBIT physics
     * fixture has its own legacy Earth Sable binding and must not layer chart Earth/Namek proxies on
     * top of it.
     */
    public boolean drawsProxiesIn(String dimensionId) { return false; }

    /** True only when the corresponding Sable body is replicated on this client. */
    public boolean drawsBodies() { return false; }
}
