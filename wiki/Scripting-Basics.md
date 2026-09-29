# Scripting Basics

NPCs can run **JavaScript**. The script engine (Nashorn) is built into XenoNPCs - nothing else to
install.

## Where scripts go

Scripts are files inside your world folder:

| Folder | Runs for |
|---|---|
| `<world>/XenoNpcs/scripts` | NPCs (bind a script to an NPC with the Script Tool) |
| `<world>/XenoNpcs/player_scripts` | every player |
| `<world>/XenoNpcs/forge_scripts` | server-wide events |

**Save a file and it takes effect within two seconds** - no restart or relog. Deleting a file stops
it the same way.

## Your first script

1. Take the **Script Tool** from the XenoNPCs creative tab.
2. Right-click an NPC with it - the script screen opens for that NPC.
3. Pick or write a script and save.

The mod ships working examples in the `examples/customnpcs` folder of the XenoNPCs download - open one,
change a line, and watch the NPC do it. Every example there is checked by the mod's own tests, so they
all work.

## Events

A script is a set of functions named after the event they handle - for example a function that runs
when a player interacts with the NPC, when it is hit, or every tick. The examples show the common ones;
the [Scripting Reference](Scripting-Reference) has the full list of calls.
