# FAQ and Troubleshooting

**My NPC just stands there when I hit it.**
The combat brain is off by default. Brain tab > **Combat brain** on, then save. See [Combat Brain](Combat-Brain).

**It can't reach me when I stand on a block.**
It reaches a block or two up or down. Much higher than that it uses ki or, with the Fly skill, flies up.

**The wand does nothing.**
You need operator permission (level 2). On your own world, turn cheats on.

**I changed a script and nothing happened.**
Changes take effect within two seconds of saving the file. Check the file is in the right folder
(`<world>/XenoNpcs/scripts`) and look in the log for a script error.

**The game says XenoPixels Network already includes XenoNPCs.**
You have both installed. XenoPixels contains everything in XenoNPCs - keep one.

**The game says DragonMineZ needs TerraBlender.**
Install TerraBlender; DragonMineZ requires it (and GeckoLib and Curios).

**Where are the logs?**
`logs/latest.log` in your game (or server) folder. Crashes also write a file in `crash-reports/`.

**Where are the settings?**
`config/xenonpcs-server.json` - see [Settings](Settings).
