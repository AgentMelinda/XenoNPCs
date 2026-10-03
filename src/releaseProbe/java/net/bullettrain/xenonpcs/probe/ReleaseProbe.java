package net.bullettrain.xenonpcs.probe;

import com.dragonminez.common.stats.StatsData;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.bullettrain.xenonpcs.compat.npc.NpcDmzStats;
import net.bullettrain.xenonpcs.compat.npc.NpcStatsDataAccess;
import net.bullettrain.xenonpcs.mixin.ConditionalMixinPlugin;
import net.bullettrain.xenonpcs.missile.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStreamReader;
import java.util.List;

/** Runs only in the separate development source set and fresh disposable worlds. */
@Mod("xenonpcs_release_probe")
public final class ReleaseProbe {
    private LivingEntity npc;
    private StatsData stats;
    private int ticks;
    public ReleaseProbe() {
        String profile = System.getProperty("xenonpcs.releaseProfile", "core");
        require(net.minecraftforge.fml.ModList.get().isLoaded("customnpcs") == !profile.equals("core"), "CustomNPCs profile discovery");
        require(net.minecraftforge.fml.ModList.get().isLoaded("cnpcgeckoaddon") == profile.equals("gecko"), "CNPC Gecko profile discovery");
        MinecraftForge.EVENT_BUS.addListener(this::started);
        MinecraftForge.EVENT_BUS.addListener(this::tick);
    }
    private void started(ServerStartedEvent event) {
        forceTargets("mixins");
        var level = event.getServer().overworld();
        npc = ModEntities.XENO_NPC_HUMANOID.get().create(level);
        require(npc != null, "native NPC creation");
        ((net.minecraft.world.entity.Mob) npc).setNoAi(true);
        npc.moveTo(level.getSharedSpawnPos(), 0, 0);
        stats = NpcDmzStats.getOrCreate(npc);
        require(stats != null && stats.getPlayer() == null, "NPC null player stats");
        require(((NpcStatsDataAccess) stats).xenopixels$getNpcHost() == npc, "bound NPC host");
        stats.getStats().setStrength(100);
        stats.getStats().setStrikePower(100);
        stats.getStats().setResistance(100);
        stats.getStats().setVitality(100);
        stats.getStats().setKiPower(100);
        stats.getStats().setEnergy(100);
        require(stats.getStats().getResistance() == 100, "NPC stat attribute write/read");
        npc.getAttribute(Attributes.ARMOR).setBaseValue(0);
        double defense = stats.getDefense(), maxDefense = stats.getMaxDefense();
        finite(defense); finite(maxDefense);
        npc.getAttribute(Attributes.ARMOR).setBaseValue(10);
        require(stats.getDefense() > defense && stats.getMaxDefense() > maxDefense, "NPC armor contribution");
        var player = net.minecraftforge.common.util.FakePlayerFactory.getMinecraft(level);
        StatsData playerStats = new StatsData(player);
        ((NpcStatsDataAccess) playerStats).xenopixels$markLoaded();
        player.getAttribute(Attributes.ARMOR).setBaseValue(0);
        double playerDefense = playerStats.getDefense();
        player.getAttribute(Attributes.ARMOR).setBaseValue(10);
        require(playerStats.getDefense() > playerDefense, "ordinary player armor path");
        require(level.addFreshEntity(npc), "spawn native NPC");
        LogUtils.getLogger().info("XENONPCS_PROBE_ASSERTIONS_PASS defense={} maxDefense={} armoredDefense={} playerDefense={}",
                defense, maxDefense, stats.getDefense(), playerStats.getDefense());
    }
    private void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || stats == null) return;
        finite(stats.getDefense()); finite(stats.getMaxDefense()); finite(stats.getBattlePowerExact());
        require(stats.getBattlePowerExact() > 0, "positive battle power");
        if (++ticks == 40) {
            LogUtils.getLogger().info("XENONPCS_PROBE_SERVER_PASS ticks={} battlePower={}", ticks, stats.getBattlePowerExact());
            npc.getServer().halt(false);
        }
    }
    static void forceTargets(String side) {
        ClassLoader loader = ReleaseProbe.class.getClassLoader();
        var policy = new ConditionalMixinPlugin();
        int count = 0;
        try {
            for (String config : List.of("xenonpcs.mixins.json", "xenonpcs.compat.mixins.json")) {
                try (var reader = new InputStreamReader(loader.getResourceAsStream(config))) {
                    var json = JsonParser.parseReader(reader).getAsJsonObject();
                    for (var entry : json.getAsJsonArray(side)) {
                        String mixin = json.get("package").getAsString() + "." + entry.getAsString();
                        ClassNode node = new ClassNode();
                        try (var bytes = loader.getResourceAsStream(mixin.replace('.', '/') + ".class")) {
                            new ClassReader(bytes).accept(node, ClassReader.SKIP_CODE);
                        }
                        for (AnnotationNode a : node.invisibleAnnotations) {
                            if (!a.desc.endsWith("/Mixin;")) continue;
                            for (int i = 0; i < a.values.size(); i += 2) {
                                if (!(a.values.get(i).equals("value") || a.values.get(i).equals("targets"))) continue;
                                for (Object target : (List<?>) a.values.get(i + 1)) {
                                    String name = target instanceof Type type ? type.getClassName() : target.toString();
                                    if (!policy.shouldApplyMixin(name, mixin)) continue;
                                    Class.forName(name, false, loader);
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
            LogUtils.getLogger().info("XENONPCS_PROBE_TARGETS_PASS side={} count={}", side, count);
        } catch (Exception e) { throw new IllegalStateException("Release mixin target probe", e); }
    }
    private static void require(boolean value, String check) {
        if (!value) throw new IllegalStateException("Release probe failed: " + check);
    }
    private static void finite(double value) { require(Double.isFinite(value), "finite DMZ value " + value); }
}
