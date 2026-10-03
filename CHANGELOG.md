# Changelog

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
