# Handoff — DragonMineZ tail-render crash

**Date:** 2026-10-03

## Current state

Scope: only C:/XenoPixelsNetwork_qwen/XenoNPCs and C:/XenoPixelsNetwork_qwen/XenoNPCs-1.20.1. Parent repository source was not changed. No commits, staging, tags or pushes.

Crash input: C:/Users/Admin/Downloads/Modpack test (for Jackit0 ) (1)/overrides/crash-reports/crash-2026-10-01_17.45.48-client.txt. Minecraft 1.20.1, Forge 47.4.10, Java 17.0.8, DMZ 2.1.3, XenoNPCs 0.0.4. During DMZ race-selection preview, DmzNpcTailColorMixin required a packColor(FFFF)I invocation that is absent from the Forge DMZ jar.

Repositories before changes:
- XenoNPCs: branch 1.21.1, HEAD 3de6cf2ff5e7a8d9a5b698da1246bc1b878ea09c, upstream divergence 0/0. Existing untracked paths: build-1211.log, build-codex-resume-1211.log, build-final-combat-fix.log, runserver-1211.log, runserver-codex-resume-1211.log.
- XenoNPCs-1.20.1: branch 1.20.1, HEAD eb705f363e090d4d0853e2bc8ef8eade5780bb87, upstream divergence 0/0. Existing untracked paths: ctx-1201.txt, errors-1201.txt, logs/, runclient-answers-fix.log, runclient-bubble-movement-fix.log, runclient-codex-release-fix.log, runclient-codex-release-fix2.log, runclient-final-combat-fix.log, runserver-1201.log, runserver-codex-resume-1201.log, test-1201.log, wiki/. Preserved.

Dependency evidence (SHA-256):
- NeoForge DMZ 2.1.3, XenoNPCs/libs/dragonminez-2.1.3.jar: 5a6e33ef5b992e64fccccaed895b105318b2ce1ab8039d8193107d40d203b581 (matches project pin).
- Forge DMZ 2.1.3, CurseMaven file 8469416, cached artifact dragonminez-1136088-8469416.jar: 5adfa8f48c07584e00e492d9688229dfcf884754d214059e024fb00492e00d79.
- GeckoLib: NeoForge 4.9.2 and Forge 4.8.3; exact configured jars inspected using javap.

## Changes

- Forge DmzNpcTailColorMixin: intercept verified GeoRenderer.renderRecursively float RGBA arguments 10–12, retaining alpha and required injection checks.
- Forge DmzNpcEmbeddedTailColorMixin: replace incompatible packed-integer method selector with the verified float signature and intercept renderCubesOfBone RGB arguments 5–7. Applies only to DMZ renderers, an active NPC override and recognized embedded tail bones; alpha and other bones' input colors remain untouched.
- Both projects: new src/test/java/net/bullettrain/xenonpcs/compat/npc/DmzTailMixinTargetTest.java. Reads compiled mixin annotations and exact dependency class bytecode without loading renderers; verifies method descriptors, invocation selectors and argument ordinals. Both tests passed per project.
- NeoForge production hooks already match its exact dependency artifacts; no NeoForge production code changed.
- New CRASH_TAIL_HANDOFF_2026-10-03.md in each project.
- No API, network, save-schema, version-pin, build-script or dependency-binary changes.

## Verified

Commands from each respective project directory (PowerShell):
- XenoNPCs: .\gradlew.bat test --tests '*DmzTailMixinTargetTest' -PofflineMcMeta --console=plain — exit 0.
- XenoNPCs: .\gradlew.bat test build jarJar -PofflineMcMeta --console=plain — exit 0; 917 tests, zero failures/errors/skips.
- XenoNPCs-1.20.1: $env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'; .\gradlew.bat test --tests '*DmzTailMixinTargetTest' --console=plain — exit 0.
- XenoNPCs-1.20.1: $env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'; .\gradlew.bat test build jarJar --console=plain — exit 0; 891 tests, zero failures/errors/skips. Project's pre-existing runtime-only test exclusions remain in place.
- javap -classpath <exact DMZ jar> -p -c -s com.dragonminez.client.render.layer.DMZRacePartsLayer — Forge has direct float RGBA renderRecursively call; NeoForge has packColor call.
- javap -classpath <GeckoLib 4.8.3 jar> -p -c -s software.bernie.geckolib.renderer.GeoEntityRenderer — verified float renderRecursively descriptor and renderCubesOfBone invocation.
- jar tf <distribution jar> — both tail mixin classes/configs present; Forge contains refmap and bundled MixinExtras 0.4.1.
- javap -classpath <Forge all jar> -p -v net.bullettrain.xenonpcs.mixin.compat.shared.DmzNpcTailColorMixin (and DmzNpcEmbeddedTailColorMixin) — final packaged annotations contain corrected selectors with require=1.
- git -C <project> diff --check — exit 0 (line-ending conversion notices only).

Distribution artifacts relative to parent workspace:
- XenoNPCs/build/libs/xenonpcs-0.0.4.jar: 15,487,475 bytes; SHA-256 910c4d2e2f212b198a3416e1f9054215dec34cf17c680a733c09b615d869208d.
- XenoNPCs-1.20.1/build/libs/xenonpcs-0.0.4-1.20.1-all.jar: 13,549,965 bytes; SHA-256 182cf3b3dad427e3523a2edb2f898ec2cb1b71819998672a7d304b502ead4ce7. Forge distribution with bundled MixinExtras.
- XenoNPCs-1.20.1/build/libs/xenonpcs-0.0.4-1.20.1.jar: 13,366,844 bytes; SHA-256 98a984677056a5f91b38a4c20c9b1ab6c861edb706be998ffbedc65b13a6005f.

## Not verified

No fresh gameplay process was run for this task. Static bytecode tests and successful packaging do not prove in-game mixin application or tail visuals. The original modpack's DMZ jar was not available in its overrides/mods directory; bytecode verification used the exact project's pinned CurseMaven artifact for the reported version.

Character creation/race carousel, native and compatibility NPC tail colors across races/forms, ordinary player colors, shaders, multiplayer and dedicated-server startup remain unverified for this change. No original modpack files were changed. Existing compiler warnings and Gradle deprecation warnings remain.

## Next steps

1. Replace the modpack's existing XenoNPCs Forge jar with the rebuilt 0.0.4-1.20.1-all.jar, retaining exactly one XenoNPCs jar. Stop and restart Minecraft before testing.
2. Open DMZ character creation/race selection with CustomNPCs present (as in the report), then exercise NPC tail overrides, default player colors and alpha. Inspect a fresh latest.log for injection failures, including both tail mixins.
3. Test Bio-Android and Frost Demon embedded tails, and Saiyan tails; confirm surrounding body bones retain their normal colors.
4. Repeat any needed NeoForge validation using its separate 1.21.1 jar. Keep both version-specific descriptors separate in future exports.

## Follow-up — synthetic Args startup crash (2026-10-03)

This section supersedes the Forge artifact hash and startup-verification status above.

The original installed Forge jar (SHA-256 182cf3b3dad427e3523a2edb2f898ec2cb1b71819998672a7d304b502ead4ce7) failed in a fresh Prism game on 2026-10-03 at 02:32:56 with NoClassDefFoundError for org/spongepowered/asm/synthetic/args/Args$1 during DMZ renderer registration. The static selector tests did not cover generation/classloading of Mixin's synthetic argument containers.

Changed both Forge tail hooks from ModifyArgs to three ModifyArg hooks, preserving the verified invocation targets, RGB indices, alpha, tail predicates and require=1. No Forge tail hook now needs a synthetic Args container. Expanded the bytecode test to reject ModifyArgs in these hooks and validate ModifyArg return types, indices, and full invocation capture signatures.

Focused command: $env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'; .\gradlew.bat test --tests '*DmzTailMixinTargetTest' --console=plain — exit 0.
Full command: $env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'; .\gradlew.bat test build jarJar --console=plain — exit 0; 891 tests, zero failures/errors. git diff --check passed.

Installed rebuilt jar in C:/Users/Admin/AppData/Roaming/PrismLauncher/instances/Modpack test (for Jackit0 ) (2)/minecraft/mods/xenonpcs-0.0.4-1.20.1-all.jar. SHA-256 d07ebd570dd12728a7cc34ffbbf7f0b8fdfb3245f21ffb75eed3ff287a96842d, 13,550,059 bytes; copied artifact hash matches. Previous jar preserved in instance's xenonpcs-backups/2026-10-03_02-37-10/. No other installed mod was replaced.

Verified installed DMZ jar hash matches the pinned Forge artifact above. Installed GeckoLib 4.8.4 SHA-256 c04abba4fd354dd8c0e8a732f1f05f4dca893d2e1d962b2ab128bbd411478747; javap verified its relevant descriptors/invocations also match.

Launched the named instance with PrismLauncher --launch "Modpack test (for Jackit0 ) (2)". Fresh javaw.exe PID 25860 started at 02:37:42 local time. Current latest.log records sound-engine start at 02:38:52.436, texture-atlas creation, and XenoNPCs animation registration at 02:38:54.128. No Args$ errors, NoClassDefFoundError or fatal startup errors were present in the inspected fresh log. Startup past the reported renderer-registration failure is verified; gameplay/tail visuals remain unverified. The game remains running with the rebuilt installed artifact.

Current startup produced separate nonfatal compatibility warnings for compat.customnpcs.PlayerScriptDataEnabledMixin and compat.customnpcs.GuiNpcInvCuriosMixin. These were not changed as part of the focused tail fix. They require a separate compatibility review.

Fresh log snapshot: XenoNPCs-1.20.1/logs/prism-startup-tail-fix-2026-10-03.log. Existing dirty paths remain preserved; new/changed paths for this follow-up are the two Forge tail mixins, Forge DmzTailMixinTargetTest.java, this handoff, and the log snapshot. XenoNPCs 1.21.1 source and parent source were unchanged during the follow-up; no commits or staging.

Next: test DMZ character creation and NPC tail overrides in the already-running instance. Keep the backup outside mods; do not install both jars together. Treat the two separate CustomNPCs warning paths as unverified until reviewed independently.

## Follow-up — NPC defense / battle-power tick crash (2026-10-03)

Input: C:/Users/Admin/Downloads/crash-2026-10-02_22.05.46-server (1).txt, dated 2026-10-02 22:05:46. Integrated Forge 1.20.1 server; entity xenonpcs:xeno_npc_humanoid (Humanoid). DMZ Revamp 2.1.1's battle-power signature calls StatsData.getMaxDefense, which dereferences the null player behind an NPC stats blob. The report contains other StatsData mixins, but this fix introduces no dependencies on their symbols.

Cause verified from exact pinned Forge DragonMineZ 2.1.3 jar (SHA-256 5adfa8f48c07584e00e492d9688229dfcf884754d214059e024fb00492e00d79): getMaxDefense()D and getDefense()D each invoke Player.m_21230_()I once. The existing StatsDataNpcHostMixin120 redirect named getArmorValue()I without remapping and permitted zero matches; production NPC defense therefore retained the unsafe player read.

Change: StatsDataNpcHostMixin120 keeps external DMZ method names unremapped, explicitly remaps the vanilla armor invocation, and requires/allows exactly two injections across the two defense methods. Existing handler selects NPC host armor, ordinary player armor, or zero when neither exists. No replacement battle-power formula, public API change or new third-party symbol was added. Changes for this follow-up are this mixin, new NpcArmorRedirectMappingTest.java and this handoff, all under XenoNPCs-1.20.1. NeoForge and parent source unchanged. Earlier tail fixes and all unrelated dirty paths retained.

Exact validation commands from XenoNPCs-1.20.1, with JAVA_HOME=C:/Program Files/Java/jdk-17:
- .\gradlew.bat compileJava --console=plain — exit 0.
- .\gradlew.bat test --tests '*NpcArmorRedirectMappingTest' --console=plain — exit 0. Checks both actual DMZ armor invocation sites and generated production refmap.
- .\gradlew.bat test build jarJar --console=plain — exit 0; 892 tests, zero failures/errors.
- git diff --check — exit 0, line-ending notices only.
- javap -classpath <pinned Forge DMZ jar> -p -c -s com.dragonminez.common.stats.StatsData — exact getMaxDefense/getDefense invocation evidence.
- Final jar's mixins.xenonpcs.refmap.json inspected via ZipFile: StatsDataNpcHostMixin120 maps Lnet/minecraft/world/entity/player/Player;getArmorValue()I to Lnet/minecraft/world/entity/player/Player;m_21230_()I.

Newest Forge distribution: build/libs/xenonpcs-0.0.4-1.20.1-all.jar, 13,550,139 bytes, SHA-256 7ec9e5c87f719e3df303c61cff8f6504f60de9f4e253bcac23882177f27bd3e5. This supersedes previous Forge artifact hashes above. Installed hash matches at C:/Users/Admin/AppData/Roaming/PrismLauncher/instances/Modpack test (for Jackit0 ) (2)/minecraft/mods/xenonpcs-0.0.4-1.20.1-all.jar.

Installation observation: before copying, the old .jar path no longer existed. The previous jar is present as xenonpcs-0.0.4-1.20.1-all.jar.duplicate (13,550,059 bytes); it was preserved. The attempted backup copy reported the absent old .jar, and the newly created xenonpcs-backups/2026-10-03_14-41-05 directory contains no old jar. Only the active rebuilt .jar was installed; no other mod or world was changed. No javaw.exe process was observed during this follow-up; no fresh gameplay process was launched. No commit, stage, tag or push.

Not verified: mixin application in a fresh game, NPC ticks with DMZ Revamp 2.1.1 and Noea from the supplied report, armor effects and player defense parity in gameplay. Earlier successful startup evidence predates this armor fix and does not prove this fix's runtime behavior.

Next: restart the affected modpack with this newest Forge jar, reproduce ticking the Humanoid NPC and reading battle power with DMZ Revamp enabled, inspect a fresh log, then check NPC and player armor behavior. The supplied crash came from a different environment than the earlier local Prism startup; installation in the local instance alone is not reproduction of that report. Leave the .duplicate file inactive and preserve the existing backups.
