package net.bullettrain.xenonpcs.client.npc;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

class NpcTextureCatalogTest {
    @Test void geoPathsBecomeEditorModelIdsAcrossAnyNamespace() {
        var id = NpcTextureCatalog.geoToModelId(
                new ResourceLocation("coolmodels", "geo/entity/saiyan/base.geo.json"));
        assertEquals("coolmodels", id.getNamespace());
        assertEquals("entity/saiyan/base", id.getPath());
        assertEquals("Entity", NpcTextureCatalog.category(id));
    }

    @Test void textureEntityPathsStayEntityCategory() {
        assertEquals("Entity", NpcTextureCatalog.category(
                new ResourceLocation("dragonminez", "textures/entity/sagas/foo.png")));
    }
}
