package xenoapi.npcs.api.event;

import net.minecraftforge.eventbus.api.Event;
import xenoapi.npcs.api.NpcAPI;

public class CustomNPCsEvent extends Event {
	public final NpcAPI API = NpcAPI.Instance();
}
