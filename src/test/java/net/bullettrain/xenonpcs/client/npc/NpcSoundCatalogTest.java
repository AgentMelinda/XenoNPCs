package net.bullettrain.xenonpcs.client.npc;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NpcSoundCatalogTest {
    @Test
    void sortsAndDeduplicatesRegistryIds() {
        assertEquals(List.of("minecraft:ambient.cave", "minecraft:entity.zombie.hurt",
                        "xenonpcs:npc.power_up"),
                NpcSoundCatalog.sortedIds(List.of(
                        new ResourceLocation("xenonpcs:npc.power_up"),
                        new ResourceLocation("minecraft:entity.zombie.hurt"),
                        new ResourceLocation("minecraft:ambient.cave"),
                        new ResourceLocation("minecraft:entity.zombie.hurt"))));
    }
}
