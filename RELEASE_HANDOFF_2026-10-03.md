# Handoff — Forge 1.20.1 mixin audit and local 0.0.5 candidate

**Date:** 2026-10-03  
**Repository:** C:/XenoPixelsNetwork_qwen/XenoNPCs-1.20.1  
**Branch:** 1.20.1  
**HEAD:** eb705f363e090d4d0853e2bc8ef8eade5780bb87

## Validation snapshot before publication

At the validation snapshot, the cached upstream comparison was 0 ahead / 0 behind and the work was uncommitted. The owner subsequently authorized commits under the configured Git identity, pushing both branches, and a v0.0.5 release tag. Publication status must be checked in GitHub Actions; the evidence below predates publication. Raw runtime logs remain local and are not committed. All task-owned release probe JVMs have stopped. Other user processes and existing game installations were not changed by this task.

Source/build/documentation changes were confined to this Forge project. XenoNPCs 1.21.1, parent XenoPixels source, parent export tools, user modpacks and existing archives were preserved. Earlier tail and NPC armor fixes were already dirty when this implementation began and are retained. Existing untracked handoffs, logs, ctx/errors files and wiki material were preserved.

Dependency evidence:

| Dependency | SHA-256 | Bytes |
| --- | --- | --- |
| Official Forge DMZ 2.1.3, Downloads/dragonminez-2.1.3 (2).jar; matches CurseMaven 8469416 | 5adfa8f48c07584e00e492d9688229dfcf884754d214059e024fb00492e00d79 | 61828647 |
| CustomNPCs 1.20.1.20260711, local libs | 604916a1cd0949fd426cab5fad8f839037d7e6f74d9f18a9afbeaa7731edfc38 | 10873769 |
| CNPC Gecko 1.20.1-1.0.14, local libs | 4321b9ac40f3c9c829f16304176a9c1ab32d907421b7af2e8428b6a17a4e07f2 | 304561 |
| Maven GeckoLib Forge 4.8.3 | 8fada49c19464069316b94203cffeeafc06e0af3d572443e7020c62cd0f20300 | 1039046 |
| Maven GeckoLib Forge 4.8.4, used for the second runtime matrix | 33765775385857e7be6dca712d37f1cf05b48d052b86a8453c93631009ea1106 | 1038208 |
| Installed GeckoLib Forge 4.8.4, read only for comparison | c04abba4fd354dd8c0e8a732f1f05f4dca893d2e1d962b2ab128bbd411478747 | 1038209 |

The two 4.8.4 jars each contain 443 entries. Every uncompressed entry is identical except META-INF/MANIFEST.MF; that difference is the Implementation-Timestamp (21:06:28 versus 21:07:43 on 2026-06-20, +1000). No installed dependency was replaced. Other runtime pins: Java 17, Minecraft 1.20.1, Forge 47.4.10, Curios 5.14.1+1.20.1 and TerraBlender 1.20.1-3.0.1.10.

## Changes

- Corrected AuraRenderer renderSparksImpl's extra view matrix and applyAndDraw's single boolean for Forge DMZ.
- Moved the third-person weapon render redirect from the nonexistent DMZWeaponsLayer lambda to KiWeaponRenderer.lambda$processWeapons$1, with the correct static handler.
- Corrected PlayerScriptData's Forge Event callback, inventory init mapping, custom scrolling render mapping, and melee vanilla method/invocation mapping.
- Used explicit development/SRG aliases for the custom Parts init and Pseudo NPC tick hooks. Restricted party command wrapping to the intended four-argument overload.
- Split inventory preview input into a new mixin on GuiContainerNPCInterface2, which owns the inherited click method; it applies only to GuiNPCInv instances.
- Enabled native DMZ appearance hooks and NPC swing suppression without requiring a legacy NPC mod. Kept optional integrations gated.
- Required matches for registered injections and the compatibility config's default. Preserved prior tail ModifyArg hooks and the exactly-two armor redirects.
- Added RegisteredMixinAuditTest for all 68 registrations, including actual dependency bytecode, production refmaps and the upstream player animation/reflection contracts. See [the complete inventory](MIXIN_AUDIT_2026-10-03.md).
- Added checksum enforcement during compilation, bumped to 0.0.5, separated CI distribution/sources uploads, and added development-only isolated probes. The probe source set is excluded from both distribution and sources jars.

No public API signature, packet protocol or saved-data migration was introduced.

## Verified

All commands were run from this project. Game JVMs used the configured Java 17 toolchain. Runtime logs use local Asia/Jerusalem time (UTC+03:00).

Focused validation included RegisteredMixinAuditTest and MixinPolicyTest. The dedicated tail and armor mapping tests are included in the full suite. The final commands were:

```powershell
.\gradlew.bat test --tests '*RegisteredMixinAuditTest' '-Pgeckolib_version=4.8.4' --console=plain
.\gradlew.bat test build jarJar --console=plain
git diff --check
```

All exited 0. The final pinned build completed in 50 seconds: **894 tests, zero failures/errors/skips** in plain JUnit. The existing runtime-only exclusions remain; this is not a claim that those excluded tests ran. Logs: audit-4.8.4.log and validation-final-0.0.5.log. Compiler and Gradle deprecation warnings remain.

Runtime commands (each profile passed both sides):

```powershell
.\gradlew.bat runServer runClient -PreleaseProbe -PreleaseNpcProfile=core --console=plain
.\gradlew.bat runServer -PreleaseProbe -PreleaseNpcProfile=custom --console=plain
.\gradlew.bat test --tests '*RegisteredMixinAuditTest' runClient -PreleaseProbe -PreleaseNpcProfile=custom --console=plain
.\gradlew.bat runServer runClient -PreleaseProbe -PreleaseNpcProfile=gecko --console=plain
# Repeat core, custom and gecko with this additional, quoted PowerShell argument:
# '-Pgeckolib_version=4.8.4'
```

These are fresh Forge development processes, using isolated generated worlds under build/release-runtime/. Core includes the configured Curios and TerraBlender dependencies. Custom adds CustomNPCs; gecko adds CustomNPCs and CNPC Gecko. The probe checks ModList discovery before accepting a profile.

| GeckoLib | Profile | Server pass, 2026-10-03 | Client pass, 2026-10-03 | Common/client target counts |
| --- | --- | --- | --- | --- |
| 4.8.3 | Core | 15:33:24.775 | 15:33:49.845 | 6 / 27 |
| 4.8.3 | CustomNPCs | 15:30:48.500 | 15:30:14.705 | 20 / 44 |
| 4.8.3 | CustomNPCs + CNPC Gecko | 15:31:23.972 | 15:32:05.972 | 21 / 47 |
| 4.8.4 | Core | 15:36:29.020 | 15:37:07.631 | 6 / 27 |
| 4.8.4 | CustomNPCs | 15:37:45.414 | 15:38:11.525 | 20 / 44 |
| 4.8.4 | CustomNPCs + CNPC Gecko | 15:38:49.278 | 15:39:18.441 | 21 / 47 |

All twelve logs have pass markers and zero mixin application/injection failures. The gecko client transformed targets for all 68 registrations. Clients completed loading to the accessibility onboarding screen and shut down automatically. Snapshots are retained in [logs/release-0.0.5](logs/release-0.0.5/); each filename includes profile, GeckoLib, side and date.

Every final server probe created and spawned a native Humanoid NPC with a null player and a bound host. It wrote/read DMZ stats, read defense and maximum defense without an exception, increased armor from 0 to 10, checked the ordinary player armor path using a FakePlayer, and read finite positive battle power for 40 ticks. Observed NPC defense 1.2 -> 1.45, base maximum defense 24, and battle power 393.14600463026727. This directly exercises the unsafe StatsData call from the supplied crash without adding a replacement formula.

The probe harness initially had configuration failures (missing launch main, missing generated refmap in development, optional jars visible without mod discovery) and an incomplete normal-world startup was stopped. Those results are superseded by the final twelve successful profiles. Earlier client evidence that stopped on LoadingErrorScreen is not counted. The corrected probe checks loading errors and actual mod discovery. None of those setup experiments changed user instances.

DMZ emits pre-existing missing configuration-template/model/shader warnings in the fresh environments; CustomNPCs also looks for optional JEI/TLauncher classes. These were not changed as part of the mixin fixes.

Final artifacts, rebuilt with the pinned GeckoLib 4.8.3 configuration:

| Artifact | Bytes | SHA-256 |
| --- | --- | --- |
| build/libs/xenonpcs-0.0.5-1.20.1-all.jar | 13551570 | d7cd00f8407ce1648bc56fca29accd36457be4cb664bc0cded1fa63a25c383d4 |
| build/libs/xenonpcs-0.0.5-1.20.1-sources.jar | 12129101 | 3f984bd929af01194a8a5e214b8ff23d55cbb2cf31edb6085078e11206cf6872 |
| build/libs/xenonpcs-0.0.5-1.20.1.jar | 13368449 | ac26897460ac24f01fe124bffd0434e77aa08cf6da536e3df4ff1ec53396857f |

Use the **all.jar** distribution. Its expanded mods.toml says 0.0.5. Its production refmap maps the StatsData armor invocation to Player.m_21230_()I. The only files under META-INF/jarjar/ are metadata.json and mixinextras-forge-0.4.1.jar. Nashorn and the development probe are absent. The probe is also absent from the sources artifact. Prior version artifacts and archives remain preserved.

## Not verified

- Launching the final reobfuscated all.jar in a production loader/modpack; the twelve runtime checks use development classes with the generated refmap, while packaging/refmap validation is static.
- The exact DMZ Revamp 2.1.1 / Noea combination from the supplied server crash. Those exact artifacts were unavailable. Direct native NPC StatsData defense and battle power were verified instead.
- Tail, eyebrow, aura, lightning, sparking, hair and ki weapon appearance; ordinary player visual parity; Oculus/shader packs; character creation/race carousel; animation completion/speed and cosmetic keyframe behavior during gameplay.
- Clicking/saving editors, inventory preview/Curios slots, scripts, dialogue/quests/party rewards, equipped armor versus the direct attribute probe, real logged-in player behavior, multiplayer, and ledge/respawn scenarios.
- Curios absence, unsupported integration versions, CI on GitHub, publication and installation. No claim is made that every injected callback fired merely because its target transformed.

## Next steps

1. Review the local candidate and dirty paths. Keep the pinned Forge DMZ artifact separate from the NeoForge jar. Do not regenerate from parent export tools without preserving these version-specific fixes.
2. In a separately authorized installation, use exactly one 0.0.5 all.jar and restart the game. Preserve the prior jar outside mods. Test the production modpack and the manual scenarios above using fresh logs.
3. Repeat the original server crash with the exact DMZ Revamp/Noea artifacts when available. Preserve unverified labels until those processes produce evidence.
4. Publish/stage/commit/tag/push only if explicitly requested. The CI upload path intentionally selects the current all.jar and separate sources, excluding stale local versions.

## Dirty paths at handoff

The following inventory includes pre-existing user changes and untracked material as well as this task's changes. It is not a staging list.

```
text
 M .github/workflows/build.yml
 M CHANGELOG.md
 M README.md
 M build.gradle
 M gradle.properties
 M src/main/java/net/bullettrain/xenonpcs/mixin/SharedNpcMixinPolicy.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzScriptAnimSpeedMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzStudioClipFinishMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzStudioPoseMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/common/StatsDataNpcHostMixin120.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityNpcMeleeAnimationMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityNpcScriptTickMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiCreationNewPartsDmzPanelMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiCustomScrollNopMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcInvCuriosMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcInvPreviewMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcStatsDmzAuthorityMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/NpcPartyRewardCommandMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/PlayerScriptDataEnabledMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzAttackControllerKeyframeMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzAuraGhostNpcMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzAuraStatScaleMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzHairFollowBodyMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzHairLayerNpcMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzMeleeHeadLookMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzMeleeHeadMolangMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzRenderHandIrisMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzWeaponsLayerIrisMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcAuraScaleMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcEmbeddedTailColorMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcEyebrowMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcHairProgressMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcTailColorMixin.java
 M src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/NpcSwingSuppressMixin.java
 M src/main/resources/xenonpcs.compat.mixins.json
 M src/test/java/net/bullettrain/xenonpcs/compat/npc/MixinPolicyTest.java
?? CRASH_TAIL_HANDOFF_2026-10-03.md
?? MIXIN_AUDIT_2026-10-03.md
?? RELEASE_HANDOFF_2026-10-03.md
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
?? src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcInvPreviewInputMixin.java
?? src/releaseProbe/
?? src/test/java/net/bullettrain/xenonpcs/compat/npc/DmzTailMixinTargetTest.java
?? src/test/java/net/bullettrain/xenonpcs/compat/npc/NpcArmorRedirectMappingTest.java
?? src/test/java/net/bullettrain/xenonpcs/compat/npc/RegisteredMixinAuditTest.java
?? test-1201.log
?? validation-0.0.5.log
?? validation-final-0.0.5.log
?? wiki/
```

## Clean CI mapping dependency follow-up

The first branch run 37127282996 and release run 37127285348 failed at RegisteredMixinAuditTest.java:41 because a fresh checkout had no build/createSrgToMcp/output.srg. Publication was skipped. The test task now explicitly depends on createSrgToMcp; this is a build dependency correction, with no runtime code change.

Validation: .\gradlew.bat test build jarJar --console=plain passed after the correction (894 tests). Then output.srg was moved to output.pre-ci-fix.srg under the same generated build directory; .\gradlew.bat test --tests '*RegisteredMixinAuditTest' --console=plain regenerated it and passed. The rebuilt local all jar SHA-256 is 66cf83a1eb8f56819f6cab0dc89300dc182149e31443d43c6f420643ab9cd6b9. The earlier hashes above remain the pre-publication snapshot.

The v0.0.5 tag is preserved. A new workflow dispatch from the corrected 1.21.1 release workflow pins this follow-up Forge commit instead of rewriting the tag. Check that dispatched run for the actual publication result.
