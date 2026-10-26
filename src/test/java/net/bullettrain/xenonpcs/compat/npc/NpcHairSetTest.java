package net.bullettrain.xenonpcs.compat.npc;

import com.dragonminez.common.hair.CustomHair;
import com.dragonminez.common.hair.HairCodec;
import com.dragonminez.common.hair.HairStyleSlot;
import org.junit.jupiter.api.Test;
import java.util.EnumMap;
import static org.junit.jupiter.api.Assertions.*;

class NpcHairSetTest {
    @Test
    void dmz22FullSetPreservesAllFiveSlotsAndOwnedCopies() {
        EnumMap<HairStyleSlot, CustomHair> styles = new EnumMap<>(HairStyleSlot.class);
        for (HairStyleSlot slot : HairStyleSlot.values()) {
            CustomHair hair = new CustomHair();
            hair.setName(slot.name());
            styles.put(slot, hair);
        }
        String code = HairCodec.toFullSetCode(styles);
        assertTrue(NpcHairSet.validCode(code));
        CustomHair[] resolved = NpcHairSet.parse(code);
        assertNotNull(resolved);
        assertEquals(5, resolved.length);
        for (HairStyleSlot slot : HairStyleSlot.values()) {
            assertEquals(slot.name(), resolved[slot.index()].getName());
            CustomHair owned = NpcHairSet.slot(resolved, slot.index());
            assertNotSame(resolved[slot.index()], owned);
            owned.setName("changed");
            assertEquals(slot.name(), resolved[slot.index()].getName());
        }
    }

    @Test
    void singleStyleAppliesAcrossExistingAndSsj4Slots() {
        CustomHair hair = new CustomHair();
        hair.setName("single");
        CustomHair[] resolved = NpcHairSet.parse(HairCodec.toCode(hair));
        assertNotNull(resolved);
        assertEquals(5, resolved.length);
        for (CustomHair style : resolved) assertEquals("single", style.getName());
        assertFalse(NpcHairSet.validCode("DMZF22:invalid!"));
        assertNull(NpcHairSet.parse("DMZF22:invalid!"));
    }
}
