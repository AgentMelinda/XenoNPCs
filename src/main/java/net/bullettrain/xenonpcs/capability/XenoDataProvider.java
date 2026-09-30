package net.bullettrain.xenonpcs.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/** Serializable value stored by the Xeno player data capability (1.20.1: no registry provider). */
public final class XenoDataProvider implements INBTSerializable<CompoundTag> {
    private final XenoPlayerData data = new XenoPlayerData();

    public XenoPlayerData data() {
        return data;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        data.saveNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        data.loadNBT(nbt);
    }
}
