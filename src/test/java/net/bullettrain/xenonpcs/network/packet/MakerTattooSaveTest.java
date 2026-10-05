package net.bullettrain.xenonpcs.network.packet;
import net.bullettrain.xenonpcs.compat.npc.NpcDmzAppearance;
import net.bullettrain.xenonpcs.features.taotto.TaottoDocument;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class MakerTattooSaveTest {
    private CompoundTag saved(TaottoDocument doc) { var tag = new CompoundTag(); doc.saveNbt(tag); return tag; }
    private CompoundTag payload(CompoundTag tattoo) {
        var appearance = new NpcDmzAppearance().toTag(); appearance.put("Taotto", tattoo);
        var payload = new CompoundTag(); payload.put("DmzAppearance", appearance); return payload;
    }
    @Test void validCanvasSurvivesServerMerge() {
        var doc = TaottoDocument.blank(); doc.setPixel(2,3,0xFF123456);
        var incoming = payload(saved(doc));
        assertTrue(XenoNpcSavePolicy.validate(incoming).accepted());
        var merged = XenoNpcSavePolicy.merge(new CompoundTag(), incoming);
        assertEquals(0xFF123456, NpcDmzAppearance.fromTag(merged.getCompound("DmzAppearance")).taotto.pixel(2,3));
    }
    @Test void corruptAndOversizedCanvasesAreRefused() {
        var tattoo = saved(TaottoDocument.blank()); tattoo.putInt("Size", 65);
        assertFalse(XenoNpcSavePolicy.validate(payload(tattoo)).accepted());
        tattoo = saved(TaottoDocument.blank()); tattoo.putIntArray("Pixels", new int[3]);
        assertFalse(XenoNpcSavePolicy.validate(payload(tattoo)).accepted());
    }
    @Test void nonFinitePlacementIsRefused() {
        for (String key : new String[]{"U", "V", "Scale"}) {
            var tattoo = saved(TaottoDocument.blank()); tattoo.putFloat(key, Float.NaN);
            assertFalse(XenoNpcSavePolicy.validate(payload(tattoo)).accepted(), key);
        }
    }
}
