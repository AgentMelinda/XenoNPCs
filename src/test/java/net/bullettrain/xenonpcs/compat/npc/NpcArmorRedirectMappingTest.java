package net.bullettrain.xenonpcs.compat.npc;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Regression for NPC defense reads reached through third-party battle-power formulas. */
class NpcArmorRedirectMappingTest {
    @Test
    void bothDefenseReadsAreRequiredAndHaveAProductionArmorMapping() throws Exception {
        String owner = "net/bullettrain/xenonpcs/mixin/common/StatsDataNpcHostMixin120";
        ClassNode mixin = read(owner);
        var handler = mixin.methods.stream().filter(m -> m.name.equals("xenonpcs$hostArmor"))
                .findFirst().orElseThrow();
        AnnotationNode redirect = handler.visibleAnnotations.stream()
                .filter(a -> a.desc.endsWith("/Redirect;")).findFirst().orElseThrow();
        List<?> methods = (List<?>) value(redirect, "method");
        assertEquals(2, methods.size());
        assertEquals(2, value(redirect, "require"), "A skipped armor redirect must not become a tick-time NPE");
        AnnotationNode at = (AnnotationNode) value(redirect, "at");
        assertEquals(Boolean.TRUE, value(at, "remap"));
        String named = (String) value(at, "target");
        ClassNode dmz = read("com/dragonminez/common/stats/StatsData");
        for (Object methodName : methods) {
            var method = dmz.methods.stream().filter(m -> m.name.equals(methodName) && m.desc.equals("()D"))
                    .findFirst().orElseThrow();
            int matches = 0;
            for (var instruction : method.instructions) {
                if (instruction instanceof MethodInsnNode invoke
                        && named.equals("L" + invoke.owner + ";" + invoke.name + invoke.desc)) matches++;
            }
            assertEquals(1, matches, "Expected one player armor read in " + methodName);
        }
        Path refmap = Path.of(System.getProperty("xenopixels.projectDir"),
                "build/tmp/compileJava/compileJava-refmap.json");
        var json = JsonParser.parseString(Files.readString(refmap)).getAsJsonObject();
        // Verified against the pinned released Forge DMZ jar, not just its deobfuscated dev copy.
        assertEquals("Lnet/minecraft/world/entity/player/Player;m_21230_()I",
                json.getAsJsonObject("mappings").getAsJsonObject(owner).get(named).getAsString());
    }

    private static ClassNode read(String name) throws Exception {
        try (var stream = NpcArmorRedirectMappingTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(stream);
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, 0);
            return node;
        }
    }

    private static Object value(AnnotationNode annotation, String key) {
        for (int i = 0; i < annotation.values.size(); i += 2) {
            if (key.equals(annotation.values.get(i))) return annotation.values.get(i + 1);
        }
        return null;
    }
}
