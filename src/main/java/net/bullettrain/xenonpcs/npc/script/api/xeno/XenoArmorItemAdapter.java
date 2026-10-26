package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import xenoapi.npcs.api.item.IItemArmor;

/** Armor specialization backed by the item's actual equipment slot and registered material. */
public final class XenoArmorItemAdapter extends XenoItemAdapter implements IItemArmor {
    public XenoArmorItemAdapter(ItemStack stack) {
        super(stack);
        if (!(stack.getItem() instanceof ArmorItem)) throw new IllegalArgumentException("IItemArmor requires an armor item");
    }

    @Override public int getArmorSlot() { return getSlotType().getIndex(); }
    @Override public EquipmentSlot getSlotType() { return ((ArmorItem) stack.getItem()).getEquipmentSlot(); }
    @Override public String getArmorMaterial() {
        return ((ArmorItem) stack.getItem()).getMaterial().getName();
    }
}
