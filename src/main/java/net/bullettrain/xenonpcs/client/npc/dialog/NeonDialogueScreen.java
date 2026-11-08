package net.bullettrain.xenonpcs.client.npc.dialog;

import com.dragonminez.client.gui.character.util.ScaledScreen;
import net.bullettrain.xenonpcs.client.npc.NpcGuiScale;
import net.bullettrain.xenonpcs.client.npc.quest.ClientQuests;
import net.bullettrain.xenonpcs.client.npc.speech.BubbleText;
import net.bullettrain.xenonpcs.client.screen.StudioViewport;
import net.bullettrain.xenonpcs.npc.dialog.XenoDialogue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Locale;

/**
 * The interactive NPC conversation menu, drawn from the neon source kit.
 *
 * <p>It replaces the speech bubbles for dialogue and quest offers: the NPC's line is typed into a
 * panel along the bottom of the screen, the options are buttons above it, and the NPC stands in a
 * portrait frame. The world stays visible behind it.
 *
 * <h2>Who decides what</h2>
 * Exactly as the bubbles did, because it shares their logic rather than copying it. The
 * conversation state is {@link DialogueBubbleSession} and picking an option goes through
 * {@link DialogueBubbleChoice#choose}: moving between text nodes is local presentation, and
 * anything with a consequence (a quest offer, a command, travel) is sent to the server as an
 * <em>index</em> that the server checks against its own copy of the dialogue. Nothing a quest
 * option does is decided on this side.
 */
public final class NeonDialogueScreen extends ScaledScreen {

    private static final int PAD = 5;
    private static final int LINE_H = 10;

    private final StudioViewport portraitView = new StudioViewport();

    private int firstVisible;
    private int focus;
    private String shownNode = "";
    private int ageTicks;
    private int pressedRow = -1;
    private boolean portraitFailed;

    public NeonDialogueScreen() {
        super(Component.literal("Dialogue"));
    }

    /**
     * Opens the menu, or does nothing when the dialogue has no node to show.
     *
     * <p>The session is started first: it owns the conversation, and a dialogue whose start node
     * does not exist yields none - an empty panel is worse than no panel.
     */
    public static void open(int entityId, XenoDialogue dialogue, String npcName) {
        if (DialogueBubbleSession.begin(entityId, dialogue, npcName) == null) {
            return;
        }
        Minecraft.getInstance().setScreen(new NeonDialogueScreen());
    }

    /** Drawn at the vanilla GUI Scale; see {@code NpcGuiScale}. */
    @Override
    protected float computeDynamicScale(int width, int height) {
        return NpcGuiScale.dynamicScale(super.computeDynamicScale(width, height));
    }

    @Override
    public boolean isPauseScreen() {
        // The NPC is still standing there; a conversation should not stop single-player time.
        return false;
    }

    /** No vanilla dimming layer: the world stays visible, with a soft fade under the panel. */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fillGradient(0, height * 45 / 100, width, height, 0x00000000, 0xB0000000);
    }

    @Override
    public void tick() {
        super.tick();
        ageTicks++;
    }

    // -- rendering -------------------------------------------------------------------------

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        DialogueBubbleSession session = DialogueBubbleSession.active();
        if (session == null) {
            onClose();
            return;
        }
        syncNode(session);

        double ux = toUiX(mouseX);
        double uy = toUiY(mouseY);
        super.render(graphics, (int) ux, (int) uy, partialTick);

        beginUiScale(graphics);
        List<XenoDialogue.Option> options = session.options();
        NeonDialogueLayout.Layout layout = layout(options.size());
        firstVisible = layout.first();

        drawPortrait(graphics, session, layout);
        drawPanel(graphics, session, layout, partialTick);
        drawOptions(graphics, session, options, layout, ux, uy);
        endUiScale(graphics);
    }

    private NeonDialogueLayout.Layout layout(int optionCount) {
        return NeonDialogueLayout.compute(getUiWidth(), getUiHeight(), optionCount, firstVisible);
    }

    /** A new node restarts the typing and puts the keyboard focus back on the first option. */
    private void syncNode(DialogueBubbleSession session) {
        String id = session.nodeId();
        if (!id.equals(shownNode)) {
            shownNode = id;
            ageTicks = 0;
            focus = 0;
            firstVisible = 0;
        }
    }

    private void drawPortrait(GuiGraphics g, DialogueBubbleSession session,
                              NeonDialogueLayout.Layout layout) {
        NeonDialogueLayout.Rect r = layout.portrait();
        // Render the NPC directly; the portrait card is intentionally omitted so the native
        // DMZ dialogue layout stays clear of the extra blue/orange frame.
        if (portraitFailed || minecraft == null || minecraft.level == null) {
            return;
        }
        Entity entity = minecraft.level.getEntity(session.entityId());
        if (!(entity instanceof LivingEntity npc)) {
            return;
        }
        // The frame's inner window, inside its border; the NPC is scaled to stand in it.
        int ix = r.x() + Math.round(r.w() * 0.09f);
        int iy = r.y() + Math.round(r.h() * 0.17f);
        int ix2 = r.right() - Math.round(r.w() * 0.09f);
        int iy2 = r.bottom() - Math.round(r.h() * 0.20f);
        int scale = Math.max(8, Math.round((iy2 - iy) / Math.max(0.5f, npc.getBbHeight()) * 0.82f));
        try {
            portraitView.render(g, ix, iy, ix2, iy2, npc, scale);
        } catch (RuntimeException e) {
            // A model that cannot be drawn in a GUI must not take the whole screen with it.
            portraitFailed = true;
        }
    }

    private void drawPanel(GuiGraphics g, DialogueBubbleSession session,
                           NeonDialogueLayout.Layout layout, float partialTick) {
        NeonDialogueLayout.Rect p = layout.panel();
        blit(g, NeonDialogueStyle.PANEL, p.x(), p.y(), p.w(), p.h());

        // Speaker name inside the panel's tab, scaled down rather than clipped when it is long.
        String name = session.npcName().isBlank() ? "???" : session.npcName();
        NeonDialogueLayout.Rect n = layout.nameplate();
        drawFitted(g, Component.literal(name), n.x() + 2, n.y(), n.w() - 4, n.h(),
                NeonDialogueStyle.LIME);

        NeonDialogueLayout.Rect t = layout.text();
        List<FormattedCharSequence> lines = font.split(BubbleText.styled(session.text()), t.w());
        int total = 0;
        for (FormattedCharSequence line : lines) {
            total += length(line);
        }
        int revealed = NeonTypewriter.visible(total, ageTicks);
        int maxLines = Math.max(1, t.h() / LINE_H);
        int y = t.y();
        for (int i = 0; i < lines.size() && i < maxLines && revealed > 0; i++) {
            FormattedCharSequence line = lines.get(i);
            int len = length(line);
            g.drawString(font, truncated(line, revealed), t.x(), y, NeonDialogueStyle.TEXT, false);
            revealed -= len;
            y += LINE_H;
        }
        if (lines.size() > maxLines && NeonTypewriter.done(total, ageTicks)) {
            g.drawString(font, "...", t.right() - font.width("..."), t.bottom() - 8,
                    NeonDialogueStyle.MUTED, false);
        }
    }

    private void drawOptions(GuiGraphics g, DialogueBubbleSession session,
                             List<XenoDialogue.Option> options, NeonDialogueLayout.Layout layout,
                             double ux, double uy) {
        for (int i = 0; i < layout.rows().size(); i++) {
            int index = layout.first() + i;
            XenoDialogue.Option option = options.get(index);
            NeonDialogueLayout.Rect r = layout.rows().get(i);
            NeonOptionKind kind = NeonOptionKind.of(option);
            boolean hovered = kind.usable() && (r.contains(ux, uy) || index == focus);
            boolean pressed = hovered && pressedRow == index;
            drawButton(g, NeonDialogueStyle.forKind(kind, hovered, pressed), r);

            Component label = BubbleText.styled(session.resolve(option.text()));
            int color = kind.usable() ? (hovered ? NeonDialogueStyle.LIME : NeonDialogueStyle.TEXT)
                    : NeonDialogueStyle.MUTED;
            int shift = 0;
            if (kind == NeonOptionKind.PRIMARY) {
                // The quest mark; the label itself is the author's text, untouched.
                g.drawString(font, "!", r.x() + 9, r.y() + (r.h() - 8) / 2,
                        NeonDialogueStyle.LIME, false);
                shift = 6;
                if (questActive(option)) {
                    String tag = "active";
                    g.drawString(font, tag, r.right() - 10 - font.width(tag),
                            r.y() + (r.h() - 8) / 2, NeonDialogueStyle.MUTED, false);
                }
            }
            drawFitted(g, label, r.x() + 12 + shift, r.y(), r.w() - 24 - shift - (shift > 0
                    && questActive(option) ? 34 : 0), r.h(), color);
        }
        // The Close row, nearest the panel. Always present: a conversation must be leavable
        // even when the dialogue shipped no quit option.
        NeonDialogueLayout.Rect c = layout.close();
        boolean closeHover = c.contains(ux, uy);
        drawButton(g, NeonDialogueStyle.forKind(NeonOptionKind.SECONDARY, closeHover,
                closeHover && pressedRow == CLOSE_ROW), c);
        drawFitted(g, Component.literal("Close"), c.x() + 12, c.y(), c.w() - 24, c.h(),
                closeHover ? NeonDialogueStyle.LIME : NeonDialogueStyle.MUTED);

        if (layout.more()) {
            g.drawString(font, (layout.first() > 0 ? "^ " : "") + "scroll",
                    layout.rows().get(0).x() + 2, layout.rows().get(0).y() - 9,
                    NeonDialogueStyle.MUTED, false);
        }
    }

    private static final int CLOSE_ROW = -2;

    private boolean questActive(XenoDialogue.Option option) {
        if (option.type() != XenoDialogue.OptionType.QUEST || option.quest() == null) {
            return false;
        }
        String id = option.quest().trim().toLowerCase(Locale.ROOT);
        for (ClientQuests.Entry entry : ClientQuests.all()) {
            if (entry.id().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // -- drawing helpers -------------------------------------------------------------------

    private static void blit(GuiGraphics g, NeonDialogueStyle.Sprite s, int x, int y, int w, int h) {
        g.blit(s.rl(), x, y, w, h, 0f, 0f, s.w(), s.h(), s.w(), s.h());
    }

    /** A button sprite at any width; the caps keep their proportions, the middle stretches. */
    private static void drawButton(GuiGraphics g, NeonDialogueStyle.Sprite s,
                                   NeonDialogueLayout.Rect r) {
        for (NeonDialogueLayout.Slice slice : NeonDialogueLayout.threeSlice(
                s.w(), s.h(), r.x(), r.y(), r.w(), r.h())) {
            g.blit(s.rl(), slice.dx(), slice.dy(), slice.dw(), slice.dh(),
                    slice.u(), slice.v(), slice.uw(), slice.vh(), s.w(), s.h());
        }
    }

    /** Draws text vertically centred in a box, scaled down (not clipped) when it is too wide. */
    private void drawFitted(GuiGraphics g, Component text, int x, int y, int w, int h, int color) {
        int width = font.width(text);
        float scale = width <= w || width == 0 ? 1.0f : Math.max(0.6f, w / (float) width);
        g.pose().pushPose();
        g.pose().translate(x, y + (h - 8 * scale) / 2f, 0);
        g.pose().scale(scale, scale, 1f);
        g.drawString(font, text, 0, 0, color, false);
        g.pose().popPose();
    }

    private static int length(FormattedCharSequence seq) {
        int[] n = {0};
        seq.accept((pos, style, cp) -> {
            n[0]++;
            return true;
        });
        return n[0];
    }

    /** The first {@code limit} characters of a wrapped line, styles intact. */
    private static FormattedCharSequence truncated(FormattedCharSequence full, int limit) {
        return sink -> {
            int[] n = {0};
            return full.accept((pos, style, cp) -> {
                if (n[0] >= limit) {
                    return false;
                }
                n[0]++;
                return sink.accept(pos, style, cp);
            });
        };
    }

    // -- input -----------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DialogueBubbleSession session = DialogueBubbleSession.active();
        if (session == null) {
            onClose();
            return true;
        }
        if (button != 0) {
            return false;
        }
        double ux = toUiX(mouseX);
        double uy = toUiY(mouseY);
        List<XenoDialogue.Option> options = session.options();
        NeonDialogueLayout.Layout layout = layout(options.size());
        for (int i = 0; i < layout.rows().size(); i++) {
            if (layout.rows().get(i).contains(ux, uy)) {
                pressedRow = layout.first() + i;
                return true;
            }
        }
        if (layout.close().contains(ux, uy)) {
            pressedRow = CLOSE_ROW;
            return true;
        }
        if (layout.panel().contains(ux, uy)) {
            skipTyping(session);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int pressed = pressedRow;
        pressedRow = -1;
        DialogueBubbleSession session = DialogueBubbleSession.active();
        if (button != 0 || pressed == -1 || session == null) {
            return false;
        }
        // Acts on release over the same button, so a press can be cancelled by dragging away.
        double ux = toUiX(mouseX);
        double uy = toUiY(mouseY);
        NeonDialogueLayout.Layout layout = layout(session.options().size());
        if (pressed == CLOSE_ROW) {
            if (layout.close().contains(ux, uy)) {
                onClose();
            }
            return true;
        }
        int slot = pressed - layout.first();
        if (slot >= 0 && slot < layout.rows().size() && layout.rows().get(slot).contains(ux, uy)) {
            choose(session, pressed);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        DialogueBubbleSession session = DialogueBubbleSession.active();
        if (session == null || scrollY == 0) {
            return false;
        }
        int count = session.options().size();
        NeonDialogueLayout.Layout layout = layout(count);
        firstVisible = NeonDialogueLayout.scrolled(layout.first(), scrollY < 0 ? 1 : -1, count,
                layout.rows().size());
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        DialogueBubbleSession session = DialogueBubbleSession.active();
        if (session == null) {
            return super.keyPressed(key, scan, modifiers);
        }
        int count = session.options().size();
        // Number keys pick options directly, the same as the bubbles allowed.
        if (key >= 49 && key <= 57 && key - 49 < count) {
            choose(session, key - 49);
            return true;
        }
        switch (key) {
            case 264 -> { // down
                moveFocus(session, 1);
                return true;
            }
            case 265 -> { // up
                moveFocus(session, -1);
                return true;
            }
            case 257, 335, 32 -> { // enter, keypad enter, space
                if (!NeonTypewriter.done(textLength(session), ageTicks)) {
                    skipTyping(session);
                } else if (count > 0) {
                    choose(session, Math.max(0, Math.min(focus, count - 1)));
                }
                return true;
            }
            default -> {
                return super.keyPressed(key, scan, modifiers);
            }
        }
    }

    private void moveFocus(DialogueBubbleSession session, int delta) {
        int count = session.options().size();
        if (count == 0) {
            return;
        }
        focus = Math.floorMod(focus + delta, count);
        NeonDialogueLayout.Layout layout = layout(count);
        firstVisible = NeonDialogueLayout.revealing(layout.first(), focus, layout.rows().size());
    }

    private int textLength(DialogueBubbleSession session) {
        NeonDialogueLayout.Layout layout = layout(session.options().size());
        int total = 0;
        for (FormattedCharSequence line : font.split(BubbleText.styled(session.text()),
                layout.text().w())) {
            total += length(line);
        }
        return total;
    }

    private void skipTyping(DialogueBubbleSession session) {
        ageTicks = Math.max(ageTicks, NeonTypewriter.ticksToFinish(textLength(session)));
    }

    /**
     * Acts on one option through the logic every input route shares.
     *
     * <p>The screen only has to close itself when that says the conversation is over; the session
     * has already ended by then, so this must not end it a second time.
     */
    private void choose(DialogueBubbleSession session, int index) {
        if (!NeonOptionKind.of(session.options().get(index)).usable()) {
            return;
        }
        if (DialogueBubbleChoice.choose(session, index) == DialogueBubbleChoice.Result.CLOSED) {
            super.onClose();
        }
    }

    @Override
    public void onClose() {
        DialogueBubbleSession.end();
        super.onClose();
    }
}
