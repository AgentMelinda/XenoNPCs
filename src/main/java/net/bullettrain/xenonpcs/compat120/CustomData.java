package net.bullettrain.xenonpcs.compat120;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * The part of 1.21's {@code net.minecraft.world.item.component.CustomData} XenoNPCs uses, over a
 * 1.20.1 item's NBT tag. The keys XenoNPCs writes (bound NPC ids, payloads) are its own, so they sit
 * in the tag next to vanilla's display and enchantment data without colliding.
 */
public final class CustomData {
    public static final CustomData EMPTY = new CustomData(new CompoundTag());

    private final CompoundTag tag;

    private CustomData(CompoundTag tag) {
        this.tag = tag;
    }

    /** {@code stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)} on 1.21. */
    public static CustomData of(ItemStack stack) {
        CompoundTag tag = stack == null ? null : stack.getTag();
        return tag == null ? EMPTY : new CustomData(tag);
    }

    /** {@code CustomData.update(DataComponents.CUSTOM_DATA, stack, edit)} on 1.21. */
    public static void update(ItemStack stack, Consumer<CompoundTag> edit) {
        CompoundTag tag = stack.getOrCreateTag();
        edit.accept(tag);
        if (tag.isEmpty()) stack.setTag(null);
    }

    public CompoundTag copyTag() { return tag.copy(); }
    public boolean contains(String key) { return tag.contains(key); }
    public boolean isEmpty() { return tag.isEmpty(); }
    /** Read-only use only, as on 1.21. */
    public CompoundTag getUnsafe() { return tag; }
}
