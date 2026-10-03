# XenoNPCs 0.0.5 release handoff

Date: 2026-10-03

The owner authorized commits, normal branch pushes, and a v0.0.5 tag to trigger the release workflow. Git author and committer: jacky yuval <gitlab_admin_263562@gitlab.xpn.co.il>. Remote: https://github.com/AgentMelinda/XenoNPCs.git.

## Reviewed branch inputs

- Forge 1.20.1: fe3703a8b3821f401ef6d2dfcfc126cbe594a5cc, pinned immutably in release.yml. Full mixin and fresh development runtime evidence is in that commit's MIXIN_AUDIT_2026-10-03.md and RELEASE_HANDOFF_2026-10-03.md.
- NeoForge 1.21.1: parent 3de6cf2ff5e7a8d9a5b698da1246bc1b878ea09c; this commit changes version, runtime jar classification, and release workflows. Its actual commit ID is available with git rev-parse HEAD. The release tag selects this branch's committed input.

## Validation before tagging

- Forge: .\gradlew.bat test build jarJar --console=plain; 894 tests, no failures/errors/skips. Twelve fresh development client/server probes passed across GeckoLib 4.8.3/4.8.4 and core/CustomNPCs/Gecko addon profiles.
- NeoForge: .\gradlew.bat test build jarJar -PofflineMcMeta --console=plain; 917 tests, no failures/errors/skips.
- Actionlint v1.7.12: both branch build workflows and the release workflow passed (ShellCheck disabled on Windows).
- NeoForge jar metadata contains version 0.0.5 and embeds Nashorn 15.4. Forge embeds MixinExtras 0.4.1. Runtime jar metadata checks precede publication; publish requires exactly both expected jars and both successful build jobs.

| Local runtime artifact | Bytes | SHA-256 |
| --- | --- | --- |
| Forge build/libs/xenonpcs-0.0.5-1.20.1-all.jar | 13551570 | d7cd00f8407ce1648bc56fca29accd36457be4cb664bc0cded1fa63a25c383d4 |
| NeoForge build/libs/xenonpcs-0.0.5-all.jar | 15487475 | 5e1f92ed80b13e395b9e71bad0e8acbd3585e147fa50f8d661dba9e8e372a1ba |

The release workflow names the assets xenonpcs-0.0.5-1.20.1-all.jar and xenonpcs-0.0.5-1.21.1-all.jar. CI rebuilds the jars, so CI hashes may differ from these local build hashes.

## Remaining evidence and preserved work

This document records the state before tag push; GitHub Actions and the release asset list are authoritative for publication success. No production modpack startup, rendering, multiplayer, or unavailable Revamp/Noeaj integration is verified here. No fresh NeoForge game process was run for these packaging changes. The Forge runtime probe logs remain local.

Unrelated untracked NeoForge tail test/handoff and build/server logs remain uncommitted. Forge raw logs, ctx/errors files, and wiki material remain uncommitted. Parent XenoPixels source and export tools were not edited. No game installation was changed by this release step. Safe next step after successful CI: use the jar matching the Minecraft version and loader, then verify the production modpack in a fresh process.

## CI follow-up without tag rewriting

First release run 37127285348 failed the new Forge audit because its generated SRG mapping file was not a dependency of test; publishing was skipped. Forge follow-up commit 70e6b05ea807168ac3568fca24bbfd0d0c40c2d3 explicitly generates that file. A full local build/test passed, and the focused audit passed with that output initially absent. The release workflow now pins this corrected Forge commit.

Tag v0.0.5 remains on NeoForge commit 36c81d05b7aed6d731e7636bc3a89efc4e4013a3, with no history rewriting. The corrected release is dispatched from branch 1.21.1 using tag=v0.0.5; the NeoForge build still checks out the immutable tag and Forge checks out the new immutable pin. The workflow run records the exact workflow commit. CI's NeoForge all jar is 15487576 bytes, SHA-256 3f0a669f745738918033aec1b7aea94dba49aebbc36a96a552139e1e2f59a8b3; its embedded version and Nashorn jar were inspected locally.
