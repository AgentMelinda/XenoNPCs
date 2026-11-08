package net.bullettrain.xenonpcs.compat.npc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 2026-10-14: a body-steal script that could copy an NPC's look onto another NPC could not touch the
 * player half at all, because every appearance call took an NPC. A look borrow is offered by both
 * XenoPixels globals this build ships - native and CustomNPCs - or the script that works in the editor
 * fails on a live server. (There is no My NPCs bridge here.)
 */
class BridgeAppearanceApiTest {
    private static final List<String> BRIDGE_SOURCES = List.of(
            "src/main/java/net/bullettrain/xenonpcs/compat/npc/NpcXenoScriptApi.java");
    private static final List<String> BORROW_CALLS = List.of(
            "String stealAppearance(", "boolean restoreAppearance(", "boolean isAppearanceStolen(",
            "int getAppearanceStealTicksLeft(", "Map<String, Object> getNpcAppearance(",
            "Map<String, Object> getPlayerAppearance(");

    @Test void everyBridgeOffersTheBorrowAndTheReturn() throws Exception {
        for (String source : BRIDGE_SOURCES) {
            String file = read(source);
            for (String call : BORROW_CALLS) {
                assertTrue(file.contains(call), source + " is missing " + call);
            }
            assertTrue(file.contains("VERSION = \"29\""), source + " does not advertise API 29");
        }
    }

    // XenoNPCs has no ScriptFunctionCatalog (the root project's editor-completion registry), so the
    // root test's "the catalog never advertises .unwrap(" check has no equivalent here; the borrow
    // calls themselves hand back plain values, which the stale-player test below pins.
    @Test void theNativeGlobalAdvertisesTheBorrow() throws Exception {
        assertEquals("29", net.bullettrain.xenonpcs.npc.script.api.NativeXenoScriptApi.INSTANCE.getVersion());
        String nativeApi = readNative();
        for (String signature : List.of("public String stealAppearance(",
                "public boolean restoreAppearance(", "public boolean isAppearanceStolen(",
                "public int getAppearanceStealTicksLeft(", "public Map<String, Object> getNpcAppearance(",
                "public Map<String, Object> getPlayerAppearance(")) {
            assertTrue(nativeApi.contains(signature), "the native global is missing " + signature);
        }
    }

    /** A missing NPC is an answer, not an exception - a script that mistargets gets NO_SOURCE back. */
    @Test void aBorrowWithNothingToCopyIsRefused() {
        assertEquals(NpcAppearanceMapping.Result.NO_SOURCE,
                PlayerAppearanceSteals.start(null, (net.minecraft.world.entity.Entity) null, 600L));
    }

    /**
     * A script can hold its player across hooks and hand back one that has since logged out. Every
     * native call has to answer that the way the CustomNPCs and My NPCs bridges do - with the refusal
     * it would give a player who was never there - because an exception lands in the server log and
     * stops the rest of the script.
     */
    @Test void aStalePlayerIsAnsweredRatherThanThrown() throws Exception {
        net.bullettrain.xenonpcs.npc.script.api.NativeXenoScriptApi api =
                net.bullettrain.xenonpcs.npc.script.api.NativeXenoScriptApi.INSTANCE;
        assertEquals(NpcAppearanceMapping.Result.NO_SOURCE.name(), api.stealAppearance(null, null));
        assertFalse(api.restoreAppearance(null));
        assertFalse(api.isAppearanceStolen(null));
        assertEquals(0, api.getAppearanceStealTicksLeft(null));
        assertTrue(api.getPlayerAppearance(null).isEmpty());
    }

    private static String readNative() throws Exception {
        return read("src/main/java/net/bullettrain/xenonpcs/npc/script/api/NativeXenoScriptApi.java");
    }

    private static String read(String relative) throws Exception {
        return java.nio.file.Files.readString(java.nio.file.Path.of(
                System.getProperty("xenopixels.projectDir"), relative));
    }
}
