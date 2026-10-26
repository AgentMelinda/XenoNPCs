# Combat Brain

The combat brain makes an NPC fight like a DragonMineZ saga enemy: close in, punch, fire ki, block,
fly after you, transform.

## Turning it on

It is **off by default**, so an NPC you placed does not start a fight on its own.

1. Open the NPC in the [editor](The-Editor).
2. Go to the **Brain** tab.
3. Switch **Combat brain** on and save.

## The V9 brain

XenoNPCs has one brain, **V9**: DragonMineZ's own saga decision tree - the same logic DMZ's saga
enemies use - with a switch for every action. The Brain tab lists those actions (strike, charge,
ki blasts and waves, flying, landing, vanish, deflecting ki...). Turn off anything this NPC should
never do.

A new NPC starts with **ki-blast and ki-wave deflection off**; switch them on for a tougher fighter.

## How it fights

- It picks melee when you are close and ki when you are further away, like DMZ's saga enemies.
- It can hit you a block or two above or below it (on a step, a slab, mid-jump); much higher than
  that, it uses ki or flies up to you.
- If its punch cannot reach, it closes in instead of swinging at the air.
- With the **Fly** skill (see [DragonMineZ NPCs](DragonMineZ-NPCs)) it follows you into the air.

## Aim

Its ki blasts lead a moving target by the **Accuracy** on the Stats > Ranged Properties page.
