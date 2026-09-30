package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.Tags;
import xenoapi.npcs.api.INbt;
import xenoapi.npcs.api.constants.ItemType;
import xenoapi.npcs.api.entity.IEntityLiving;
import xenoapi.npcs.api.entity.IMob;
import xenoapi.npcs.api.entity.data.IData;
import xenoapi.npcs.api.item.IItemStack;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A live item stack as XenoAPI's {@link IItemStack}; changes apply to the wrapped stack.
 * 1.20.1 version: names, lore, enchantments and attribute modifiers live in the stack's NBT tag,
 * where 1.21.1 has item components. {@link #hasNbt}/{@link #removeNbt} act on the keys that are not
 * vanilla's own (1.21.1's custom-data component), and {@link #getItemNbt} is a detached save snapshot.
 */
public final class XenoItemAdapter implements IItemStack {
    static final int MAX_LORE_LINES = 64;
    static final int MAX_ENCHANT_LEVEL = 255;

    final ItemStack stack;

    public XenoItemAdapter(ItemStack stack) {
        this.stack = Objects.requireNonNull(stack);
    }

    private static ResourceLocation id(String method, String id) {
        ResourceLocation key = id == null ? null : ResourceLocation.tryParse(id);
        if (key == null) throw new IllegalArgumentException(method + ": invalid id " + id);
        return key;
    }

    // ------------------------------------------------------------------ size / damage

    @Override public int getStackSize() { return stack.getCount(); }

    /** 1 to the maximum stack size, as the reference documents ("a number between 1 and 64"). */
    @Override
    public void setStackSize(int size) {
        stack.setCount(Math.max(1, Math.min(stack.getMaxStackSize(), size)));
    }

    @Override public int getMaxStackSize() { return stack.getMaxStackSize(); }
    @Override public boolean isDamageable() { return stack.isDamageableItem(); }
    @Override public int getDamage() { return stack.getDamageValue(); }

    @Override
    public void setDamage(int value) {
        if (!stack.isDamageableItem()) throw new IllegalArgumentException("IItemStack.setDamage: item is not damageable");
        stack.setDamageValue(Math.max(0, Math.min(stack.getMaxDamage(), value)));
    }

    @Override public int getMaxDamage() { return stack.getMaxDamage(); }

    // ------------------------------------------------------------------ enchantments

    @Override public boolean isEnchanted() { return stack.isEnchanted(); }

    @Override
    public boolean hasEnchant(String id) {
        ResourceLocation key = id("IItemStack.hasEnchant", id);
        return EnchantmentHelper.getEnchantments(stack).keySet().stream()
                .anyMatch(enchantment -> key.equals(BuiltInRegistries.ENCHANTMENT.getKey(enchantment)));
    }

    @Override
    public void addEnchantment(String id, int strength) {
        ResourceLocation key = id("IItemStack.addEnchantment", id);
        if (strength < 1 || strength > MAX_ENCHANT_LEVEL) {
            throw new IllegalArgumentException("IItemStack.addEnchantment: level must be 1-" + MAX_ENCHANT_LEVEL);
        }
        Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.getOptional(key)
                .orElseThrow(() -> new IllegalArgumentException("Unknown enchantment " + id));
        stack.enchant(enchantment, strength);
    }

    @Override
    public boolean removeEnchant(String id) {
        ResourceLocation key = id("IItemStack.removeEnchant", id);
        if (!hasEnchant(id)) return false;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.keySet().removeIf(enchantment -> key.equals(BuiltInRegistries.ENCHANTMENT.getKey(enchantment)));
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return true;
    }

    // ------------------------------------------------------------------ kind / names

    @Override public boolean isBlock() { return stack.getItem() instanceof BlockItem; }
    @Override public boolean isWearable() { return Equipable.get(stack) != null; }
    @Override public boolean isBook() { return stack.is(Items.WRITTEN_BOOK) || stack.is(Items.WRITABLE_BOOK); }
    @Override public boolean isEmpty() { return stack.isEmpty(); }

    @Override
    public int getType() {
        if (isBook()) return ItemType.BOOK;
        if (stack.is(Tags.Items.SEEDS)) return ItemType.SEEDS;
        if (stack.getItem() instanceof BlockItem) return ItemType.BLOCK;
        if (stack.getItem() instanceof ArmorItem) return ItemType.ARMOR;
        if (stack.getItem() instanceof SwordItem) return ItemType.SWORD;
        return ItemType.NORMAL;
    }

    @Override public boolean hasCustomName() { return stack.hasCustomHoverName(); }

    @Override
    public void setCustomName(String name) {
        String next = XenoApiAdapters.boundedText("IItemStack.setCustomName", name, 128);
        if (next.isEmpty()) stack.resetHoverName();
        else stack.setHoverName(Component.literal(next));
    }

    @Override public String getDisplayName() { return stack.getHoverName().getString(); }
    @Override public String getItemName() { return stack.getItem().getName(stack).getString(); }
    @Override public String getName() { return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(); }

    @Override
    public String[] getLore() {
        CompoundTag display = stack.getTagElement(ItemStack.TAG_DISPLAY);
        if (display == null || !display.contains(ItemStack.TAG_LORE, Tag.TAG_LIST)) return new String[0];
        ListTag lore = display.getList(ItemStack.TAG_LORE, Tag.TAG_STRING);
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < lore.size(); i++) {
            Component line = Component.Serializer.fromJson(lore.getString(i));
            lines.add(line == null ? "" : line.getString());
        }
        return lines.toArray(String[]::new);
    }

    @Override
    public void setLore(String[] lore) {
        if (lore == null || lore.length == 0) {
            CompoundTag display = stack.getTagElement(ItemStack.TAG_DISPLAY);
            if (display != null) {
                display.remove(ItemStack.TAG_LORE);
                if (display.isEmpty()) stack.removeTagKey(ItemStack.TAG_DISPLAY);
            }
            return;
        }
        if (lore.length > MAX_LORE_LINES) throw new IllegalArgumentException("IItemStack.setLore: at most " + MAX_LORE_LINES + " lines");
        ListTag lines = new ListTag();
        for (String line : lore) {
            lines.add(StringTag.valueOf(Component.Serializer.toJson(
                    Component.literal(XenoApiAdapters.boundedText("IItemStack.setLore", line, 256)))));
        }
        stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY).put(ItemStack.TAG_LORE, lines);
    }

    /** Nutrition of a food item; 0 for anything that is not food. */
    @Override
    public int getFoodLevel() {
        var food = stack.getItem().getFoodProperties();
        return food == null ? 0 : food.getNutrition();
    }

    // ------------------------------------------------------------------ data

    /** Tag keys vanilla 1.20.1 keeps for what 1.21.1 stores as its own components, not custom data. */
    static final Set<String> VANILLA_KEYS = Set.of(ItemStack.TAG_DAMAGE, "Unbreakable", "CanDestroy", "CanPlaceOn",
            ItemStack.TAG_DISPLAY, ItemStack.TAG_ENCH, "StoredEnchantments", "RepairCost", "HideFlags",
            "AttributeModifiers", "CustomModelData", "BlockEntityTag", "BlockStateTag", "Trim");

    @Override
    public boolean hasNbt() {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getAllKeys().stream().anyMatch(key -> !VANILLA_KEYS.contains(key));
    }

    @Override
    public void removeNbt() {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;
        for (String key : new ArrayList<>(tag.getAllKeys())) {
            if (!VANILLA_KEYS.contains(key)) tag.remove(key);
        }
        if (tag.isEmpty()) stack.setTag(null);
    }

    /** A detached snapshot of the whole saved stack; writing to it changes nothing. */
    @Override
    public INbt getItemNbt() {
        if (stack.isEmpty()) return XenoApiAdapters.wrap(new CompoundTag());
        return XenoApiAdapters.wrap(stack.save(new CompoundTag()));
    }

    // ------------------------------------------------------------------ copies / comparison

    @Override public IItemStack copy() { return new XenoItemAdapter(stack.copy()); }

    /** Splits {@code stackSize} items off this stack into a new one, as vanilla {@code split}. */
    @Override
    public IItemStack split(int stackSize) {
        if (stackSize < 1) throw new IllegalArgumentException("IItemStack.split: size must be at least 1");
        return new XenoItemAdapter(stack.split(stackSize));
    }

    @Override
    public boolean compare(IItemStack item, boolean ignoreNBT) {
        return compare(item, ignoreNBT, false);
    }

    @Override
    public boolean compare(IItemStack item, boolean ignoreNBT, boolean ignoreDamage) {
        return compare(XenoApiAdapters.unwrap(item), ignoreNBT, ignoreDamage);
    }

    /** Same item, and unless ignored the same components; {@code ignoreDamage} skips durability. */
    @Override
    public boolean compare(ItemStack item, boolean ignoreNBT, boolean ignoreDamage) {
        if (item == null) item = ItemStack.EMPTY;
        if (stack.isEmpty() || item.isEmpty()) return stack.isEmpty() && item.isEmpty();
        if (!ItemStack.isSameItem(stack, item)) return false;
        if (ignoreNBT) return ignoreDamage || stack.getDamageValue() == item.getDamageValue();
        if (!ignoreDamage) return ItemStack.isSameItemSameTags(stack, item);
        ItemStack left = stack.copy();
        ItemStack right = item.copy();
        left.removeTagKey(ItemStack.TAG_DAMAGE);
        right.removeTagKey(ItemStack.TAG_DAMAGE);
        if (left.getTag() != null && left.getTag().isEmpty()) left.setTag(null);
        if (right.getTag() != null && right.getTag().isEmpty()) right.setTag(null);
        return ItemStack.isSameItemSameTags(left, right);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof XenoItemAdapter that && that.stack == stack;
    }

    @Override public int hashCode() { return System.identityHashCode(stack); }
    @Override public String toString() { return stack.getCount() + "x " + getName(); }

    // ------------------------------------------------------------------ attributes

    static final double MAX_ATTRIBUTE = 1_000_000.0;

    /** Reference slot codes: -1 all (null: no slot), 0 main hand, 1 off hand, 2 feet, 3 legs, 4 chest, 5 head. */
    static net.minecraft.world.entity.EquipmentSlot slotGroup(int slot) {
        return switch (slot) {
            case -1 -> null;
            case 0 -> net.minecraft.world.entity.EquipmentSlot.MAINHAND;
            case 1 -> net.minecraft.world.entity.EquipmentSlot.OFFHAND;
            case 2 -> net.minecraft.world.entity.EquipmentSlot.FEET;
            case 3 -> net.minecraft.world.entity.EquipmentSlot.LEGS;
            case 4 -> net.minecraft.world.entity.EquipmentSlot.CHEST;
            case 5 -> net.minecraft.world.entity.EquipmentSlot.HEAD;
            default -> throw new IllegalArgumentException("Attribute slot must be -1..5, got " + slot);
        };
    }

    private static net.minecraft.world.entity.ai.attributes.Attribute attribute(String name) {
        ResourceLocation id = name == null ? null : ResourceLocation.tryParse(name);
        if (id == null) throw new IllegalArgumentException("Unknown attribute " + name);
        return BuiltInRegistries.ATTRIBUTE.getOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown attribute " + name));
    }

    /** The id 1.21.1 gives this modifier; 1.20.1 keys modifiers by a UUID derived from it. */
    private static String modifierName(net.minecraft.world.entity.ai.attributes.Attribute attribute, int slot) {
        String path = BuiltInRegistries.ATTRIBUTE.getKey(attribute).getPath().replace('.', '_');
        return "xenonpcs:xenoapi/" + path + "/" + (slot < 0 ? "any" : slot);
    }

    private static UUID modifierId(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    private record Entry(net.minecraft.world.entity.ai.attributes.Attribute attribute,
                         net.minecraft.world.entity.ai.attributes.AttributeModifier modifier,
                         net.minecraft.world.entity.EquipmentSlot slot) {}

    /**
     * Every modifier the stack carries, once each: its AttributeModifiers tag when it has one,
     * otherwise the item's defaults per slot (1.21.1's {@code getAttributeModifiers().modifiers()}).
     */
    private List<Entry> entries() {
        List<Entry> out = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("AttributeModifiers", Tag.TAG_LIST)) {
            ListTag list = tag.getList("AttributeModifiers", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                ResourceLocation id = ResourceLocation.tryParse(entry.getString("AttributeName"));
                var attribute = id == null ? null : BuiltInRegistries.ATTRIBUTE.get(id);
                var modifier = net.minecraft.world.entity.ai.attributes.AttributeModifier.load(entry);
                if (attribute == null || modifier == null) continue;
                var slot = entry.contains("Slot", Tag.TAG_STRING)
                        ? net.minecraft.world.entity.EquipmentSlot.byName(entry.getString("Slot")) : null;
                out.add(new Entry(attribute, modifier, slot));
            }
            return out;
        }
        for (var slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            stack.getAttributeModifiers(slot).forEach((attribute, modifier) -> out.add(new Entry(attribute, modifier, slot)));
        }
        return out;
    }

    /** Sum of main-hand ADDITION attack-damage modifiers, e.g. 5.0 for an iron sword. */
    @Override
    public double getAttackDamage() {
        double total = 0.0;
        for (var modifier : stack.getAttributeModifiers(net.minecraft.world.entity.EquipmentSlot.MAINHAND)
                .get(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)) {
            if (modifier.getOperation() == net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION) {
                total += modifier.getAmount();
            }
        }
        return total;
    }

    @Override
    public void setAttribute(String name, double value) {
        setAttribute(name, value, -1);
    }

    /** An ADDITION modifier this adapter owns for that attribute and slot; 0 removes it. */
    @Override
    public void setAttribute(String name, double value, int slot) {
        if (!Double.isFinite(value) || Math.abs(value) > MAX_ATTRIBUTE) {
            throw new IllegalArgumentException("IItemStack.setAttribute: value must be finite and within " + MAX_ATTRIBUTE);
        }
        var attribute = attribute(name);
        var group = slotGroup(slot);
        String modifierName = modifierName(attribute, slot);
        UUID id = modifierId(modifierName);
        // A written AttributeModifiers tag replaces the item's defaults, so carry them over, as 1.21.1 does.
        List<Entry> kept = new ArrayList<>();
        for (Entry entry : entries()) {
            if (!(entry.attribute() == attribute && entry.modifier().getId().equals(id))) kept.add(entry);
        }
        stack.removeTagKey("AttributeModifiers");
        for (Entry entry : kept) stack.addAttributeModifier(entry.attribute(), entry.modifier(), entry.slot());
        if (value != 0.0) {
            stack.addAttributeModifier(attribute, new net.minecraft.world.entity.ai.attributes.AttributeModifier(id,
                    modifierName, value, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION), group);
        } else if (kept.isEmpty()) {
            stack.getOrCreateTag().put("AttributeModifiers", new ListTag());
        }
    }

    /** Sum of ADDITION modifiers for that attribute across all slots. */
    @Override
    public double getAttribute(String name) {
        var attribute = attribute(name);
        double total = 0.0;
        for (Entry entry : entries()) {
            if (entry.attribute() == attribute
                    && entry.modifier().getOperation() == net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION) {
                total += entry.modifier().getAmount();
            }
        }
        return total;
    }

    @Override
    public boolean hasAttribute(String name) {
        var attribute = attribute(name);
        return entries().stream().anyMatch(entry -> entry.attribute() == attribute);
    }

    // ------------------------------------------------------------------ unsupported

    @Override
    public ItemStack getMCItemStack() {
        throw XenoApiAdapters.unsupported("IItemStack.getMCItemStack (raw handles are not exposed)");
    }

    @Override public INbt getNbt() { throw XenoApiAdapters.unsupported("IItemStack.getNbt (raw item NBT is not exposed; use getItemNbt)"); }
    /**
     * Wears the stack by {@code damage}, as use would: unbreaking applies, and a stack that breaks
     * is used up. {@code living} is who wears it; null wears it with no one to credit.
     */
    @Override
    public void damageItem(int damage, IMob living) {
        if (damage < 1 || damage > 1_000_000) throw new IllegalArgumentException("IItemStack.damageItem: damage must be 1-1000000");
        var holder = XenoApiAdapters.unwrapLiving(living);
        var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        net.minecraft.server.level.ServerLevel level = holder != null && holder.level() instanceof net.minecraft.server.level.ServerLevel own
                ? own : server == null ? null : server.overworld();
        if (level == null) throw new IllegalStateException("IItemStack.damageItem needs a running server");
        XenoApiAdapters.requireServerThread(level);
        if (!stack.isDamageableItem()) return;
        if (holder != null) {
            stack.hurtAndBreak(damage, holder, item -> { });
        } else if (stack.hurt(damage, level.random, null)) {
            stack.shrink(1);
            stack.setDamageValue(0);
        }
    }
    private static final java.util.Map<ItemStack, java.util.Map<String, Object>> TEMP =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());
    private static final String STORED_DATA = "XenoScriptData";

    /** Per stack instance (ItemStack keeps identity equality), gone when the stack is collected. */
    @Override
    public IData getTempdata() {
        return XenoDataAdapter.ofView(() -> XenoBoundedData.temp(
                TEMP.computeIfAbsent(stack, ignored -> new java.util.HashMap<>())));
    }

    /** Strings and numbers in the custom-data component under {@code XenoScriptData}. */
    @Override
    public IData getStoreddata() {
        return XenoDataAdapter.ofView(() -> XenoBoundedData.stored(
                () -> net.bullettrain.xenonpcs.compat120.CustomData.of(stack)
                        .copyTag().getCompound(STORED_DATA),
                tag -> net.bullettrain.xenonpcs.compat120.CustomData.update(stack,
                        root -> root.put(STORED_DATA, tag))));
    }
    /**
     * Uses this stack as a right-click in the air by a player, from the chosen hand. The stack must
     * be the one in that hand; only players use items this way.
     */
    @Override
    public void use(IEntityLiving entity, boolean isMainHand) {
        if (!(XenoApiAdapters.unwrap(entity) instanceof net.minecraft.server.level.ServerPlayer player)) {
            throw new IllegalArgumentException("IItemStack.use: only a player can use an item");
        }
        var hand = isMainHand ? net.minecraft.world.InteractionHand.MAIN_HAND : net.minecraft.world.InteractionHand.OFF_HAND;
        if (player.getItemInHand(hand) != stack) {
            throw new IllegalArgumentException("IItemStack.use: the item must be the one in that hand");
        }
        XenoApiAdapters.requireServerThread(player.level());
        player.gameMode.useItem(player, player.level(), stack, hand);
    }
}
