# XenoNPCs for Minecraft 1.20.1

XenoNPCs provides native NPCs for DragonMineZ: creation tools, an editor, combat, roles,
dialogue, quests and scripting. This branch is the Forge 1.20.1 export generated from
XenoPixels by `tools/xenonpcs/export_xenonpcs.py --target 1.20.1`.

## Install

Use Java 17, Minecraft 1.20.1 and Forge 47.4.10. Install DragonMineZ 2.1.3,
GeckoLib 4.8.3 and `xenonpcs-0.0.5-1.20.1-all.jar` built from this branch.
Version 0.0.5 is a local release candidate until published.
Curios and CustomNPCs/CNPC Gecko compatibility are optional. MyNPCs compatibility
is omitted from this version's export.

The same release also contains a separate NeoForge 1.21.1 jar. Select the jar matching
your Minecraft version and loader. Do not install XenoNPCs alongside XenoPixels.

## Build

```sh
./gradlew build --no-daemon
```

The two optional compatibility jars under `libs/` are compile-only dependencies.
DragonMineZ is resolved from the pinned CurseMaven file in `gradle.properties`.
Compilation checks its SHA-256 against the official Forge 2.1.3 jar.

Development release probes use isolated worlds and logs under `build/release-runtime/`.
They never use the normal `run/` directory and are excluded from the released jar:

```sh
./gradlew runServer -PreleaseProbe -PreleaseNpcProfile=core
./gradlew runClient -PreleaseProbe -PreleaseNpcProfile=core
```

Repeat with `custom` and `gecko` profiles to enable the optional local compatibility jars.
Use `-Pgeckolib_version=4.8.4` to check that GeckoLib build. Servers stop after the
NPC defense/armor assertions and 40 NPC ticks; clients stop after startup and target classloading.
These probes do not verify rendering appearance or interactive editor behavior.

The pure unit tests run in this build. `runtime-only-tests.txt` and `EXPORT_REPORT.md`
list 34 tests requiring a running Forge game that are excluded from plain JUnit.
See `RELEASE_HANDOFF_2026-10-03.md` for dated release evidence. The probes validate startup,
active mixin target loading and native NPC defense/ticks. Rendering appearance, interactive
editors, multiplayer and the ledge/respawn scenarios still require gameplay validation.
