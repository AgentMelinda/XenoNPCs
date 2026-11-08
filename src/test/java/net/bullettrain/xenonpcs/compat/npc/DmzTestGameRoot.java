package net.bullettrain.xenonpcs.compat.npc;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gives DragonMineZ a config folder to point at, so its {@code Character} can be constructed in a
 * unit test with no game running.
 *
 * <p>Only 1.20.1 needs this. Its DragonMineZ build resolves its config directory while
 * {@code ConfigManager} initialises, which {@code Character}'s constructor touches, and Forge's game
 * root is {@code null} until the launcher has run - so the first {@code new Character()} on a bare
 * test JVM dies in a static initialiser rather than in the code under test. Pointing Forge's path
 * table at a throwaway directory here costs nothing and keeps the appearance-mapping tests real unit
 * tests instead of ones that need a live server.
 */
final class DmzTestGameRoot {
    private static boolean ready;

    private DmzTestGameRoot() {}

    static synchronized void ensure() {
        if (ready) {
            return;
        }
        Path root = Paths.get(System.getProperty("java.io.tmpdir"), "xenonpcs-unit-game");
        FMLPaths.loadAbsolutePaths(root);
        ready = true;
    }
}
