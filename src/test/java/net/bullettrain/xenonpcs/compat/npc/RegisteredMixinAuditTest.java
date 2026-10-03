package net.bullettrain.xenonpcs.compat.npc;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.ClassReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Dependency bytecode contracts for every mixin shipped by this Forge export. */
class RegisteredMixinAuditTest {
    private final Map<String, ClassNode> classes = new HashMap<>();
    private final Map<String, String> vanillaNames = new HashMap<>();
    private final List<String> failures = new ArrayList<>();
    private int checked;
    private java.net.URLClassLoader dependencyResources;
    private java.net.URLClassLoader rawDependencyResources;
    private com.google.gson.JsonObject productionMappings;

    @Test
    void registeredMixinsMatchForgeDependencies() throws Exception {
        Path root = Path.of(System.getProperty("xenopixels.projectDir"));
        var urls = Arrays.stream(System.getProperty("xenonpcs.auditClasspath").split(java.io.File.pathSeparator))
                .map(Path::of).map(p -> {
                    try { return p.toUri().toURL(); } catch (Exception e) { throw new IllegalStateException(e); }
                }).toArray(java.net.URL[]::new);
        dependencyResources = new java.net.URLClassLoader(urls, getClass().getClassLoader());
        var rawUrls = Arrays.stream(System.getProperty("xenonpcs.rawAuditClasspath").split(java.io.File.pathSeparator))
                .map(Path::of).map(p -> {
                    try { return p.toUri().toURL(); } catch (Exception e) { throw new IllegalStateException(e); }
                }).toArray(java.net.URL[]::new);
        rawDependencyResources = new java.net.URLClassLoader(rawUrls, null);
        productionMappings = JsonParser.parseString(Files.readString(root.resolve("build/tmp/compileJava/mixins.xenonpcs.refmap.json")))
                .getAsJsonObject().getAsJsonObject("mappings");
        for (String line : Files.readAllLines(root.resolve("build/createSrgToMcp/output.srg"))) {
            String[] parts = line.split(" ");
            if (parts[0].equals("MD:")) vanillaNames.put(simple(parts[1]), simple(parts[3]));
            if (parts[0].equals("FD:")) vanillaNames.put(simple(parts[1]), simple(parts[2]));
        }
        for (String configName : List.of("xenonpcs.mixins.json", "xenonpcs.compat.mixins.json")) {
            var config = JsonParser.parseString(Files.readString(root.resolve("src/main/resources/" + configName))).getAsJsonObject();
            for (String side : List.of("mixins", "client")) {
                for (var entry : config.getAsJsonArray(side)) {
                    String name = config.get("package").getAsString().replace('.', '/') + "/" + entry.getAsString().replace('.', '/');
                    audit(name);
                    checked++;
                }
            }
        }
        assertEquals(68, checked, "Update audit coverage deliberately when registration changes");
        assertTrue(failures.isEmpty(), String.join("\n", failures));
    }

    private void audit(String name) throws Exception {
        ClassNode mixin = read(name);
        var annotation = annotations(mixin.visibleAnnotations, mixin.invisibleAnnotations).stream()
                .filter(a -> a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")).findFirst().orElseThrow();
        boolean mixinRemap = !Boolean.FALSE.equals(value(annotation, "remap"));
        List<String> targets = new ArrayList<>();
        for (Object type : list(value(annotation, "value"))) targets.add(((Type) type).getInternalName());
        for (Object target : list(value(annotation, "targets"))) targets.add(target.toString().replace('.', '/'));
        for (String targetName : targets) {
            ClassNode target = read(targetName);
            for (FieldNode field : mixin.fields) {
                for (var a : annotations(field.visibleAnnotations, field.invisibleAnnotations)) {
                    if (a.desc.endsWith("/Shadow;")) {
                        String fieldName = field.name.startsWith("shadow$") ? field.name.substring(7) : field.name;
                        var found = fields(target).stream().filter(f -> mapped(f.name).equals(mapped(fieldName)) && f.desc.equals(field.desc)).findFirst();
                        check(found.isPresent(), name + ": missing shadow field " + fieldName + field.desc);
                        found.ifPresent(f -> check(isStatic(f.access) == isStatic(field.access), name + ": shadow field staticness " + fieldName));
                    }
                }
            }
            for (MethodNode handler : mixin.methods) {
                for (var a : annotations(handler.visibleAnnotations, handler.invisibleAnnotations)) {
                    if (a.desc.endsWith("/Shadow;")) {
                        String methodName = handler.name.startsWith("shadow$") ? handler.name.substring(7) : handler.name;
                        var found = methods(target).stream().filter(m -> mapped(m.name).equals(mapped(methodName)) && m.desc.equals(handler.desc)).findFirst();
                        check(found.isPresent(), name + ": missing shadow method " + methodName + handler.desc);
                        found.ifPresent(m -> check(isStatic(m.access) == isStatic(handler.access), name + ": shadow method staticness " + methodName));
                    }
                    if (a.desc.endsWith("/Accessor;")) {
                        String fieldName = (String) value(a, "value");
                        if (fieldName == null) fieldName = Character.toLowerCase(handler.name.charAt(3)) + handler.name.substring(4);
                        Type type = Type.getReturnType(handler.desc);
                        if (type.equals(Type.VOID_TYPE)) type = Type.getArgumentTypes(handler.desc)[0];
                        String finalName = fieldName;
                        String desc = type.getDescriptor();
                        check(fields(target).stream().anyMatch(f -> mapped(f.name).equals(mapped(finalName)) && f.desc.equals(desc)), name + ": missing accessor field " + fieldName + desc);
                    }
                    if (value(a, "method") == null) continue;
                    boolean remap = value(a, "remap") instanceof Boolean b ? b : mixinRemap;
                    for (Object selector : list(value(a, "method"))) {
                        String selectedName = selector.toString().split("\\(", 2)[0];
                        boolean rawMatch = target.methods.stream().anyMatch(m -> m.name.equals(selectedName)
                                && (selector.toString().indexOf('(') < 0 || selector.toString().substring(selector.toString().indexOf('(')).equals(m.desc)));
                        boolean srgTarget = !rawMatch && target.methods.stream().anyMatch(m -> selects(selector.toString(), m));
                        boolean rawAlternative = target.methods.stream().filter(m -> selects(selector.toString(), m))
                                .anyMatch(m -> list(value(a, "method")).stream().anyMatch(s -> s.toString().equals(m.name) || s.toString().equals(m.name + m.desc)));
                        check(!srgTarget || remap || rawAlternative,
                                name + "::" + handler.name + ": named vanilla selector requires remapping: " + selector);
                        if (srgTarget && remap && !rawAlternative) {
                            var mappings = productionMappings.getAsJsonObject(name);
                            check(mappings != null && mappings.has(selector.toString()), name + ": missing production refmap for " + selector);
                        }
                        List<MethodNode> selected = injectionMethods(target).stream().filter(m -> selects(selector.toString(), m)).toList();
                        check(!selected.isEmpty(), name + "::" + handler.name + ": missing method " + selector);
                        for (MethodNode method : selected) {
                            check(isStatic(method.access) == isStatic(handler.access), name + "::" + handler.name + ": handler staticness for " + method.name);
                            if (a.desc.endsWith("/Inject;")) checkCallback(name, handler, method);
                            if (a.desc.endsWith("/ModifyReturnValue;")) {
                                Type[] captured = Type.getArgumentTypes(handler.desc);
                                Type returned = Type.getReturnType(method.desc);
                                check(captured.length > 0 && captured[0].equals(returned) && Type.getReturnType(handler.desc).equals(returned),
                                        name + "::" + handler.name + ": return modifier type mismatch");
                            }
                            if (a.desc.endsWith("/WrapMethod;")) {
                                Type[] captured = Type.getArgumentTypes(handler.desc);
                                check(captured.length > 0 && captured[captured.length - 1].getClassName().equals("com.llamalad7.mixinextras.injector.wrapoperation.Operation")
                                                && Arrays.equals(Arrays.copyOf(captured, captured.length - 1), Type.getArgumentTypes(method.desc))
                                                && Type.getReturnType(handler.desc).equals(Type.getReturnType(method.desc)),
                                        name + "::" + handler.name + ": WrapMethod signature mismatch: " + method.name + method.desc);
                            }
                            for (Object at : list(value(a, "at"))) {
                                checkAt(name, handler, method, a, (AnnotationNode) at, remap);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkCallback(String owner, MethodNode handler, MethodNode target) {
        Type[] args = Type.getArgumentTypes(handler.desc);
        if (args.length == 0) { check(false, owner + ": callback missing CallbackInfo"); return; }
        int callback = -1;
        for (int i = 0; i < args.length; i++) if (args[i].getDescriptor().startsWith("Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo")) { callback = i; break; }
        check(callback >= 0, owner + "::" + handler.name + ": callback missing CallbackInfo");
        if (callback >= 0) check(args[callback].getClassName().endsWith(Type.getReturnType(target.desc).equals(Type.VOID_TYPE) ? "CallbackInfo" : "CallbackInfoReturnable"),
                owner + "::" + handler.name + ": callback return contract");
        if (callback <= 0) return;
        Type[] expected = Type.getArgumentTypes(target.desc);
        check(callback <= expected.length, owner + "::" + handler.name + ": too many callback arguments");
        for (int i = 0; i < Math.min(callback, expected.length); i++) {
            boolean coerce = hasCoerce(handler.invisibleParameterAnnotations, i) || hasCoerce(handler.visibleParameterAnnotations, i);
            check(args[i].equals(expected[i]) || coerce, owner + "::" + handler.name + ": callback argument " + i + " " + args[i] + " != " + expected[i]);
        }
    }

    private static boolean hasCoerce(List<AnnotationNode>[] annotations, int index) {
        return annotations != null && index < annotations.length && annotations[index] != null
                && annotations[index].stream().anyMatch(a -> a.desc.endsWith("/Coerce;"));
    }

    private void checkAt(String owner, MethodNode handler, MethodNode method, AnnotationNode injection, AnnotationNode at, boolean remap) {
        String kind = (String) value(at, "value");
        String selector = (String) value(at, "target");
        if (kind.equals("NEW") && selector != null) {
            boolean found = false;
            for (var node : method.instructions) if (node instanceof TypeInsnNode type && type.getOpcode() == Opcodes.NEW && type.desc.equals(selector)) found = true;
            check(found, owner + "::" + handler.name + ": missing construction " + selector);
            return;
        }
        if (selector == null || !(kind.equals("INVOKE") || kind.equals("FIELD") || kind.equals("INVOKE_ASSIGN"))) return;
        int end = selector.indexOf(';');
        if (end < 0) { check(false, owner + ": unsupported selector " + selector); return; }
        String callOwner = selector.substring(1, end);
        String member = selector.substring(end + 1);
        int split = member.indexOf('(');
        if (split < 0) split = member.indexOf(':');
        String callName = member.substring(0, split);
        boolean atRemap = value(at, "remap") instanceof Boolean b ? b : remap;
        check(!callOwner.startsWith("net/minecraft/") || !vanillaNames.containsValue(callName) || atRemap,
                owner + "::" + handler.name + ": vanilla invocation requires remapping: " + selector);
        String desc = member.substring(split);
        List<AbstractInsnNode> matches = new ArrayList<>();
        for (var node : method.instructions) {
            if (node instanceof MethodInsnNode call && call.owner.equals(callOwner) && mapped(call.name).equals(mapped(callName)) && call.desc.equals(desc)) matches.add(node);
            if (node instanceof FieldInsnNode field && field.owner.equals(callOwner) && mapped(field.name).equals(mapped(callName)) && (":" + field.desc).equals(desc)) matches.add(node);
        }
        int ordinal = value(at, "ordinal") instanceof Integer n ? n : -1;
        check(!matches.isEmpty() && (ordinal < 0 || ordinal < matches.size()), owner + "::" + handler.name + ": missing " + selector + " ordinal=" + ordinal + " in " + method.name);
        if (matches.isEmpty()) return;
        if (injection.desc.endsWith("/Redirect;") && matches.get(0) instanceof MethodInsnNode call) {
            Type[] captured = Type.getArgumentTypes(handler.desc);
            List<Type> expected = new ArrayList<>();
            if (call.getOpcode() != Opcodes.INVOKESTATIC) expected.add(Type.getObjectType(call.owner));
            expected.addAll(Arrays.asList(Type.getArgumentTypes(call.desc)));
            check(Type.getReturnType(handler.desc).equals(Type.getReturnType(call.desc)), owner + "::" + handler.name + ": redirect return type");
            check(captured.length >= expected.size(), owner + "::" + handler.name + ": missing redirect arguments");
            for (int i = 0; i < Math.min(captured.length, expected.size()); i++) {
                check(captured[i].equals(expected.get(i)) || hasCoerce(handler.visibleParameterAnnotations, i) || hasCoerce(handler.invisibleParameterAnnotations, i),
                        owner + "::" + handler.name + ": redirect argument " + i + " " + captured[i] + " != " + expected.get(i));
            }
        }
        if (injection.desc.endsWith("/ModifyArg;")) {
            Type[] args = Type.getArgumentTypes(desc);
            int index = value(injection, "index") instanceof Integer n ? n : -1;
            if (index >= 0) {
                check(index < args.length && args[index].equals(Type.getReturnType(handler.desc)), owner + "::" + handler.name + ": ModifyArg index/type mismatch");
                Type[] captured = Type.getArgumentTypes(handler.desc);
                check(captured.length == 1 || Arrays.equals(captured, args), owner + "::" + handler.name + ": invocation capture mismatch");
            }
        }
    }

    private List<MethodNode> methods(ClassNode node) {
        List<MethodNode> result = new ArrayList<>(node.methods);
        if (node.superName != null && !node.superName.equals("java/lang/Object")) result.addAll(methods(read(node.superName)));
        return result;
    }
    private List<MethodNode> injectionMethods(ClassNode node) {
        List<MethodNode> result = new ArrayList<>(node.methods);
        if (node.name.equals("net/minecraft/client/player/AbstractClientPlayer")) {
            ClassNode upstream = read("com/dragonminez/mixin/client/PlayerGeoAnimatableMixin");
            var mixin = annotations(upstream.visibleAnnotations, upstream.invisibleAnnotations).stream()
                    .filter(a -> a.desc.endsWith("/Mixin;")).findFirst().orElseThrow();
            check(list(value(mixin, "value")).stream().anyMatch(t -> t instanceof Type type && type.getInternalName().equals(node.name)),
                    "DMZ player animation mixin must own AbstractClientPlayer");
            check(upstream.fields.stream().anyMatch(f -> f.name.equals("dragonminez$attackAnimTicks") && f.desc.equals("I")),
                    "DMZ studio clip counter reflection contract");
            check(read("software/bernie/geckolib/core/animation/AnimatableManager$ControllerRegistrar").fields.stream()
                    .anyMatch(f -> f.name.equals("controllers") && f.desc.equals("Ljava/util/List;")), "GeckoLib controller registrar reflection contract");
            result.addAll(upstream.methods.stream().filter(m -> !m.name.startsWith("<")).toList());
        }
        return result;
    }
    private List<FieldNode> fields(ClassNode node) {
        List<FieldNode> result = new ArrayList<>(node.fields);
        if (node.superName != null && !node.superName.equals("java/lang/Object")) result.addAll(fields(read(node.superName)));
        return result;
    }
    private ClassNode read(String name) {
        return classes.computeIfAbsent(name, key -> {
            var raw = rawDependencyResources.getResourceAsStream(key + ".class");
            try (var stream = raw != null ? raw : dependencyResources.getResourceAsStream(key + ".class")) {
                if (stream == null) throw new IllegalStateException("Missing target bytecode " + key);
                ClassNode node = new ClassNode();
                new ClassReader(stream).accept(node, 0);
                return node;
            } catch (Exception e) { throw new IllegalStateException(key, e); }
        });
    }
    private boolean selects(String selector, MethodNode method) {
        int split = selector.indexOf('(');
        String name = split < 0 ? selector : selector.substring(0, split);
        return mapped(name).equals(mapped(method.name)) && (split < 0 || selector.substring(split).equals(method.desc));
    }
    private String mapped(String name) { return vanillaNames.getOrDefault(name, name); }
    private static String simple(String name) { return name.substring(name.lastIndexOf('/') + 1); }
    private void check(boolean success, String failure) { if (!success) failures.add(failure); }
    private static boolean isStatic(int flags) { return (flags & Opcodes.ACC_STATIC) != 0; }
    private static List<?> list(Object value) { return value == null ? List.of() : value instanceof List<?> values ? values : List.of(value); }
    private static List<AnnotationNode> annotations(List<AnnotationNode> visible, List<AnnotationNode> invisible) {
        List<AnnotationNode> result = new ArrayList<>();
        if (visible != null) result.addAll(visible);
        if (invisible != null) result.addAll(invisible);
        return result;
    }
    private static Object value(AnnotationNode annotation, String key) {
        if (annotation.values != null) for (int i = 0; i < annotation.values.size(); i += 2) if (key.equals(annotation.values.get(i))) return annotation.values.get(i + 1);
        return null;
    }
}
