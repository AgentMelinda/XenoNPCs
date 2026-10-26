package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import xenoapi.npcs.api.item.IItemBook;
import static org.junit.jupiter.api.Assertions.*;

class XenoSpecializedItemsTest {
    @BeforeAll static void bootstrap() { net.minecraft.SharedConstants.tryDetectVersion(); net.minecraft.server.Bootstrap.bootStrap(); }
    @Test void metadataEditsKeepFiltersAndGenerationUntilPagesAreReplaced() {
        var stack = new ItemStack(Items.WRITTEN_BOOK);
        var tag = stack.getOrCreateTag(); tag.putInt("generation", 2); tag.putString("filtered_title", "safe title");
        var filtered = new CompoundTag(); filtered.putString("0", "filtered page"); tag.put("filtered_pages", filtered);
        var pages = new ListTag(); pages.add(StringTag.valueOf("{\"text\":\"original\"}")); tag.put("pages", pages);
        var book = (IItemBook) XenoApiAdapters.wrap(stack); book.setAuthor("Vegeta");
        assertEquals("safe title", tag.getString("filtered_title")); assertSame(pages, tag.get("pages"));
        assertEquals(2, tag.getInt("generation"));
        book.setText(new String[]{"literal"}); assertTrue(tag.getBoolean("resolved"));
        assertFalse(tag.contains("filtered_pages")); assertEquals(2, tag.getInt("generation"));
    }
    @Test void invalidMetadataIsAtomic() {
        var stack = new ItemStack(Items.WRITTEN_BOOK); var book = (IItemBook) XenoApiAdapters.wrap(stack);
        book.setText(new String[]{"keep"}); var before = stack.getTag().copy();
        assertThrows(IllegalArgumentException.class, () -> book.setTitle("x".repeat(33)));
        assertThrows(IllegalArgumentException.class, () -> book.setAuthor("x".repeat(129)));
        assertThrows(IllegalArgumentException.class, () -> book.setText(new String[]{"changed", null}));
        assertEquals(before, stack.getTag());
    }
}
