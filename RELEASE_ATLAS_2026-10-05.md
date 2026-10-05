# Handoff — Maker Studio atlas fix (0.0.7)

**Date:** 2026-10-05  
**Repository:** C:\Users\Admin\.grok\worktrees\dragonminez\XenoPixelsNetwork_qwen\XenoNPCs-1.20.1  
**Branch:** 1.20.1  
**Pre-patch HEAD:** dfe35f80de3ecaa6c67a158013cf7fda5b31b912

## Current state

Release 0.0.6 registered seven maker shapes but omitted their 28 PNGs (four palettes) from both standalone jars. Its initialization-only client probe did not verify texture loading. This patch copies the exact generated donor PNGs from the read-only parent into the standalone resource namespace, adds full atlas resource/dimension regression coverage and rendered-menu capture probes. No runtime API, packet, save schema, dependency, world/chunk conversion or parent source changed.

Version is 0.0.7. Combined publication is pending the new immutable tag and both CI build/server jobs. The post-release receipt will record code commits, CI links and downloaded artifact digests. Commit identity: jacky yuval <gitlab_admin_263562@gitlab.xpn.co.il>.

## Verified

- Regression RED on both loaders: `./gradlew test --tests '*AtlasAssetsTest'` (plus `-PofflineMcMeta` for NeoForge) failed on missing `/assets/xenonpcs/textures/gui/atlas/xeno_maker_race_card_blue.png` before restoring assets.
- `./gradlew build --console=plain` passed: 972 local tests, zero failures. NeoForge includes two preserved owner-only untracked tail tests; Forge retains its 34 documented runtime-only exclusions.
- All 28 restored PNGs byte-match the generated parent donor. `AtlasAssetsTest` resolves every registered shape/palette from processed resources and verifies readable PNGs and native dimensions (696 textures).
- Runtime jar `xenonpcs-0.0.7-1.20.1-all.jar`: 13853670 bytes; SHA-256 `d2687e7ca6fcfe0f0455735157cb899aaf710f7510fcc84385801416981896e9`. All 28 maker entries match the source PNGs.
- Fresh client `./gradlew runClient -PreleaseProbe -PreleaseNpcProfile=core --console=plain` resolves 696 atlas resources and captures hub/race/form/hair/tattoo menus. Evidence: `build/release-runtime/core-4.8.3/client/logs/latest.log`, `build/release-runtime/core-4.8.3/client/maker-screenshots/`, `build/atlas-client-007.log`. Menu captures use GUI scale 1; NeoForge's initial capture at default scale exposed inherited DMZ minimum-scale clipping.
- Parent `C:\XenoPixelsNetwork_qwen` was read only. Donor: `src/generated/resources/assets/xenopixelsmod/textures/gui/atlas/xeno_maker_*.png`. Existing standalone resources are authored packaged assets under `src/main/resources`.

## Not verified

Captures open the menus before entering a world and show “No local player”; they do not verify player/NPC preview models, editing, multiplayer writes or interactive tattoo placement. Inherited DMZ minimum GUI scale can clip menus at small GUI viewports; this focused texture patch does not change its layout behavior. Production resource-pack overrides and optional integration combinations were not exercised again. Fresh dedicated server proof for this patch is delegated to the release workflow gate, before publication.

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

Commit the reviewed Forge paths first, pin its full commit in the NeoForge combined workflow, commit NeoForge, push both branches under the configured owner identity, create/push a new `v0.0.7` tag, monitor both build/server gates, then download and inspect both released -all jars. Preserve `v0.0.6` and its assets unchanged.
