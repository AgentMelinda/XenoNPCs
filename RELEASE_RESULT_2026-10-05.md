# Release result — XenoNPCs 0.0.6

**Date:** 2026-10-05

**Correction (2026-10-05):** The 0.0.6 exports omitted 28 registered Maker Studio PNGs. The initialization-only client probe did not catch missing resources. The 0.0.7 atlas patch restores them and adds asset/dimension tests plus rendered-menu captures; see `RELEASE_ATLAS_2026-10-05.md`. The 0.0.6 receipts below describe the original binaries, which remain immutable.

Published release: https://github.com/AgentMelinda/XenoNPCs/releases/tag/v0.0.6
Successful combined build/test/server-probe/publication run: https://github.com/AgentMelinda/XenoNPCs/actions/runs/37325263832

## Source and identity

- NeoForge branch `1.21.1`, release tag `v0.0.6`, code commit `5e108d6b29e350db1911c0df498b48ce99121775`.
- Forge branch `1.20.1`, pinned code commit `602688e107d7084dda0e0fbae61d6c68a3cec7b1`.
- DragonMineZ branch `1.21.1-v2.1`, respawn fix `f1caa5215a20d5bcde06c9cb624edbf07f4b3103`.
- All commits use jacky yuval <gitlab_admin_263562@gitlab.xpn.co.il>. Both XenoNPCs branch pushes and the release tag push succeeded; both folders have the same local release tag. DMZ source push succeeded.
- This document is a post-release receipt; the release code commits and tag remain immutable. Documentation commits after the tag do not change published binaries.

## Published artifacts verified by fresh download

| File | Bytes | SHA-256 |
|---|---:|---|
| `xenonpcs-0.0.6-1.20.1-all.jar` | 13830704 | `64a806056e60d545aeba58af6ba44bf683b48de79af4f25b2bb4a5a8ce932690` |
| `xenonpcs-0.0.6-1.21.1-all.jar` | 15765460 | `eb11f7bd9b35e52803df2ab14b6c1a2f2529d92a57a5216f3ea20c356cf25e11` |

Exactly two -all assets were published. Downloaded hashes match GitHub's asset digests. Both contain the hub, tattoo, race, form and hair maker classes and exclude the development probe. NeoForge nests only Nashorn 15.4 plus jarjar metadata; Forge nests only MixinExtras 0.4.1 plus jarjar metadata.

For NeoForge, the exact patched DMZ prerequisite is https://raw.githubusercontent.com/AgentMelinda/XenoNPCs/v0.0.6/libs/dragonminez-2.1.3.jar — 61,614,552 bytes, SHA-256 `55b2d8ff71a1951a68e7967badc6eba2c8da3a4e1faf07a900111a96d91948b1`. This URL was downloaded and its hash verified. It is not the Forge dependency.

## Evidence

- Both CI build/test jobs and fresh isolated core server probes passed before Publish ran. Logs captured at `build/release-downloads/v0.0.6/workflow.log` in the NeoForge folder.
- Fresh Linux server logs: NeoForge 2026-10-05 14:32:28–14:32:31 UTC, Forge 2026-10-05 14:34:38–14:34:40 UTC. Each records tattoo persistence/ki-height policy assertions and 40 native NPC ticks.
- Both branch build workflows passed: NeoForge https://github.com/AgentMelinda/XenoNPCs/actions/runs/37325230864 and Forge https://github.com/AgentMelinda/XenoNPCs/actions/runs/37325148453.
- Local suites passed 995 NeoForge tests (including two preserved owner-only untracked tail tests) and 971 Forge tests. Fresh local clients initialized all five makers; Forge also passed with CustomNPCs/CNPC Gecko and GeckoLib 4.8.4. Exact commands and observations are in RELEASE_HANDOFF_2026-10-05.md.
- A duplicate pending tag-event release run was cancelled; the completed combined run above is the publication evidence.
- DMZ's independent dev deployment workflow stopped before build because DMZ_VPS_HOST/VPS configuration is absent: https://github.com/AgentMelinda/dragonminez-1.21.1/actions/runs/37322820889. The local DMZ full build/regression test and XenoNPCs CI using that jar passed. No deployment configuration was changed.

## Limits and safe next steps

Interactive visual tattoo dragging, aura v4/model-size appearance, multiplayer maker writes and production modpack gameplay remain unverified. The generic skill strict-evidence auditor still reports preexisting reflection/string/optional-integration warnings; it is not a clean strict audit. Do not infer those observations from unit tests or startup logs.

Use Model > Maker Studio for the selected NPC or `/xenomaker` for your player. Race edits require server OP2, revision checks, staging and backups. Test installation in a disposable world copy before replacing production jars. No world/chunk conversion code changed. Parent C:\XenoPixelsNetwork_qwen remained read-only.

## Preserved dirty paths in this folder

```
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

No tracked code changes remain outside the release commits. Owner logs, archives and untracked material are preserved. Task probe processes have exited; unrelated owner processes were not stopped.
