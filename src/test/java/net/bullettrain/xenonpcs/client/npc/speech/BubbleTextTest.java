package net.bullettrain.xenonpcs.client.npc.speech;

import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BubbleTextTest {
    @Test
    void legacyCodesBecomeStyledSpansInsteadOfVisibleGlyphs() {
        var text = BubbleText.styled("§aHello§r world");
        assertEquals("Hello world", text.getString());
        assertEquals(ChatFormatting.GREEN.getColor(),
                text.getSiblings().getFirst().getStyle().getColor().getValue());
    }
}
