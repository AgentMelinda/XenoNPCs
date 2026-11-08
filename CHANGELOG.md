# Changelog

## 0.1.1 (Minecraft 1.20.1, 2026-11-08)

- Rebuilt the Forge DMZ 2.2.1 compatible release and packaged MixinExtras in the all-in-one jar,
  fixing the missing `Operation` class crash from installations using the previous artifact.
- Matched the paired NeoForge 1.21.1 release version.

## 0.1.0 (Minecraft 1.20.1, 2026-11-08)

- Added a selectable DragonMineZ saga AI alongside Combat Brain 9, with a persisted four-step
  reaction tier.
- Ported native DragonMineZ quest dialogue options from XenoPixelsMod into the standalone editor.
- Fixed optional DMZ mixin targets that do not exist in the Forge 1.20.1 dependency.

## Unreleased (Minecraft 1.20.1, 2026-10-14)

- Script API v29: a player can borrow an NPC's DragonMineZ look and get their own back.
  `XenoPixels.stealAppearance(player, npc[, ticks])` copies the NPC's race, gender, class, body
  type, eyes/nose/mouth/tattoo indices, body/hair/eye/aura colours, saiyan tail and hair onto the
  player after saving what they had; `restoreAppearance(player)` puts it back exactly, and the
  default 600-tick timer does the same on its own. `isAppearanceStolen`,
  `getAppearanceStealTicksLeft`, `getNpcAppearance` and `getPlayerAppearance` report the state.
  The same calls are on the CustomNPCs bridge when that mod is installed.
  Nothing else is touched: health, stats, skills, techniques, cooldowns, quests, forms and
  masteries stay the player's, and armour is still `getArmor`/`setArmor`. A refusal is a returned
  name (`NO_DATA`, `NO_SOURCE`, `INVALID_RACE`, `INVALID_CLASS`, `INVALID_COLOR`, `INVALID_HAIR`,
  `ALREADY_ACTIVE`, `NOT_ACTIVE`, `INTERNAL`), never an exception into the engine.
  The snapshot lives in the player's own XenoPixels data rather than in memory, so dying, logging
  out, a server restart, or a crash mid-borrow still ends with the player's look returned; a clean
  `/stop` restores it before anything is saved. Same capability for addons as
  `DmzAppearance.stealFromNpc`/`restore`/`capture`, with `NpcAppearanceMapping` shared with the
  FULL-mode NPC renderer so the stolen look cannot drift from the one standing next to you.
  `examples/customnpcs/xenopixels_body_steal.js` is the worked example. Built and unit-tested
  (993 tests on 2026-10-14; the one failure is the pre-existing mixin-count audit, which counts
  uncommitted work outside this feature); **not yet run in a game** — in-world sync, the revert
  byte-for-byte, and the death/relog/restart paths are unverified.

## 0.0.5 (Minecraft 1.20.1, 2026-10-03)

- Correct Forge DMZ aura and ki weapon render targets and CustomNPCs script callback signatures.
- Apply native NPC appearance hooks even when CustomNPCs is absent.
- Correct inventory initialization/click ownership and NPC tick/melee method remapping.
- Preserve the tail synthetic-argument crash fix and null-player NPC armor defense fix.
- Audit every registered mixin against dependency bytecode and require injection matches.
- Verify the official Forge DMZ checksum during compilation. CI uploads the all jar and sources separately.
- Add isolated development client/server release probes; these are excluded from distribution jars.

## 0.0.1 (Minecraft 1.21.1)

First public release of XenoNPCs, the NPC system from XenoPixels Network on its own.

- Native Xeno NPCs in eight roles: humanoid, creature, trader, guard, companion, quest, transporter,
  bank.
- The Xeno NPC Wand and the Path Tool, Cloner, Jar, Mounter, Teleporter and Script Tool; Zeni for
  banks.
- The full editor, including DragonMineZ stats, skills, techniques, forms, appearance and attacks.
- The V9 combat brain only. New NPCs start with every DragonMineZ skill at its maximum level, and
  with ki deflection off.
- NPC, player and server scripting (JavaScript, Nashorn bundled); script files reload within two
  seconds of saving.
- The CustomNPCs-style XenoAPI: factions, quests, dialogs, clones, mail, scoreboard, triggers and
  the NPC settings objects (display, stats, AI, inventory, lines, role, job). Imported CustomNPCs /
  My NPCs content keeps its numbers.
- Optional CustomNPCs / My NPCs integration.
- Declared incompatible with XenoPixels Network, which already contains all of this.
