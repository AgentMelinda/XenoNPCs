# Commands

*Generated from the XenoNPCs source by `tools/xenonpcs/gen_wiki.py` - every command listed here exists. Nested items are sub-commands; `<name>` is a value you type.*

Most commands need operator permission (level 2). Commands marked **(op)** check it explicitly.

## /xenoclone

The saved-clone library, as operator commands.

- `list`
  - `<tab> (integer)`
- `save`
  - `<tab> (integer)`
    - `<id> (word)`
      - `<npc> (entity)`
- `place`
  - `<tab> (integer)`
    - `<id> (word)`

## /xenonpcimport

`/xenonpcimport` — read My NPCs / CustomNPCs content off disk into the Xeno store.

- `factions`
  - `confirm`
- `all`
  - `confirm`

## /xenonpcs

Runtime catalog of the CustomNPCs scripting API actually installed on the server.

- `scriptapi` **(op)**
  - `globals`
    - `<page> (integer)`
  - `all`
    - `<page> (integer)`
  - `search`
    - `<text> (greedyString)`
  - `show`
    - `<class> (greedyString)`
      - `<page> (integer)`
  - `dump` **(op)**

Script-facing entry point for the My NPCs / CustomNPCs race-stats-ki-attack bridge (see `compat.npc`).

- `migratenpcs`
- `importclones`
  - `<tab> (integer)`
- `npcprofile`
  - `set`
    - `<raceId> (word)`
      - `<strength> (integer)`
        - `<strikePower> (integer)`
          - `<resistance> (integer)`
            - `<vitality> (integer)`
              - `<kiPower> (integer)`
                - `<energy> (integer)`
  - `damage`
  - `combat`
    - `punchable`
      - `<value> (bool)`
    - `knockable`
      - `<value> (bool)`
    - `pinnative`
      - `<value> (bool)`
  - `kiattack`
    - `<blastType> (word)`
      - `color`
        - `<hex> (word)`
      - `<durationTicks> (integer)`
        - `color`
          - `<hex> (word)`
        - `<lookAt> (entity)`
          - `color`
            - `<hex> (word)`
          - `<hex> (word)`
  - `color`
    - `<hex> (word)`
  - `aura`
    - `on`
    - `off`
    - `toggle`
    - `color`
      - `<hex> (word)`
    - `scale`
      - `<factor> (floatArg)`
  - `form`
    - `<group> (word)`
      - `<form> (word)`
  - `transform`
    - `<group> (word)`
      - `<form> (word)`
        - `<ticks> (integer)`
  - `descend`
  - `charge`
    - `<percent> (integer)`
  - `mastery`
    - `<group> (word)`
      - `<form> (word)`
        - `<percent> (doubleArg)`
  - `tech`
    - `add`
      - `<id> (word)`
    - `remove`
      - `<id> (word)`
    - `fire`
      - `<id> (word)`
        - `<lookAt> (entity)`
    - `list`
  - `skin`
    - `player`
      - `<name> (word)`
    - `url`
      - `<url> (greedyString)`
  - `model`
    - `player`
      - `on`
      - `off`
  - `clothing`
    - `copy`
      - `<player> (word)`
  - `hair`
    - `on`
    - `off`
    - `code`
      - `<code> (greedyString)`
    - `style`
      - `<styleId> (integer)`
    - `color`
      - `clear`
      - `<hairHex> (word)`
  - `ai`
    - `enable`
  - `brain`
    - `status`
    - `on`
    - `off`
    - `v1`
    - `v2`
    - `v9`
- `npcsay`
  - `on`
  - `off`
  - `toggle`
  - `status`

`/xenopixels playerscripts` — live player-script gate for My NPCs or CustomNPCs, not the GUI button.

- `playerscripts` **(op)**
  - `status`
  - `enable`

## /xenoscene

Operator authoring and playback of shared native NPC scenes.

- `list`
- `show`
  - `<id> (word)`
- `create`
  - `<id> (word)`
    - `<name> (greedyString)`
- `say`
  - `<id> (word)`
    - `<tick> (integer)`
      - `<text> (greedyString)`
- `clip`
  - `<id> (word)`
    - `<tick> (integer)`
      - `<clip> (word)`
- `remove`
  - `<id> (word)`
    - `<index> (integer)`
- `delete`
  - `<id> (word)`
- `start`
  - `<npc> (entity)`
    - `<id> (word)`
- `testclip`
  - `<npc> (entity)`
    - `<clip> (word)`
- `check`
  - `<npc> (entity)`
- `stop`
  - `<npc> (entity)`
- `pause`
  - `<npc> (entity)`
- `resume`
  - `<npc> (entity)`
- `reset`
  - `<npc> (entity)`
- `time`
  - `<npc> (entity)`
    - `<tick> (integer)`
