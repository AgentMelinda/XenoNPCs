package xenoapi.npcs.api.event;

import net.minecraft.world.entity.player.Player;
import xenoapi.npcs.api.NpcAPI;
import xenoapi.npcs.api.entity.ICustomNpc;
import xenoapi.npcs.api.entity.IPlayer;
import xenoapi.npcs.api.handler.data.IDialog;
import xenoapi.npcs.api.handler.data.IDialogOption;

public class DialogEvent extends NpcEvent {
	public final IDialog dialog;
	public final IPlayer player;
	
	public DialogEvent(ICustomNpc npc, Player player, IDialog dialog) {
		super(npc);
		this.dialog = dialog;
		this.player = (IPlayer) NpcAPI.Instance().getIEntity(player);
	}

	/**
	 * dialog
	 */
	    @net.minecraftforge.eventbus.api.Cancelable
	    public static class OpenEvent extends DialogEvent {
		public OpenEvent(ICustomNpc npc, Player player, IDialog dialog) {
			super(npc, player, dialog);
		}
    	
    }

	/**
	 * dialogClose
	 */
    public static class CloseEvent extends DialogEvent {
		public CloseEvent(ICustomNpc npc, Player player, IDialog dialog) {
			super(npc, player, dialog);
		}
    	
    }

	/**
	 * dialogOption
	 */
	    @net.minecraftforge.eventbus.api.Cancelable
	    public static class OptionEvent extends DialogEvent {
    	public final IDialogOption option;
		public OptionEvent(ICustomNpc npc, Player player, IDialog dialog, IDialogOption option) {
			super(npc, player, dialog);
			this.option = option;
		}
    	
    }
}
