package net.bullettrain.xenonpcs.test;

import java.nio.file.Path;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/** Isolates DMZ configuration paths before discovery; registries still require a launched Forge game. */
public final class ForgeUnitBootstrap implements LauncherSessionListener {
    @Override public void launcherSessionOpened(LauncherSession session) {
        net.minecraftforge.fml.loading.FMLPaths.loadAbsolutePaths(
                Path.of(System.getProperty("xenopixels.projectDir"), "build", "unit-game"));
    }
}
