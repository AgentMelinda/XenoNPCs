package net.bullettrain.xenonpcs.client.aura;

/** Forge NPCs use DMZ's native aura pipeline; the NeoForge AAA particle renderer is not shipped. */
public final class HdAuraClient {
    public static boolean debug;
    private HdAuraClient() {}
    public static boolean replacesDmzAura() { return false; }
    public static boolean variant2() { return false; }
    public static boolean variant3() { return false; }
    public static boolean variant4() { return false; }
    public static void refreshSizing() {}
    public static void seen(net.minecraft.client.player.AbstractClientPlayer player) {}
}
