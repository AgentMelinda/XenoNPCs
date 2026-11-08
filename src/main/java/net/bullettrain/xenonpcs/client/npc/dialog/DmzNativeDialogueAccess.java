package net.bullettrain.xenonpcs.client.npc.dialog;

import com.dragonminez.client.gui.dialogue.NpcDialogueScreen;
import net.minecraft.network.chat.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

/** Verified private DMZ 2.2 dialogue models; does not call DMZ quest services. */
final class DmzNativeDialogueAccess {
    private static final Constructor<NpcDialogueScreen> SCREEN;
    private static final Method SPEECH, ROOT, OPTION, ENABLED, SET_PAGE;
    private static final boolean READY;
    static {
        Constructor<NpcDialogueScreen> screen = null;
        Method speech = null, root = null, option = null, enabled = null, setPage = null;
        boolean ready = false;
        try {
            ClassLoader loader = NpcDialogueScreen.class.getClassLoader();
            Class<?> page = Class.forName("com.dragonminez.client.gui.dialogue.DialoguePage", false, loader);
            Class<?> optionClass = Class.forName("com.dragonminez.client.gui.dialogue.DialogueOption", false, loader);
            screen = NpcDialogueScreen.class.getDeclaredConstructor(String.class, int.class,
                    boolean.class, boolean.class, List.class);
            screen.setAccessible(true);
            speech = access(page, "speech", String.class, Supplier.class, Supplier.class);
            root = access(page, "asRoot");
            option = access(optionClass, "of", String.class, Component.class, Runnable.class);
            enabled = access(optionClass, "enabled", boolean.class);
            setPage = access(NpcDialogueScreen.class, "setPage", page);
            String version = net.minecraftforge.fml.ModList.get().getModContainerById("dragonminez")
                    .map(container -> container.getModInfo().getVersion().toString()).orElse("");
            ready = version.equals("2.2") || version.startsWith("2.2.");
        } catch (ReflectiveOperationException | RuntimeException error) {
            net.bullettrain.xenonpcs.XenoNpcsMod.LOGGER.warn(
                    "DMZ 2.2 dialogue presentation unavailable; using Xeno dialogue", error);
        }
        SCREEN = screen; SPEECH = speech; ROOT = root; OPTION = option; ENABLED = enabled;
        SET_PAGE = setPage; READY = ready;
    }
    private DmzNativeDialogueAccess() {}
    private static Method access(Class<?> owner, String name, Class<?>... types)
            throws ReflectiveOperationException {
        Method method = owner.getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method;
    }
    static boolean available() { return READY; }
    static NpcDialogueScreen screen(int entityId) {
        try { return SCREEN.newInstance("xenonpcs", entityId, false, false, List.of()); }
        catch (ReflectiveOperationException error) { throw new IllegalStateException("DMZ dialogue contract changed", error); }
    }
    static Object option(String id, Component label, Runnable action, boolean enabledValue) {
        try { return ENABLED.invoke(OPTION.invoke(null, id, label, action), enabledValue); }
        catch (ReflectiveOperationException error) { throw new IllegalStateException("DMZ dialogue option contract changed", error); }
    }
    static void page(NpcDialogueScreen screen, String id, Supplier<Component> line,
                     Supplier<List<Object>> options) {
        try { SET_PAGE.invoke(screen, ROOT.invoke(SPEECH.invoke(null, id, line, options))); }
        catch (ReflectiveOperationException error) { throw new IllegalStateException("DMZ dialogue page contract changed", error); }
    }
}
