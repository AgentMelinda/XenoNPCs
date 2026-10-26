package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xenoapi.npcs.api.item.IItemBook;

/** Forge 1.20.1 book NBT counterpart of the component-based NeoForge adapter. */
public final class XenoBookItemAdapter extends XenoItemAdapter implements IItemBook {
    public XenoBookItemAdapter(ItemStack stack) {
        super(stack);
        if (!isBook()) throw new IllegalArgumentException("IItemBook requires a book");
    }
    @Override public String[] getText() {
        ListTag pages = stack.getOrCreateTag().getList("pages", Tag.TAG_STRING);
        String[] result = new String[pages.size()];
        for (int i = 0; i < result.length; i++) {
            String raw = pages.getString(i);
            result[i] = raw;
            if (stack.is(Items.WRITTEN_BOOK)) {
                try { var parsed = Component.Serializer.fromJson(raw); if (parsed != null) result[i] = parsed.getString(); }
                catch (RuntimeException ignored) { /* preserve malformed legacy text */ }
            }
        }
        return result;
    }
    @Override public void setText(String[] pages) {
        if (pages == null || pages.length > 100) throw new IllegalArgumentException("At most 100 pages required");
        ListTag replacement = new ListTag();
        for (String page : pages) {
            if (page == null || page.length() > 1024) throw new IllegalArgumentException("Page exceeds 1024 characters");
            replacement.add(StringTag.valueOf(stack.is(Items.WRITTEN_BOOK)
                    ? Component.Serializer.toJson(Component.literal(page)) : page));
        }
        stack.getOrCreateTag().put("pages", replacement);
        if (stack.is(Items.WRITTEN_BOOK)) {
            stack.getOrCreateTag().putBoolean("resolved", true);
            stack.getOrCreateTag().remove("filtered_pages");
        }
    }
    private void requireWritten() {
        if (!stack.is(Items.WRITTEN_BOOK)) throw new IllegalArgumentException("Requires a written book");
    }
    @Override public String getAuthor() { return stack.is(Items.WRITTEN_BOOK) ? stack.getOrCreateTag().getString("author") : ""; }
    @Override public String getTitle() { return stack.is(Items.WRITTEN_BOOK) ? stack.getOrCreateTag().getString("title") : ""; }
    @Override public void setAuthor(String author) {
        requireWritten();
        if (author == null || author.length() > 128) throw new IllegalArgumentException("Author exceeds 128 characters");
        stack.getOrCreateTag().putString("author", author);
    }
    @Override public void setTitle(String title) {
        requireWritten();
        if (title == null || title.length() > 32) throw new IllegalArgumentException("Title exceeds 32 characters");
        stack.getOrCreateTag().putString("title", title);
        stack.getOrCreateTag().remove("filtered_title");
    }
}
