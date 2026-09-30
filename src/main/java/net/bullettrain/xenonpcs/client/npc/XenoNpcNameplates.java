package net.bullettrain.xenonpcs.client.npc;

import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.client.compat.npc.NpcFullDmzRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.client.event.RenderNameTagEvent;

/**
 * Hides the name tag of the stand-in player a FULL DMZ Xeno NPC is drawn through. The player
 * renderer put that name at player height, inside tall DMZ hair and without the title;
 * {@code XenoNpcRenderer} draws the NPC's own nameplate instead.
 */
@EventBusSubscriber(modid = XenoNpcsMod.MOD_ID, value = Dist.CLIENT)
public final class XenoNpcNameplates {
    private XenoNpcNameplates() {}

    @SubscribeEvent
    public static void onNameTag(RenderNameTagEvent event) {
        if (NpcFullDmzRenderer.isXenoNpcProxy(event.getEntity())) event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
    }
}
