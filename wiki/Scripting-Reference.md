# Scripting Reference

XenoNPCs scripts are JavaScript, shaped like CustomNPCs scripts - so most CustomNPCs scripting guides
apply.

## What a script can use

An NPC script gets these names:

| Name | What it is |
|---|---|
| `npc` | the NPC running the script |
| `world` | the world it is in |
| `event` | the event being handled - what happened, and what you can change or cancel |
| `log` | write a line to the server log (handy while testing) |
| `script`, `scriptName` | the script itself and its file name |
| `XenoPixels` | the DragonMineZ helpers: race, stats, forms, ki attacks, skills |
| `XenoAPI` | XenoAPI (`xenoapi.npcs.api`), the CustomNPCs-style API for Xeno NPCs |

Player scripts and server-wide (forge) scripts get `XenoPixels` and `XenoAPI` too.

## What XenoAPI covers

The CustomNPCs calls scripts use most work on Xeno NPCs:

- **Players:** messages, items, experience, game mode, spawn point, quests (start, finish, stop,
  remove, active and finished lists, "can this be accepted"), faction points and status, dialogs
  (show, read, add, remove), titles (`sendNotification`), mail, music, clickable website links,
  permissions, timers, `clearData` and `trigger`.
- **NPCs:** speech, targets, navigation, home, owner, commands, timers, faction, dialog slots,
  mark, `reset`, `trigger`, and the settings objects `getDisplay()`, `getStats()` (with
  `getMelee()` and `getRanged()`), `getAi()`, `getInventory()`, `getAdvanced()` (lines and
  sounds), `getRole()` (a trader's shop) and `getJob()`. A change made through them applies at
  once, exactly as saving the editor would.
- **World:** blocks, entities, particles, sounds, explosions, the scoreboard (objectives, scores,
  teams), clones, world spawn and `trigger`.
- **`XenoAPI`:** `getFactions()`, `getQuests()`, `getDialogs()`, `getClones()`, `createMail(...)`,
  `getRawPlayerData(...)` and `getGlobalDir()`.

**Numbers.** CustomNPCs scripts name factions, dialogs and quests by number, for example
`player.showDialog(3, "Elder")`. Content imported from CustomNPCs or My NPCs keeps its number.
Content you make in XenoNPCs has no number (its `getId()` is -1): find it in the lists, such as
`XenoAPI.getFactions().list()`, or by its id, such as `XenoAPI.getQuests().get("my_quest")`.

**A few things work differently.** A dialog command runs when the player picks that option, not
when the dialog opens, and only if the server allows dialogue commands. An `IDialog` is one step
of a Xeno conversation, and its options lead to other steps of the same conversation.

**Not available:** custom GUIs, the player's screen size, Pixelmon, carpentry recipes, random
names, renaming a player, NPCs shooting item projectiles (they fire ki attacks), and the raw
Minecraft objects (`getMCEntity()` and similar). A call to one of these stops the script with an
error that names the call.

## Examples

The `examples/customnpcs` folder in the XenoNPCs download has working scripts. Those whose names
start with `xenopixels_` show the DragonMineZ helpers - for example an NPC transforming in a show, or
players playing animation clips. Every example is compiled by the mod's own tests.

## With CustomNPCs installed

If you also run CustomNPCs, its NPC scripts get the same `XenoPixels` helpers. `/xenonpcs scriptapi dump`
writes the full CustomNPCs scripting API list to `logs/xenopixels-customnpcs-script-api.md` in the
server folder. See [Commands](Commands).
