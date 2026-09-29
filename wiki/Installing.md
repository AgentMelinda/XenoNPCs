# Installing

XenoNPCs is a normal NeoForge mod. Install it the same way on the **client and the server**.

1. Install **NeoForge 21.1.233 or newer** for Minecraft **1.21.1**.
2. Put these jars in your `mods` folder:
   - `xenonpcs-<version>.jar`
   - DragonMineZ **2.1.3** or newer
   - GeckoLib, TerraBlender and Curios - DragonMineZ needs all three, so you probably have them
     already.
3. Optional: CustomNPCs **or** My NPCs, if you also use those NPCs. Install only one of them.
4. Start the game. XenoNPCs appears in the mod list with its logo, and a **XenoNPCs** tab appears in
   the creative inventory.

## Things that stop the game from starting

| Message | What to do |
|---|---|
| "XenoPixels Network already includes XenoNPCs" | Remove one of the two. XenoPixels has everything XenoNPCs has. |
| "Mod dragonminez requires terrablender ..." | Install TerraBlender (DragonMineZ needs it, not XenoNPCs). |
| "requires dragonminez 2.1.3 or above" | Update DragonMineZ. |

## Servers

The server needs XenoNPCs and DragonMineZ (with its GeckoLib, TerraBlender and Curios). Players need
XenoNPCs installed too: NPC editing screens, speech bubbles and quest popups are drawn on the client.

The server's settings file is `config/xenonpcs-server.json` - see [Settings](Settings).
