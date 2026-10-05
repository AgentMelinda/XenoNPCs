# Handoff — standalone makers and ki aura height

**Date:** 2026-10-05
**Repository:** C:\Users\Admin\.grok\worktrees\dragonminez\XenoPixelsNetwork_qwen\XenoNPCs-1.20.1
**Branch:** 1.20.1
**HEAD before implementation:** 70e6b05ea807168ac3568fca24bbfd0d0c40c2d3

## Current state

- Release candidate/tag: v0.0.6. Review the tag's combined release matrix for the exact Forge commit. Final post-push commit/run/asset evidence is in `RELEASE_RESULT_2026-10-05.md`.
- Owner Git identity: jacky yuval <gitlab_admin_263562@gitlab.xpn.co.il>. Origin remains https://github.com/AgentMelinda/XenoNPCs.git.
- Parent C:\XenoPixelsNetwork_qwen was read-only. No world/chunk conversion code or existing server worlds changed. Probes use only isolated build/release-runtime worlds.
- Preexisting owner dirt was preserved and excluded from staging:

```
audit-4.8.4.log
audit-run.log
ctx-1201.txt
errors-1201.txt
logs/2026-09-30-10.log.gz
logs/2026-09-30-11.log.gz
logs/2026-09-30-12.log.gz
logs/2026-09-30-13.log.gz
logs/2026-09-30-14.log.gz
logs/2026-09-30-15.log.gz
logs/2026-09-30-16.log.gz
logs/2026-09-30-17.log.gz
logs/2026-09-30-18.log.gz
logs/2026-09-30-19.log.gz
logs/2026-09-30-2.log.gz
logs/2026-09-30-20.log.gz
logs/2026-09-30-21.log.gz
logs/2026-09-30-22.log.gz
logs/2026-09-30-23.log.gz
logs/2026-09-30-24.log.gz
logs/2026-09-30-25.log.gz
logs/2026-09-30-26.log.gz
logs/2026-09-30-27.log.gz
logs/2026-09-30-28.log.gz
logs/2026-09-30-29.log.gz
logs/2026-09-30-3.log.gz
logs/2026-09-30-4.log.gz
logs/2026-09-30-5.log.gz
logs/2026-09-30-6.log.gz
logs/2026-09-30-7.log.gz
logs/2026-09-30-8.log.gz
logs/2026-09-30-9.log.gz
logs/2026-10-01-2.log.gz
logs/2026-10-03-2.log.gz
logs/2026-10-03-3.log.gz
logs/2026-10-03-4.log.gz
logs/2026-10-03-5.log.gz
logs/2026-10-03-6.log.gz
logs/2026-10-03-7.log.gz
logs/2026-10-05-1.log.gz
logs/2026-10-05-2.log.gz
logs/2026-10-05-3.log.gz
logs/2026-10-05-4.log.gz
logs/bubble-green.log
logs/bubble-red.log
logs/client-bubble-probe-20260930.log
logs/client-bubble-trace-20260930.log
logs/client-native-probe-20260930.log
logs/client-stationary-20260930.log
logs/debug-1.log.gz
logs/debug-2.log.gz
logs/debug-3.log.gz
logs/debug-4.log.gz
logs/debug-5.log.gz
logs/debug.log
logs/export-bubble-probe-20260930.log
logs/export-bubble-trace-20260930.log
logs/export-native-motion-20260930.log
logs/export-stationary-20260930.log
logs/focused-native-motion-20260930.log
logs/focused-stationary-20260930.log
logs/latest.log
logs/prism-startup-tail-fix-2026-10-03.log
logs/release-0.0.5/core-4.8.3-client-2026-10-03.log
logs/release-0.0.5/core-4.8.3-server-2026-10-03.log
logs/release-0.0.5/core-4.8.4-client-2026-10-03.log
logs/release-0.0.5/core-4.8.4-server-2026-10-03.log
logs/release-0.0.5/custom-4.8.3-client-2026-10-03.log
logs/release-0.0.5/custom-4.8.3-server-2026-10-03.log
logs/release-0.0.5/custom-4.8.4-client-2026-10-03.log
logs/release-0.0.5/custom-4.8.4-server-2026-10-03.log
logs/release-0.0.5/gecko-4.8.3-client-2026-10-03.log
logs/release-0.0.5/gecko-4.8.3-server-2026-10-03.log
logs/release-0.0.5/gecko-4.8.4-client-2026-10-03.log
logs/release-0.0.5/gecko-4.8.4-server-2026-10-03.log
logs/resync-green.log
logs/resync-red.log
logs/runtime-bubble-missing-20260930.log
release-client-core.log
release-client-custom.log
release-core-4.8.3.log
release-core-4.8.4.log
release-custom-4.8.4.log
release-gecko-4.8.3.log
release-gecko-4.8.4.log
release-server-core.log
release-server-custom.log
runclient-answers-fix.log
runclient-bubble-movement-fix.log
runclient-codex-release-fix.log
runclient-codex-release-fix2.log
runclient-final-combat-fix.log
runserver-1201.log
runserver-codex-resume-1201.log
test-1201.log
validation-0.0.5.log
validation-final-0.0.5.log
wiki/Combat-Brain.md
wiki/Commands.md
wiki/CustomNPCs-and-MyNPCs.md
wiki/DragonMineZ-NPCs.md
wiki/FAQ-and-Troubleshooting.md
wiki/Home.md
wiki/Installing.md
wiki/Making-Quests.md
wiki/Roles-Dialogue-Quests.md
wiki/Scripting-Basics.md
wiki/Scripting-Reference.md
wiki/Settings.md
wiki/The-Editor.md
wiki/Your-First-NPC.md
wiki/_Footer.md
wiki/_Sidebar.md
wiki/logo.png
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

- Full test suite: 971, zero failures. Hair codec fixtures, tattoo old-save/copy persistence and server save bounds, ki width/height, archive traversal/bomb/rollback-preflight/additive preservation checks included.
- `./gradlew build`: pass after the explicit-root sidecar fix.
- `./gradlew build runServer runClient -PreleaseProbe -PreleaseNpcProfile=core`: pass, server log 2026-10-05 17:22:17 through 17:22:50; 40 native NPC ticks, armor/stat host, tattoo round trip and ki height policy verified.
- Core client log 2026-10-05 17:23:32 through 17:23:33: 6 common + 30 client target entries loaded and all five maker screens initialized.
- RegisteredMixinAuditTest audits all 71 registered mixins against exact dependency bytecode and Forge mappings. Its prior 68-count assertion was updated for the three added maker client hooks.
- `./gradlew runServer runClient -PreleaseProbe -PreleaseNpcProfile=gecko -Pgeckolib_version=4.8.4`: pass. Fresh logs 2026-10-05 17:27:40–17:28:46: 21 common + 50 client target entries, 40 NPC ticks and all five maker screens initialized with both optional mods present. Interactive gameplay remains unverified.
- Local runtime artifact: `xenonpcs-0.0.6-1.20.1-all.jar`, 13,830,313 bytes, SHA-256 `3832ebb01a18c3374fa476f3518e2f5295f82f99274158d1161287f8bb752a12`. Probe classes are excluded. NeoForge nests Nashorn 15.4 only; Forge nests MixinExtras 0.4.1 only (plus metadata.json).
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
