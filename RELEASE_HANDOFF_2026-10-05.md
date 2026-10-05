# Handoff — standalone makers and ki aura height

**Date:** 2026-10-05
**Repository:** C:\Users\Admin\.grok\worktrees\dragonminez\XenoPixelsNetwork_qwen\XenoNPCs
**Branch:** 1.21.1
**HEAD before implementation:** de8d452da84bbaa1aadeac5bd333671da57326bf

## Current state

- Release candidate/tag: v0.0.6. The combined release matrix pins Forge commit `602688e107d7084dda0e0fbae61d6c68a3cec7b1`. Final post-push commit/run/asset evidence is in `RELEASE_RESULT_2026-10-05.md`.
- Owner Git identity: jacky yuval <gitlab_admin_263562@gitlab.xpn.co.il>. Origin remains https://github.com/AgentMelinda/XenoNPCs.git.
- Parent C:\XenoPixelsNetwork_qwen was read-only. No world/chunk conversion code or existing server worlds changed. Probes use only isolated build/release-runtime worlds.
- Preexisting owner dirt was preserved and excluded from staging:

```
CRASH_TAIL_HANDOFF_2026-10-03.md
build-1211.log
build-codex-resume-1211.log
build-final-combat-fix.log
runserver-1211.log
runserver-codex-resume-1211.log
src/test/java/net/bullettrain/xenonpcs/compat/npc/DmzTailMixinTargetTest.java
```

## Changes

- Tattoo, hair, race and form maker ports; native selected-NPC Apply through the editor's existing OP2/range/UUID/revision/lock/save checks; player `/xenomaker` commands retained.
- Preview drafts use scoped state and restore the real player/NPC context. NPC Apply hair/race/form enables Full DMZ appearance; tattoos remain additive to preset tattooType.
- NPC appearance schema 5 adds Taotto; old saves load an empty canvas. Player save/load/death copies preserve independent tattoo documents. Main ModNetwork packet numbering unchanged; new namespaced maker, taotto and race channels use protocol 1.
- Custom race operations are server authoritative (OP2, built-in packs read-only, expected revision), staged, archive/path bounded, with backups before update. Remote client catalog backups preserve preexisting local packs. No delete semantics for third-party DMZ patches.
- Ordinary ki charging adds width without vertical growth; transformation/model/power scaling retained. Legacy kiChargeHeight is ignored.
- NeoForge export restores missing NPC stat-host mixins, native DMZ hook gating and exact GeckoLib speed return descriptor; Forge's established target/mapping fixes remain in place.
- Patched NeoForge DMZ source commit: f1caa5215a20d5bcde06c9cb624edbf07f4b3103, independently built from clean base 5d433856d3c56a1068bf6a7307a5631bbe955730. Jar SHA-256 55b2d8ff71a1951a68e7967badc6eba2c8da3a4e1faf07a900111a96d91948b1 (61,614,552 bytes). Forge continues to use official 2.1.3 hash 5adfa8f48c07584e00e492d9688229dfcf884754d214059e024fb00492e00d79.

## Verified

- Full test suite: 995 (includes 2 preexisting untracked owner tail tests; 993 belong to the committed tree), zero failures. Hair codec fixtures, tattoo old-save/copy persistence and server save bounds, ki width/height, archive traversal/bomb/rollback-preflight/additive preservation checks included.
- `./gradlew build -PofflineMcMeta`: pass, then `./gradlew build runClient -PreleaseProbe -PofflineMcMeta`: pass after final native-mixin gate/sidecar changes.
- `./gradlew runServer -PreleaseProbe -PofflineMcMeta`: pass with patched DMZ, server log 2026-10-05 17:10:19 through 17:10:52; native stat host, player/NPC armor, tattoo round trip, ki height policy and 40 NPC ticks verified.
- Final client log 2026-10-05 17:25:17: 6 common + 30 client target entries loaded, all five maker screens initialized. This is initialization proof, not visual gameplay proof.
- DMZ clean-checkout `./gradlew test --tests com.dragonminez.compat.RespawnTargetTest` failed with the old selector, then passed after the fix; `./gradlew build` passed. Exact PlayerList bytecode has the third RemovalReason argument and the wrapped setHealth call.
- DMZ fix committed/pushed as `f1caa521` (full hash recorded below); original repository's unrelated Ki Sense/Memory edits remain untouched. Its independent deployment workflow failed on missing DMZ_VPS_HOST configuration before build; no deployment settings changed.
- Local runtime artifact: `xenonpcs-0.0.6-all.jar`, 15,765,369 bytes, SHA-256 `4c58a8b2d41ec0e3a683e045bb049d31bcd7c092c598e73be00e95b733747731`. Probe classes are excluded. NeoForge nests Nashorn 15.4 only; Forge nests MixinExtras 0.4.1 only (plus metadata.json).
- Workflow shell blocks pass bash syntax validation. Combined workflow builds/tests and fresh core server probes before publishing exactly two version-labelled -all assets; Forge commit is pinned.

## Not verified

- Interactive visual tattoo dragging, aura v4 size appearance, hair editing/rendering, race/form creation through multiplayer packets, and production modpack gameplay have not been manually observed.
- MyNPCs/Revamp/Noeaj optional runtime combinations are not proved by current probes. Forge excludes the documented 34 runtime-only tests from plain JUnit.
- Generic skill strict-evidence audit still reports existing reflection/string/optional-symbol warnings; Forge also conflicts with that auditor's fixed 1.21.1/Java21 baseline. It is not a clean strict audit. Exact dependency tests and runtime probe observations above are the narrower evidence.
- Actual player death/respawn health behavior has not been exercised interactively; the fixed hook is verified against pinned bytecode and fresh process startup.

## Next steps

- Install the matching -all release jar; on NeoForge also install the exact patched DMZ dependency linked in README/release notes.
- In a disposable copy of a world, exercise each NPC maker Apply, save/reload, tattoo placement, aura v4 model size and multiplayer server race-save conflicts before replacing a production installation.
- Preserve owner dirty files and backups. Do not run parent export scripts over these standalone ports or alter parent LinearReader work.
