<p align="center"><img src="logo.png" alt="XenoNPCs" width="640"></p>

# XenoNPCs

Native NPCs for **DragonMineZ** on Minecraft 1.21.1 (NeoForge).

- **A wand and six tools** to place, edit, clone, move, mount, teleport and script NPCs.
- **An in-game editor**: looks, stats, AI, inventory, roles, dialogue, factions, quests, banks,
  transport, speech-bubble lines, scenes.
- **DragonMineZ integration**: DMZ stats and power level, races, forms, auras, every DMZ skill,
  ki blasts, waves and strikes.
- **The V9 combat brain**: DragonMineZ's saga decision tree with a switch for every action.
- **Scripting** in JavaScript (Nashorn built in), CustomNPCs-style, with DragonMineZ helpers;
  edits to script files apply within two seconds.
- Optional **CustomNPCs / My NPCs** support: their NPCs get the DragonMineZ features too.

**New here? Read the [wiki](wiki/Home.md).**

## Requirements

| | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.233+ |
| DragonMineZ | 2.1.3+ (it also needs GeckoLib, TerraBlender and Curios) |

XenoNPCs cannot be installed together with **XenoPixels Network**, which already contains it.

## Building

```
# put dragonminez-2.1.3.jar in libs/ (not redistributed here), then:
./gradlew build
```

The jar is in `build/libs/`. `./gradlew test` runs the test suite; `./gradlew runClient` starts a dev
client.

## Where this code comes from

XenoNPCs is generated from the XenoPixels Network source by its export tool; this repository is the
published result. Please report issues here.

## Licence

All Rights Reserved - see [LICENSE](LICENSE).

The scripting API interfaces under `xenoapi/` are adapted from Noppes' CustomNPCsAPI, which publishes
no licence of its own; their terms are not established here.
