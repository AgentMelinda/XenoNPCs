# Settings

*Generated from the XenoNPCs source by `tools/xenonpcs/gen_wiki.py`.*

Server settings live in `config/xenonpcs-server.json`, written with every default the first time the server starts. Stop the server, change a value, start it again. XenoNPCs has no `/xenoset` command - that belongs to XenoPixels Network.

The file also holds settings XenoNPCs shares with XenoPixels' code; only the ones that change NPCs and quests are listed here.

| Setting (JSON key) | Default | What it does |
|---|---|---|
| `migrateCustomNpcsWorldData` | `true` | Migrate CustomNPCs world data to My NPCs on load |
| `npcAttackStartRadius` | `1.0` | Blocks an NPC must close to before melee attacks start (per-NPC Melee Range still wins; 4.5 = old DMZ band) |
| `npcCommandsIgnoreCommandBlockSetting` | `false` | Let NPC quest/dialog commands run while command blocks are disabled |
| `npcDamageMode` | `"dmz"` | Profiled NPC damage mode (dmz, mynpc, or numeric) |
| `npcMeleeHeightReach` | `0.5` | Blocks of air allowed between NPC and target hitboxes for melee (0-8) |
| `npcMeleeHeightRule` | `true` | NPC melee reaches targets a block or two up/down (off: old straight-line distance) |
| `npcNumericDamage` | `10.0` | Damage for profiled NPC numeric mode |
| `npcSayEnabled` | `true` | CustomNPCs npc.say() and NPC executeCommand OP chat |
| `xenoNpcSizeScalesHitbox` | `true` | Native XenoNPC hitbox follows Display Size |
