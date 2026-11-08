<p align="center"><img src="logo.png" alt="XenoNPCs" width="640"></p>

# XenoNPCs

Native NPCs for **DragonMineZ** on Minecraft 1.21.1 (NeoForge).

- **A wand and six tools** to place, edit, clone, move, mount, teleport and script NPCs.
- **An in-game editor**: looks, stats, AI, inventory, roles, dialogue, factions, quests, banks,
  transport, speech-bubble lines, scenes.
- **DragonMineZ integration**: DMZ stats and power level, races, forms, auras, every DMZ skill,
  ki blasts, waves and strikes.
- **Two selectable combat brains**: DragonMineZ's saga AI or Combat Brain 9. The saga AI has
  four persisted reaction-speed tiers; Combat Brain 9 keeps its per-action controls.
- **Scripting** in JavaScript (Nashorn built in), CustomNPCs-style, with DragonMineZ helpers;
  edits to script files apply within two seconds.
- Optional **CustomNPCs / My NPCs** support: their NPCs get the DragonMineZ features too.

**New here? Read the [wiki](wiki/Home.md).**

## Requirements

| | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.248 |
| DragonMineZ | Official 2.2.1 Halo fixes release pinned in `libs/` (also needs GeckoLib 4.9.2, TerraBlender and Curios) |

XenoNPCs cannot be installed together with **XenoPixels Network**, which already contains it.

## Building

```
# The exact DragonMineZ 2.2.1 Halo fixes jar is tracked in libs/; its SHA-256 is enforced.
./gradlew build -PofflineMcMeta
```

The distributable jar is in `build/libs/`. `./gradlew test` runs the test suite;
`./gradlew runClient` starts a dev client.

## Where this code comes from

XenoNPCs is generated from the XenoPixels Network source by its export tool; this repository is the
published result. Please report issues here.

## Licence

All Rights Reserved - see [LICENSE](LICENSE).

The scripting API interfaces under `xenoapi/` are adapted from Noppes' CustomNPCsAPI, which publishes
no licence of its own; their terms are not established here.

## Maker Studio

Version 0.0.7 restores all 28 maker panel textures omitted from 0.0.6. Every registered atlas shape is checked in all four palettes during the build. Client probes also resolve the textures and capture the five maker menus in `build/release-runtime/.../maker-screenshots/` at GUI scale 1.

Open **Model > Maker Studio** in the native NPC editor to edit the selected NPC. The hub shows the target name/id. Race, saved form, hair and tattoo Apply use the editor's existing server save checks. Hair/race/form Apply selects Full DMZ appearance; custom tattoo overlays also work on vanilla humanoid skins. Custom or mimic models need Full DMZ appearance for tattoo overlays.

For your player, use `/xenomaker`, or `/xenomaker race`, `forms`, `hair`, `tattoo` (`taotto` alias), and `/xenohairui`. Previews remain local until Apply. Tattoo paint is additive to DMZ's preset tattoos, survives player death/save/load and NPC profile save/load, and supports body-part placement and scaling.

Race creation and updates require server operator level 2. Built-in race packs are read-only. The server validates race ids and expected revisions, stages custom race edits, and saves a ZIP backup before each update in `config/xenonpcs/race-backups/<race>/`. Remote clients back up existing local packs in `config/xenonpcs/client-race-backups/` before accepting server catalog snapshots. These tools do not convert world/chunk files.

Ordinary ki charging no longer adds vertical aura growth. Normal model/power scaling, transformation growth and charge width remain enabled. Legacy `kiChargeHeight` values are ignored.

Validation evidence and remaining gameplay checks are recorded in [RELEASE_HANDOFF_2026-10-05.md](RELEASE_HANDOFF_2026-10-05.md).

For Minecraft 1.21.1, use the exact [DragonMineZ 2.2.1 Halo fixes release jar](https://github.com/AgentMelinda/dragonminez-1.21.1/releases/download/1.21.1-v2.2.1-halo-fixes.2/dragonminez-2.2.1.jar). The pinned jar's SHA-256 is checked during Gradle configuration. This NeoForge dependency is separate from the Forge 1.20.1 variant.
