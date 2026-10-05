# XenoNPCs for Minecraft 1.20.1

XenoNPCs provides native NPCs for DragonMineZ: creation tools, an editor, combat, roles,
dialogue, quests and scripting. This branch is the Forge 1.20.1 export generated from
XenoPixels by `tools/xenonpcs/export_xenonpcs.py --target 1.20.1`.

## Install

Use Java 17, Minecraft 1.20.1 and Forge 47.4.10. Install DragonMineZ 2.1.3,
GeckoLib 4.8.3 and `xenonpcs-0.0.6-1.20.1-all.jar` built from this branch.
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
NPC defense/armor assertions and 40 NPC ticks; clients stop after target classloading, atlas validation and five maker menu captures.
These captures verify menu textures at GUI scale 1; interactive editing and in-world previews require gameplay validation.

The pure unit tests run in this build. `runtime-only-tests.txt` and `EXPORT_REPORT.md`
list 34 tests requiring a running Forge game that are excluded from plain JUnit.
See `RELEASE_HANDOFF_2026-10-03.md` for dated release evidence. The probes validate startup,
active mixin target loading and native NPC defense/ticks. Rendering appearance, interactive
editors, multiplayer and the ledge/respawn scenarios still require gameplay validation.

## Maker Studio

Version 0.0.7 restores all 28 maker panel textures omitted from 0.0.6. Every registered atlas shape is checked in all four palettes during the build. Client probes also resolve the textures and capture the five maker menus in `build/release-runtime/.../maker-screenshots/` at GUI scale 1.

Open **Model > Maker Studio** in the native NPC editor to edit the selected NPC. The hub shows the target name/id. Race, saved form, hair and tattoo Apply use the editor's existing server save checks. Hair/race/form Apply selects Full DMZ appearance; custom tattoo overlays also work on vanilla humanoid skins. Custom or mimic models need Full DMZ appearance for tattoo overlays.

For your player, use `/xenomaker`, or `/xenomaker race`, `forms`, `hair`, `tattoo` (`taotto` alias), and `/xenohairui`. Previews remain local until Apply. Tattoo paint is additive to DMZ's preset tattoos, survives player death/save/load and NPC profile save/load, and supports body-part placement and scaling.

Race creation and updates require server operator level 2. Built-in race packs are read-only. The server validates race ids and expected revisions, stages custom race edits, and saves a ZIP backup before each update in `config/xenonpcs/race-backups/<race>/`. Remote clients back up existing local packs in `config/xenonpcs/client-race-backups/` before accepting server catalog snapshots. These tools do not convert world/chunk files.

Ordinary ki charging no longer adds vertical aura growth. Normal model/power scaling, transformation growth and charge width remain enabled. Legacy `kiChargeHeight` values are ignored.

Validation evidence and remaining gameplay checks are recorded in [RELEASE_HANDOFF_2026-10-05.md](RELEASE_HANDOFF_2026-10-05.md).
