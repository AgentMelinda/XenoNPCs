package net.bullettrain.xenonpcs.compat.npc;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Checks compiled injection selectors against the exact dependencies on this project's classpath. */
class DmzTailMixinTargetTest {
    @Test
    void racePartsTailHooksMatchInstalledDragonMineZ() throws IOException {
        verify("DmzNpcTailColorMixin", "com/dragonminez/client/render/layer/DMZRacePartsLayer");
    }

    @Test
    void embeddedTailHooksMatchInstalledGeckoLib() throws IOException {
        verify("DmzNpcEmbeddedTailColorMixin", "software/bernie/geckolib/renderer/GeoEntityRenderer");
    }

    private static void verify(String mixinName, String targetName) throws IOException {
        ClassNode mixin = read("net/bullettrain/xenonpcs/mixin/compat/shared/" + mixinName);
        ClassNode target = read(targetName);
        int checked = 0;
        for (MethodNode handler : mixin.methods) {
            if (handler.visibleAnnotations == null) continue;
            for (AnnotationNode injection : handler.visibleAnnotations) {
                if (!injection.desc.startsWith("Lorg/spongepowered/asm/mixin/injection/")) continue;
                assertFalse(injection.desc.endsWith("/ModifyArgs;"),
                        "Forge tail hooks must not require synthetic Args classes");
                Object selectors = value(injection, "method");
                if (!(selectors instanceof List<?>)) continue;
                for (Object selector : (List<?>) selectors) {
                    MethodNode method = target.methods.stream()
                            .filter(m -> (m.name + m.desc).equals(selector)).findFirst().orElse(null);
                    assertNotNull(method, mixinName + " requires missing method " + selector);
                    checked++;
                    Object atValue = value(injection, "at");
                    List<?> ats = atValue instanceof List<?> ? (List<?>) atValue : List.of(atValue);
                    for (Object item : ats) {
                        AnnotationNode at = (AnnotationNode) item;
                        if (!"INVOKE".equals(value(at, "value"))) continue;
                        String call = (String) value(at, "target");
                        int matches = 0;
                        for (var instruction : method.instructions) {
                            if (instruction instanceof MethodInsnNode invoke
                                    && ("L" + invoke.owner + ";" + invoke.name + invoke.desc).equals(call)) {
                                matches++;
                            }
                        }
                        assertEquals(1, matches, mixinName + " invocation selector: " + call);
                        if (injection.desc.endsWith("/ModifyArg;")) {
                            Type[] arguments = Type.getArgumentTypes(call.substring(call.indexOf('(')));
                            int index = (Integer) value(injection, "index");
                            assertTrue(index >= 0 && index < arguments.length);
                            assertEquals(arguments[index], Type.getReturnType(handler.desc));
                            Type[] handlerArguments = Type.getArgumentTypes(handler.desc);
                            if (handlerArguments.length == 1) {
                                assertEquals(arguments[index], handlerArguments[0]);
                            } else {
                                assertArrayEquals(arguments, handlerArguments,
                                        "Multi-argument ModifyArg handlers must capture the exact invocation");
                            }
                        }
                    }
                    if (injection.desc.endsWith("/ModifyVariable;")) {
                        assertEquals(Boolean.TRUE, value(injection, "argsOnly"));
                        Type variableType = Type.getReturnType(handler.desc);
                        int ordinal = (Integer) value(injection, "ordinal");
                        long count = java.util.Arrays.stream(Type.getArgumentTypes(method.desc))
                                .filter(variableType::equals).count();
                        assertTrue(ordinal >= 0 && ordinal < count,
                                mixinName + " variable ordinal must exist in target arguments");
                    }
                }
            }
        }
        assertTrue(checked > 0, "No injection selectors checked for " + mixinName);
    }

    private static ClassNode read(String name) throws IOException {
        try (var stream = DmzTailMixinTargetTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(stream, "Missing bytecode for " + name);
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, 0);
            return node;
        }
    }

    private static Object value(AnnotationNode annotation, String key) {
        if (annotation.values != null) {
            for (int i = 0; i < annotation.values.size(); i += 2) {
                if (key.equals(annotation.values.get(i))) return annotation.values.get(i + 1);
            }
        }
        return null;
    }
}
