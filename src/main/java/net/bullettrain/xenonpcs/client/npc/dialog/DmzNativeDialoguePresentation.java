package net.bullettrain.xenonpcs.client.npc.dialog;

import com.dragonminez.client.gui.dialogue.NpcDialogueScreen;
import net.bullettrain.xenonpcs.client.npc.speech.BubbleText;
import net.bullettrain.xenonpcs.network.ModNetwork;
import net.bullettrain.xenonpcs.network.packet.XenoNpcDialoguePacket;
import net.bullettrain.xenonpcs.network.packet.XenoNpcTravelPacket;
import net.bullettrain.xenonpcs.npc.dialog.XenoDialogue;
import net.bullettrain.xenonpcs.npc.dialog.XenoDialogueText;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/** Xeno dialogue content displayed by DragonMineZ's own screen, portrait and option textures. */
public final class DmzNativeDialoguePresentation {
    private static final Map<NpcDialogueScreen, String> NAMES = new WeakHashMap<>();
    private static final Map<NpcDialogueScreen, Integer> IDS = new WeakHashMap<>();
    private static final Set<NpcDialogueScreen> SCREENS =
            java.util.Collections.newSetFromMap(new WeakHashMap<>());
    private final int entityId;
    private final XenoDialogue dialogue;
    private final String name;
    private final NpcDialogueScreen screen;

    private DmzNativeDialoguePresentation(int id, XenoDialogue dialogue, String name) {
        this.entityId = id; this.dialogue = dialogue; this.name = name == null ? "" : name;
        screen = DmzNativeDialogueAccess.screen(id);
        NAMES.put(screen, this.name); IDS.put(screen, id); SCREENS.add(screen);
    }
    public static boolean available() { return DmzNativeDialogueAccess.available(); }
    public static boolean owns(NpcDialogueScreen screen) { return SCREENS.contains(screen); }
    public static boolean suppressesWorldNameplate(int id) {
        return id >= 0 && Minecraft.getInstance().screen instanceof NpcDialogueScreen screen
                && IDS.getOrDefault(screen, -1) == id;
    }
    public static void open(int id, XenoDialogue dialogue, String name) {
        if (!available() || dialogue == null || dialogue.startNode() == null) return;
        DialogueBubbleSession.end();
        DmzNativeDialoguePresentation presentation = new DmzNativeDialoguePresentation(id, dialogue, name);
        presentation.show(dialogue.start());
        Minecraft.getInstance().setScreen(presentation.screen);
    }
    private Component text(String raw) {
        var player = Minecraft.getInstance().player;
        return BubbleText.styled(XenoDialogueText.resolve(raw,
                player == null ? "" : player.getName().getString(), name));
    }
    private void show(String nodeId) {
        XenoDialogue.Node node = dialogue.nodes().get(nodeId);
        if (node == null) { screen.onClose(); return; }
        List<Object> options = new ArrayList<>();
        for (int i = 0; i < node.options().size(); i++) {
            XenoDialogue.Option option = node.options().get(i);
            int sourceIndex = option.sourceIndex() >= 0 ? option.sourceIndex() : i;
            boolean enabled = option.type() != XenoDialogue.OptionType.ROLE
                    && ((option.type() != XenoDialogue.OptionType.QUEST
                    && option.type() != XenoDialogue.OptionType.DMZ_QUEST) || !option.quest().isBlank());
            options.add(DmzNativeDialogueAccess.option(Integer.toString(i), text(option.text()),
                    () -> choose(nodeId, option, sourceIndex), enabled));
        }
        DmzNativeDialogueAccess.page(screen, nodeId, () -> text(node.text()), () -> List.copyOf(options));
    }
    private void choose(String nodeId, XenoDialogue.Option option, int index) {
        switch (option.type()) {
            case TEXT -> { ModNetwork.sendToServer(new XenoNpcDialoguePacket(entityId, nodeId, index)); show(option.target()); }
            case QUEST, DMZ_QUEST, COMMAND -> { ModNetwork.sendToServer(new XenoNpcDialoguePacket(entityId, nodeId, index)); screen.onClose(); }
            case TRANSPORT -> { ModNetwork.sendToServer(new XenoNpcTravelPacket(entityId, option.target())); screen.onClose(); }
            case ROLE, QUIT -> screen.onClose();
        }
    }
}
