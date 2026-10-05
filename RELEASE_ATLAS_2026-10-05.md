# Handoff — Maker Studio atlas fix (0.0.7)

**Date:** 2026-10-05
**Repository:** C:\Users\Admin\.grok\worktrees\dragonminez\XenoPixelsNetwork_qwen\XenoNPCs-1.20.1
**Branch:** 1.20.1
**Pre-patch HEAD:** dfe35f80de3ecaa6c67a158013cf7fda5b31b912

## Current state

Release 0.0.6 registered seven maker shapes but omitted their 28 PNGs (four palettes) from both standalone jars. Its initialization-only client probe did not verify texture loading. This patch copies the exact generated donor PNGs from the read-only parent into the standalone resource namespace, adds full atlas resource/dimension regression coverage and rendered-menu capture probes. No runtime API, packet, save schema, dependency, world/chunk conversion or parent source changed.

Version is 0.0.7. Combined publication succeeded under immutable `v0.0.7`. Both CI build/server jobs passed before publication. The receipt below records code commits, CI links and downloaded artifact digests. Commit identity: jacky yuval <gitlab_admin_263562@gitlab.xpn.co.il>.

## Verified

- Regression RED on both loaders: `./gradlew test --tests '*AtlasAssetsTest'` (plus `-PofflineMcMeta` for NeoForge) failed on missing `/assets/xenonpcs/textures/gui/atlas/xeno_maker_race_card_blue.png` before restoring assets.
- `./gradlew build --console=plain` passed: 972 local tests, zero failures. NeoForge includes two preserved owner-only untracked tail tests; Forge retains its 34 documented runtime-only exclusions.
- All 28 restored PNGs byte-match the generated parent donor. `AtlasAssetsTest` resolves every registered shape/palette from processed resources and verifies readable PNGs and native dimensions (696 textures).
- Runtime jar `xenonpcs-0.0.7-1.20.1-all.jar`: 13853670 bytes; SHA-256 `d2687e7ca6fcfe0f0455735157cb899aaf710f7510fcc84385801416981896e9`. All 28 maker entries match the source PNGs.
- Fresh client `./gradlew runClient -PreleaseProbe -PreleaseNpcProfile=core --console=plain` resolves 696 atlas resources and captures hub/race/form/hair/tattoo menus. Evidence: `build/release-runtime/core-4.8.3/client/logs/latest.log`, `build/release-runtime/core-4.8.3/client/maker-screenshots/`, `build/atlas-client-007.log`. Menu captures use GUI scale 1; NeoForge's initial capture at default scale exposed inherited DMZ minimum-scale clipping.
- Parent `C:\XenoPixelsNetwork_qwen` was read only. Donor: `src/generated/resources/assets/xenopixelsmod/textures/gui/atlas/xeno_maker_*.png`. Existing standalone resources are authored packaged assets under `src/main/resources`.

## Not verified

Captures open the menus before entering a world and show “No local player”; they do not verify player/NPC preview models, editing, multiplayer writes or interactive tattoo placement. Inherited DMZ minimum GUI scale can clip menus at small GUI viewports; this focused texture patch does not change its layout behavior. Production resource-pack overrides and optional integration combinations were not exercised again. Fresh dedicated server proof passed in the release workflow before publication (receipt below).

## Preserved dirty paths

```text
?? audit-4.8.4.log
?? audit-run.log
?? ctx-1201.txt
?? errors-1201.txt
?? logs/
?? release-client-core.log
?? release-client-custom.log
?? release-core-4.8.3.log
?? release-core-4.8.4.log
?? release-custom-4.8.4.log
?? release-gecko-4.8.3.log
?? release-gecko-4.8.4.log
?? release-server-core.log
?? release-server-custom.log
?? runclient-answers-fix.log
?? runclient-bubble-movement-fix.log
?? runclient-codex-release-fix.log
?? runclient-codex-release-fix2.log
?? runclient-final-combat-fix.log
?? runserver-1201.log
?? runserver-codex-resume-1201.log
?? test-1201.log
?? validation-0.0.5.log
?? validation-final-0.0.5.log
?? wiki/
```

No cleanup or blanket staging is authorized. Stage only reviewed patch paths. Client processes exit automatically; unrelated owner processes must remain untouched.

## Next steps

Use the matching 0.0.7 -all jar. Preserve `v0.0.6` and its assets unchanged. Small-viewport clipping and in-world/interactive maker validation remain separate follow-up work.

## Post-release receipt

- Release: https://github.com/AgentMelinda/XenoNPCs/releases/tag/v0.0.7
- Successful combined build/test/server/publication run: https://github.com/AgentMelinda/XenoNPCs/actions/runs/37342854954
- NeoForge code/tag commit: `7edec0309d58c0099b9082f2f82ccf1c7097d336`.
- Forge pinned code commit: `5404da248999a8f9deb9d736a39c3974e1c37260`.
- Both branches and the annotated tag were pushed under jacky yuval <gitlab_admin_263562@gitlab.xpn.co.il>. Both folders resolve local `v0.0.7` to the combined NeoForge tag commit.
- Branch build workflows also passed: NeoForge https://github.com/AgentMelinda/XenoNPCs/actions/runs/37342800482 and Forge https://github.com/AgentMelinda/XenoNPCs/actions/runs/37342844851.
- Fresh Linux server success on 2026-10-05: NeoForge 16:44:05 UTC, Forge 16:47:21 UTC; each logs 40 native NPC ticks and persistence/ki-height assertions. Full CI log captured in the NeoForge folder at `build/atlas-release-ci-007.log`.
- Fresh local client atlas/render markers on 2026-10-05: 19:38:54–19:39:01 Asia/Jerusalem. All 696 resources resolved; all five captured menus were visually inspected for atlas artwork. The Race Maker footer clips at the 854×480 capture height even at GUI scale 1; this does not imply complete layout validation. No task probe process remains running.
- NeoForge DMZ SHA-256 remains `55b2d8ff71a1951a68e7967badc6eba2c8da3a4e1faf07a900111a96d91948b1`; dependency URL: https://github.com/AgentMelinda/XenoNPCs/raw/refs/tags/v0.0.7/libs/dragonminez-2.1.3.jar. Forge uses official DMZ SHA-256 `5adfa8f48c07584e00e492d9688229dfcf884754d214059e024fb00492e00d79`. No dependency changed in this patch.

| Published file | Bytes | SHA-256 |
|---|---:|---|
| `xenonpcs-0.0.7-1.20.1-all.jar` | 13854061 | `5562b6f976ea64bf5d59bebbefec042fe312d447abf3a29a8afff64988372812` |
| `xenonpcs-0.0.7-1.21.1-all.jar` | 15788368 | `7405b4a94ad59083ef15baacec594b25129612c9398b31701eb4aa278f4141c5` |

Downloaded release assets match GitHub's reported digests and contain all 28 maker PNGs byte-identical to the original donor. Both include the five maker classes and exclude development probes. NeoForge nests only Nashorn 15.4 plus jarjar metadata; Forge nests only MixinExtras 0.4.1 plus metadata. The independent ZIP verifier and checks are retained under the NeoForge folder's ignored `build/verify-atlas-release.py` and `build/release-downloads/v0.0.7/atlas-checks.json`.

This receipt is committed after the release tag; it does not change published artifacts. Final working trees contain only the preserved untracked owner paths listed above. Parent source and world files remained untouched.
