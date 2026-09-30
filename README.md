# XenoNPCs for Minecraft 1.20.1

XenoNPCs provides native NPCs for DragonMineZ: creation tools, an editor, combat, roles,
dialogue, quests and scripting. This branch is the Forge 1.20.1 export generated from
XenoPixels by `tools/xenonpcs/export_xenonpcs.py --target 1.20.1`.

## Install

Use Java 17, Minecraft 1.20.1 and Forge 47.4.10. Install DragonMineZ 2.1.3,
GeckoLib 4.8.3 and the `xenonpcs-0.0.1-1.20.1.jar` from the
[v0.0.1 release](https://github.com/AgentMelinda/XenoNPCs/releases/tag/v0.0.1).
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

The pure unit tests run in this build. `runtime-only-tests.txt` and `EXPORT_REPORT.md`
list 34 tests requiring a running Forge game that are excluded from plain JUnit.
Dedicated-server startup has been observed; client rendering, optional-mod compatibility,
and the ledge/respawn scenarios still require gameplay validation.
