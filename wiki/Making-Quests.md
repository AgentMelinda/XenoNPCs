# Making Quests

A quest is made in two parts: you **write the quest** once in the shared quest list, and you give an
NPC a **dialogue answer that offers it**. This page walks through both, then covers the extra
options.

You need operator permission and the **Xeno NPC Wand**.

## 1. Write the quest

1. Right-click any NPC with the wand to open the editor.
2. Go to **Global > Quests**. The top of the page lists every quest that already exists.
3. Press **New quest** and fill in:

| Field | What to put |
|---|---|
| **Group** | A folder name for your quests, e.g. `village`. Quests in the same group are listed together. |
| **Id** | A short name without spaces, e.g. `wolf_trouble`. It cannot be changed later. |
| **Title** | What the player sees, e.g. *Wolf Trouble*. |
| **Description** | One line telling the player what to do. |
| **Journal text** | Longer text for the player's quest log. |
| **Category** | Where it sits in the quest log (defaults to the group). |
| **Objective** | What the player has to do - see the table below. |
| **Parameter / Target type / NPC target** | What the objective is about (the row changes with the objective). |
| **Target quota** | How many times: 5 wolves, 3 conversations... |
| **Complete text** | What the player is told when it is done. |
| **Repeat** | *No*, *Yes* (any time), or once per *MC day*, *MC week*, *real day*, *real week*. |

4. Choose the reward (see below) and press **Save quest**.

### Objectives

| Objective | The player has to... | Parameter |
|---|---|---|
| `kill_mobs` | kill hostile mobs | - |
| `kill_players` | kill other players | - |
| `kill_type` | kill one kind of mob | pick the **Target type**, e.g. `minecraft:wolf` |
| `kill_npc` | defeat a named NPC | pick the **NPC target**, or type its exact visible name |
| `dummy_damage` | deal damage to a training dummy | - (quota = points of damage) |
| `dummy_hits` | land hits on a training dummy | - (quota = number of hits) |
| `talk_to_npc` | talk to NPCs | an NPC's name (`Master Roshi`) or role (`master`); blank = any NPC |
| `item` | bring an item | the item id, e.g. `minecraft:diamond` (it is taken when the quest completes) |
| `dialog` | pick a certain dialogue answer | that answer |
| `location` | reach a place | - |
| `area_kill` | kill mobs inside an area | - |
| `manual` | wait for an operator to advance it | - (see the note at the bottom) |

## 2. Rewards

- **Give XenoSkill points** - on by default (2 points).
- **Enable reward actions** + **Reward command** - a command that runs when the quest completes, for
  example `give @p minecraft:golden_apple 3`. The command runs only if *Enable reward actions* is on.
- **Random reward (pay one item)** - when the quest gives several items, the player gets one of them
  at random.
- **Kill target hunts player** - for `kill_npc` quests: that NPC attacks the player from the moment
  the quest starts until it ends.
- **Completion colour** (blue, gold, green, red) and **Completion frame** (rounded, banner) - the look
  of the popup the player sees when the quest is done.

## 3. Let an NPC offer it

Quests are handed out through **dialogue**. The Quest role on its own is passive: it is the NPC's
dialogue that offers quests.

1. Open the NPC that should give the quest.
2. Go to **Advanced > Dialogs** and give it a dialogue: write its own, or link one from
   **Global > Dialogs**.
3. Add an answer, set **Does** to **QUEST**, and choose your quest in the **Quest** list.
4. Save.

When a player picks that answer, they get the quest. Picking the same answer again once the quest is
finished **hands it in**.

A dialogue can also show or hide answers depending on quests, so an NPC can say something different
before, during and after one.

## 4. Test it

Talk to your NPC, take the quest, do it. The quest log shows your progress; when you reach the quota,
the completion popup shows the reward.

## Going further: quest files

The editor writes the most common options. Quest files can do more, because each quest is a JSON
file. Put your own in a datapack at `data/<your_namespace>/npcs/quests/<id>.json`; XenoNPCs ships two
examples in `data/xenonpcs/npcs/quests/`:

```json
{
  "title": "Wolf Trouble",
  "description": "Drive off 5 wolves",
  "target": 5,
  "objective": "kill_type",
  "parameter": "minecraft:wolf",
  "reward": {
    "skill_points": 1,
    "experience": 40,
    "items": [ { "id": "minecraft:cooked_beef", "count": 4 } ]
  }
}
```

Only in files:

| Key | What it does |
|---|---|
| `objectives` | A list of steps (each with its own `objective`, `parameter` and `target`) instead of one objective. |
| `completion` + `completer_npc` | `"completion": "npc"` makes the player hand the quest in to the NPC named in `completer_npc` instead of finishing on the spot. |
| `next_quest` | The quest that starts when this one ends - chains quests into a story. |
| `mail` | A letter sent to the player (`sender`, `subject`, `body`). |
| `availability` | Conditions before the quest can be taken. |
| `reward.experience`, `reward.items`, `reward.commands` | Experience, items and commands as rewards. |
| `take_items`, `ignore_damage`, `ignore_nbt` | For `item` steps: take the items, and match them loosely. |
| `x`, `y`, `z`, `dimension`, `radius` | The place for `location` and `area_kill` steps. |

## Notes

- **XenoSkill points** belong to XenoPixels Network's skill tree. XenoNPCs has no skill tree of its
  own, so in XenoNPCs turn that reward off and give items, experience or a command instead.
- **`manual` quests** only move when an operator advances them with XenoPixels Network's quest
  command, which XenoNPCs does not have - so in XenoNPCs they never finish. Use `dialog`, `item` or
  `talk_to_npc` instead.
