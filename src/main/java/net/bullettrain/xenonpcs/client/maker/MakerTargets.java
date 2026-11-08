package net.bullettrain.xenonpcs.client.maker;

import com.dragonminez.common.hair.HairManager;
import com.dragonminez.common.hair.CustomHair;
import net.bullettrain.xenonpcs.compat.npc.NpcCombatProfile;
import net.bullettrain.xenonpcs.hair.HairApplyService;
import net.bullettrain.xenonpcs.hair.HairMakerDocument;
import net.bullettrain.xenonpcs.features.taotto.TaottoDocument;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.LivingEntity;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.UUID;
import java.util.function.Consumer;

/** Screen-local NPC drafts. A maker opened from chat has no NPC target. */
public final class MakerTargets {
    private static final Map<Screen, Target> TARGETS = new WeakHashMap<>();
    private MakerTargets() {}

    public record Target(int entityId, UUID uuid, NpcCombatProfile draft,
                         Consumer<NpcCombatProfile> accepted) {
        public LivingEntity entity() {
            var mc = Minecraft.getInstance();
            var entity = mc.level == null ? null : mc.level.getEntity(entityId);
            return entity instanceof LivingEntity living && uuid.equals(living.getUUID()) ? living : null;
        }
        public void apply(Consumer<NpcCombatProfile> change) {
            if (entity() == null) throw new IllegalStateException("The selected NPC is no longer loaded.");
            change.accept(draft);
            accepted.accept(NpcCombatProfile.fromTag(draft.toTag()));
        }
    }

    public static String label(Screen screen) {
        Target target = get(screen);
        if (target == null) return "Your character";
        var entity = target.entity();
        return "NPC #" + target.entityId + (entity == null ? " (unloaded)" : " · " + entity.getName().getString());
    }
    public static Target get(Screen screen) { return TARGETS.get(screen); }
    public static void inherit(Screen screen, Screen parent) {
        Target target = get(parent);
        if (target != null) TARGETS.put(screen, target);
    }
    public static void openNpc(Screen parent, int entityId, NpcCombatProfile profile,
                               Consumer<NpcCombatProfile> accepted) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.level.getEntity(entityId) instanceof LivingEntity entity)) return;
        Screen screen = new XenoMakerHubScreen(parent);
        TARGETS.put(screen, new Target(entityId, entity.getUUID(),
                NpcCombatProfile.fromTag(profile.toTag()), accepted));
        mc.setScreen(screen);
    }

    public static HairMakerDocument initialHair(Screen parent) {
        Target target = get(parent);
        if (target == null) return HairMakerDocument.oneStrandDemo();
        var document = new HairMakerDocument();
        String code = target.draft.hairCode;
        try {
            CustomHair hair = com.dragonminez.common.hair.HairCodec.isFullSetCode(code) ? com.dragonminez.common.hair.HairCodec.fromFullSetCode(code).get(com.dragonminez.common.hair.HairStyleSlot.BASE)
                    : com.dragonminez.common.hair.HairCodec.fromCode(code);
            if (hair != null) document.loadFromCustomHair(hair, "Base");
        } catch (RuntimeException ignored) { }
        document.globalColor(target.draft.hairColor);
        return document;
    }

    public static TaottoDocument initialTattoo(Screen parent) {
        Target target = get(parent);
        if (target != null) return target.draft.appearance.taotto.copy();
        var player = Minecraft.getInstance().player;
        return TaottoClientOverlays.document(player == null ? null : player.getUUID());
    }

    public static void applyHair(Screen screen, HairMakerDocument document) {
        Target target = get(screen);
        if (target == null) { HairApplyService.applyClient(document); return; }
        target.apply(profile -> {
            CustomHair edited = HairApplyService.toCustomHair(HairApplyService.plan(document));
            java.util.EnumMap<com.dragonminez.common.hair.HairStyleSlot, CustomHair> set =
                    new java.util.EnumMap<>(com.dragonminez.common.hair.HairStyleSlot.class);
            try {
                if (com.dragonminez.common.hair.HairCodec.isFullSetCode(profile.hairCode)) set = com.dragonminez.common.hair.HairCodec.fromFullSetCode(profile.hairCode);
                else {
                    CustomHair existing = com.dragonminez.common.hair.HairCodec.fromCode(profile.hairCode);
                    if (existing != null) for (var slot : com.dragonminez.common.hair.HairStyleSlot.values()) set.put(slot, existing.copy());
                }
            } catch (RuntimeException ignored) { }
            for (var slot : com.dragonminez.common.hair.HairStyleSlot.values()) set.putIfAbsent(slot, new CustomHair());
            set.put(com.dragonminez.common.hair.HairStyleSlot.byIndex(HairApplyService.styleIndex(document.style())), edited);
            profile.hairCode = com.dragonminez.common.hair.HairCodec.toFullSetCode(set);
            profile.hairColor = document.globalColor();
            profile.hairEnabled = true;
            profile.appearance.mode = net.bullettrain.xenonpcs.compat.npc.NpcDmzAppearance.Mode.FULL;
            profile.hairStyleId = 0;
        });
    }
}
