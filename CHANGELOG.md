# Changelog

## 0.1.1 (Minecraft 1.21.1, 2026-11-08)

- Rebuilt the DMZ 2.2.1 compatible XenoNPCs release with selectable DMZ saga AI and editor/save fixes.
- Matched the paired Forge 1.20.1 release version.

## 0.1.0 (Minecraft 1.21.1, 2026-11-08)

- Added a selectable DragonMineZ saga AI alongside Combat Brain 9, with a persisted four-step
  reaction tier.
- Ported native DragonMineZ quest dialogue options from XenoPixelsMod into the standalone editor.
- Fixed Nashorn module duplication in the packaged mod and aligned DMZ 2.2.1 compatibility.

## Unreleased (Minecraft 1.21.1, 2026-10-14)

- Script API v29: a player can borrow an NPC's DragonMineZ look and get their own back.
  `XenoPixels.stealAppearance(player, npc[, ticks])` copies the NPC's race, gender, class, body
  type, eyes/nose/mouth/tattoo indices, body/hair/eye/aura colours, saiyan tail and hair onto the
  player after saving what they had; `restoreAppearance(player)` puts it back exactly, and the
  default 600-tick timer does the same on its own. `isAppearanceStolen`,
  `getAppearanceStealTicksLeft`, `getNpcAppearance` and `getPlayerAppearance` report the state.
  The same calls are on the CustomNPCs and My NPCs bridges when those mods are installed.
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
  (1017 tests, 0 failures on 2026-10-14); **not yet run in a game** — in-world sync, the revert
  byte-for-byte, and the death/relog/restart paths are unverified.

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
