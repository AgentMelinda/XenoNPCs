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

## Examples

The `examples/customnpcs` folder in the XenoNPCs download has working scripts. Those whose names
start with `xenopixels_` show the DragonMineZ helpers - for example an NPC transforming in a show, or
players playing animation clips. Every example is compiled by the mod's own tests.

## With CustomNPCs installed

If you also run CustomNPCs, its NPC scripts get the same `XenoPixels` helpers. `/xenonpcs scriptapi dump`
writes the full CustomNPCs scripting API list to `logs/xenopixels-customnpcs-script-api.md` in the
server folder. See [Commands](Commands).
