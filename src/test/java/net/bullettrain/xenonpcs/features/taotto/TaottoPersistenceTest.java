package net.bullettrain.xenonpcs.features.taotto;
import net.bullettrain.xenonpcs.compat.npc.NpcDmzAppearance;
import net.bullettrain.xenonpcs.capability.XenoPlayerData;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TaottoPersistenceTest {
    @Test void legacyNpcLoadsEmptyAndPresetStaysSeparate() {
        CompoundTag old = new CompoundTag(); old.putInt("Schema", 4); old.putInt("TattooType", 7);
        var appearance = NpcDmzAppearance.fromTag(old);
        assertFalse(appearance.taotto.hasPaint());
        appearance.taotto.setPixel(3, 4, 0xFF123456);
        var copy = NpcDmzAppearance.fromTag(appearance.toTag());
        assertEquals(7, copy.tattooType); assertEquals(0xFF123456, copy.taotto.pixel(3,4));
        copy.taotto.clear(); assertTrue(appearance.taotto.hasPaint());
    }
    @Test void playerSaveReloadPreservesPaintAndOldSaveLoadsEmpty() {
        var source = new XenoPlayerData(); source.taotto().setPixel(2,3,0xFF00FF00);
        var tag = new CompoundTag(); source.saveNBT(tag);
        var target = new XenoPlayerData(); target.loadNBT(tag);
        assertEquals(0xFF00FF00, target.taotto().pixel(2,3));
        target.loadNBT(new CompoundTag()); assertFalse(target.taotto().hasPaint());
    }
    @Test void playerDeathCopyOwnsIndependentOverlay() {
        var source = new XenoPlayerData(); var doc = TaottoDocument.blank(); doc.setPixel(1,2,0xFFFF0000);
        source.taotto(doc); doc.clear(); assertTrue(source.taotto().hasPaint());
        var target = new XenoPlayerData(); target.copyFrom(source);
        target.taotto().clear(); assertTrue(source.taotto().hasPaint());
    }
}
