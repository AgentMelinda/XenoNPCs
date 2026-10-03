# Forge 1.20.1 mixin audit — 2026-10-03

Scope: every registration in xenonpcs.mixins.json and xenonpcs.compat.mixins.json. The inventory now has 68 mixins; one new inventory input mixin targets the base class that owns the inherited click handler.

RegisteredMixinAuditTest reads the released DMZ and optional integration jars, using the mapped compile classpath for vanilla and GeckoLib classes. It checks target methods/descriptors, callback parameters and return contracts, staticness, shadows/accessors, invocation owners/descriptors/ordinals, ModifyArg indices/capture, Redirect and WrapMethod signatures, construction sites, and generated production mappings for vanilla selectors. DMZ-added player animation methods and reflective field names are checked against the upstream mixin and GeckoLib bytecode.

Runtime probes additionally force every active target through the real Forge/Mixin transformer with injection counting enabled. The gecko client profile covers all 68 registrations across common and client sides. This proves transformation/classloading, not that every callback was invoked or every visual feature rendered correctly. The separate dedicated-server probe creates a native NPC, checks null-player defense, NPC and player armor paths, writes/reads NPC stats and reads positive battle power for 40 server ticks.

All registered injections require matches, including compatibility hooks. The conditional plugin still excludes absent optional integrations. The build enforces the official Forge DMZ SHA-256.

The prior tail ModifyArgs-to-ModifyArg fixes and the armor redirect production mapping are retained and have dedicated regression tests. Source changes are confined to XenoNPCs-1.20.1. Parent source/export tools and the NeoForge project were not changed by this task.

| Registration | Side | Gate |
| --- | --- | --- |
| [common.KiLaserAimMixin](src/main/java/net/bullettrain/xenonpcs/mixin/common/KiLaserAimMixin.java) | Common | Core / required DMZ |
| [common.KiWaveAimMixin](src/main/java/net/bullettrain/xenonpcs/mixin/common/KiWaveAimMixin.java) | Common | Core / required DMZ |
| [common.StatsProviderNpcGetMixin](src/main/java/net/bullettrain/xenonpcs/mixin/common/StatsProviderNpcGetMixin.java) | Common | Core / required DMZ |
| [common.StatsNpcHostMixin120](src/main/java/net/bullettrain/xenonpcs/mixin/common/StatsNpcHostMixin120.java) | Common | Core / required DMZ |
| [common.StatsDataNpcHostMixin120](src/main/java/net/bullettrain/xenonpcs/mixin/common/StatsDataNpcHostMixin120.java) | Common | Core / required DMZ |
| [client.DmzLockOnAccessor](src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzLockOnAccessor.java) | Client | Core / required DMZ |
| [client.DmzLockOnNpcMixin](src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzLockOnNpcMixin.java) | Client | Core / required DMZ |
| [client.DmzStudioPoseMixin](src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzStudioPoseMixin.java) | Client | Core / required DMZ |
| [client.DmzStudioClipFinishMixin](src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzStudioClipFinishMixin.java) | Client | Core / required DMZ |
| [client.DmzResourceSyncPartialLoadMixin](src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzResourceSyncPartialLoadMixin.java) | Client | Core / required DMZ |
| [client.DmzScriptAnimSpeedMixin](src/main/java/net/bullettrain/xenonpcs/mixin/client/DmzScriptAnimSpeedMixin.java) | Client | Core / required DMZ |
| [compat.shared.NpcSwingSuppressMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/NpcSwingSuppressMixin.java) | Common | Core / required DMZ |
| [compat.customnpcs.NpcEffectResetMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/NpcEffectResetMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.EntityNpcProfilePersistMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityNpcProfilePersistMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.EntityNpcSayMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityNpcSayMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.EntityNpcScriptTickMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityNpcScriptTickMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.QuestCompletionCommandMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/QuestCompletionCommandMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.NpcPartyRewardCommandMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/NpcPartyRewardCommandMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.PlayerChatEventMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/PlayerChatEventMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.PlayerScriptDataEnabledMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/PlayerScriptDataEnabledMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.ScriptContainerXenoBindingMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/ScriptContainerXenoBindingMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.DataAdvancedRoleMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/DataAdvancedRoleMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.ContainerNpcInvCuriosMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/ContainerNpcInvCuriosMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.EntityNpcMeleeAnimationMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityNpcMeleeAnimationMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.EntityWrapperLegacyApiMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityWrapperLegacyApiMixin.java) | Common | CustomNPCs |
| [compat.customnpcs.EntityLivingBaseWrapperLegacyApiMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/EntityLivingBaseWrapperLegacyApiMixin.java) | Common | CustomNPCs |
| [compat.cnpcgecko.CustomModelDataMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/cnpcgecko/CustomModelDataMixin.java) | Common | CustomNPCs + CNPC Gecko |
| [compat.shared.XenoMouseHandlerAccessor](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/XenoMouseHandlerAccessor.java) | Client | Legacy NPC integration |
| [compat.customnpcs.GuiCustomScrollingPanelMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiCustomScrollingPanelMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiCustomScrollNopMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiCustomScrollNopMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiTextAreaMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiTextAreaMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiDialogInteractMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiDialogInteractMixin.java) | Client | CustomNPCs |
| [compat.shared.RenderTypeNullTextureGuardMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/RenderTypeNullTextureGuardMixin.java) | Client | Legacy NPC integration |
| [compat.customnpcs.GuiCreationNewPartsDmzPanelMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiCreationNewPartsDmzPanelMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiNpcMenuDmzTabMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcMenuDmzTabMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiNpcStatsDmzAuthorityMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcStatsDmzAuthorityMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiNpcAdvancedRoleMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcAdvancedRoleMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiNpcInterface2PreviewMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcInterface2PreviewMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiBasicPreviewInputMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiBasicPreviewInputMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiNpcInvPreviewMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcInvPreviewMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiNpcInvPreviewInputMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcInvPreviewInputMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.GuiNpcInvCuriosMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/GuiNpcInvCuriosMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.RenderCustomNpcHairMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/RenderCustomNpcHairMixin.java) | Client | CustomNPCs |
| [compat.customnpcs.RenderCustomNpcKiPoseMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/customnpcs/RenderCustomNpcKiPoseMixin.java) | Client | CustomNPCs |
| [compat.shared.DmzNpcAuraScaleMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcAuraScaleMixin.java) | Client | Core / required DMZ |
| [compat.shared.DmzNpcEyebrowMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcEyebrowMixin.java) | Client | Core / required DMZ |
| [compat.shared.DmzNpcActiveFormMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcActiveFormMixin.java) | Client | Core / required DMZ |
| [compat.shared.DmzNpcTailColorMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcTailColorMixin.java) | Client | Core / required DMZ |
| [compat.shared.DmzNpcEmbeddedTailColorMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcEmbeddedTailColorMixin.java) | Client | Core / required DMZ |
| [compat.shared.DmzNpcTransformTargetMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcTransformTargetMixin.java) | Client | Core / required DMZ |
| [compat.shared.DmzNpcHairProgressMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/shared/DmzNpcHairProgressMixin.java) | Client | Core / required DMZ |
| [compat.cnpcgecko.EntityCustomModelOwnerMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/cnpcgecko/EntityCustomModelOwnerMixin.java) | Client | CustomNPCs + CNPC Gecko |
| [compat.cnpcgecko.EntityUtilOwnerCopyMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/cnpcgecko/EntityUtilOwnerCopyMixin.java) | Client | CustomNPCs + CNPC Gecko |
| [compat.cnpcgecko.RenderCustomModelHairMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/cnpcgecko/RenderCustomModelHairMixin.java) | Client | CustomNPCs + CNPC Gecko |
| [compat.dmz.DmzWeaponsLayerIrisMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzWeaponsLayerIrisMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzHairLayerNpcMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzHairLayerNpcMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzAuraGhostNpcMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzAuraGhostNpcMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzAuraStatScaleMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzAuraStatScaleMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzCombatAnimationRegistryMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzCombatAnimationRegistryMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzCombatAnimationNamesAccessor](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzCombatAnimationNamesAccessor.java) | Client | Core / required DMZ |
| [compat.dmz.DmzStudioPlayableKeyMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzStudioPlayableKeyMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzAttackControllerKeyframeMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzAttackControllerKeyframeMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzGeoModelBt3AnimationMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzGeoModelBt3AnimationMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzPlayerModelAnimationFilesMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzPlayerModelAnimationFilesMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzMeleeHeadLookMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzMeleeHeadLookMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzMeleeHeadMolangMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzMeleeHeadMolangMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzHairFollowBodyMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzHairFollowBodyMixin.java) | Client | Core / required DMZ |
| [compat.dmz.DmzRenderHandIrisMixin](src/main/java/net/bullettrain/xenonpcs/mixin/compat/dmz/DmzRenderHandIrisMixin.java) | Client | Core / required DMZ |
