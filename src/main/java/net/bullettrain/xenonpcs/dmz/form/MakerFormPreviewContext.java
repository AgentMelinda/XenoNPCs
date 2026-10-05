package net.bullettrain.xenonpcs.dmz.form;

import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.character.Character;

/** A draft belongs only to the character being drawn on this thread, never the config registry. */
public final class MakerFormPreviewContext {
    private static final ThreadLocal<Override> CURRENT = new ThreadLocal<>();
    private MakerFormPreviewContext() { }

    public static FormConfig.FormData get(Character character) {
        Override override = CURRENT.get();
        return override != null && override.character == character && !override.stack ? override.data : null;
    }

    public static void draw(Character character, FormConfig.FormData data, Runnable draw) {
        draw(character, data, false, draw);
    }

    public static FormConfig.FormData getStack(Character character) {
        Override override = CURRENT.get();
        return override != null && override.character == character && override.stack ? override.data : null;
    }

    public static void draw(Character character, FormConfig.FormData data, boolean stack, Runnable draw) {
        Override previous = CURRENT.get();
        CURRENT.set(new Override(character, data, stack));
        try { draw.run(); }
        finally {
            if (previous == null) CURRENT.remove();
            else CURRENT.set(previous);
        }
    }

    private record Override(Character character, FormConfig.FormData data, boolean stack) { }
}
