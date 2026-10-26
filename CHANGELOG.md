# Changelog

## 0.0.8 — 2026-10-26 (Minecraft 1.20.1 and 1.21.1)

- Update DragonMineZ integration to pinned 2.2 builds for each Minecraft version.
- Improve V9 pursuit, NPC stats binding, imported movement and aura controls.
- Add target-identified, server-timed body Hakai synchronization and native DMZ dialogue/quest bridging.
- Expand legacy dialogue, quest, model, inventory, scripts and player-progress conversion.
- Local unit and packaging validation passed for both exports. Live visual checks and full legacy converter parity remain pending.

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