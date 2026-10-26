package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import xenoapi.npcs.api.item.*;
import static org.junit.jupiter.api.Assertions.*;

/** NBT-backed Forge counterpart of the NeoForge component tests. */
class XenoSpecializedItemTest {
    @BeforeAll static void bootstrap() { net.minecraft.SharedConstants.tryDetectVersion(); net.minecraft.server.Bootstrap.bootStrap(); }
    @Test void signedBookChangesPreserveGenerationAndOtherMetadata() {
        var stack = new ItemStack(Items.WRITTEN_BOOK);
        stack.getOrCreateTag().putInt("generation", 2);
        stack.getOrCreateTag().putString("title", "Original");
        stack.getOrCreateTag().putString("author", "Goku");
        ListTag pages = new ListTag(); pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Original page"))));
        stack.getOrCreateTag().put("pages", pages);
        var book = (IItemBook) XenoApiAdapters.wrap(stack);
        book.setAuthor("Piccolo");
        assertEquals(2, stack.getTag().getInt("generation"));
        assertArrayEquals(new String[]{"Original page"}, book.getText());
        book.setTitle("Training"); book.setText(new String[]{"First", "{\"text\":\"literal\"}"});
        assertEquals("Piccolo", book.getAuthor()); assertEquals("Training", book.getTitle());
        assertEquals("{\"text\":\"literal\"}", book.getText()[1]);
        var copy = (IItemBook) book.copy(); copy.setTitle("Copied"); assertEquals("Training", book.getTitle());
    }
    @Test void invalidBookChangesDoNotCommitPartOfTheRequest() {
        var stack = new ItemStack(Items.WRITABLE_BOOK);
        var book = (IItemBook) XenoApiAdapters.wrap(stack); book.setText(new String[]{"Keep"});
        var original = stack.getTag().copy();
        assertThrows(IllegalArgumentException.class, () -> book.setText(new String[]{"Changed", null}));
        assertThrows(IllegalArgumentException.class, () -> book.setText(new String[]{"x".repeat(1025)}));
        assertThrows(IllegalArgumentException.class, () -> book.setText(new String[101]));
        assertThrows(IllegalArgumentException.class, () -> book.setAuthor("Goku"));
        assertEquals(original, stack.getTag());
    }
    @Test void specializationsSurviveCopyAndSplitAndScriptInvocation() {
        var armor = (IItemArmor) XenoApiAdapters.wrap(new ItemStack(Items.IRON_HELMET));
        assertInstanceOf(IItemArmor.class, armor.copy());
        var block = (IItemBlock) XenoApiAdapters.wrap(new ItemStack(Items.STONE, 4));
        assertInstanceOf(IItemBlock.class, block.split(2)); assertEquals(2, block.getStackSize());
        var book = XenoApiAdapters.wrap(new ItemStack(Items.WRITTEN_BOOK));
        var result = net.bullettrain.xenonpcs.npc.script.NpcScriptEngines.current().run(
                "function run(){book.setText(['BT3']);book.getNbt().setInteger('power',9);return book.getText()[0]+':'+book.getNbt().getInteger('power');}",
                net.bullettrain.xenonpcs.npc.script.NpcScriptScope.builder().put("book", book).build(), "run");
        assertTrue(result.ok(), result.describe()); assertEquals("BT3:9", result.value());
    }
}
