package xenoapi.npcs.api.event;

import net.minecraftforge.eventbus.api.Event;
import xenoapi.npcs.api.IWorld;
import xenoapi.npcs.api.entity.IEntity;

/**
 * Called for most NeoForge events. For the events I use the NeoForge name and make the first letter lowercase. <br>
 * Eg: <br>
 * - EntityEvent.EntityJoinLevelEvent becomes entityEventEntityJoinLevelEvent <br>
 * - PlayerEvent.StartTracking becomes playerEventStartTracking <br>
 * - etc <br>
 *
 * Note that these events can change anytime and that I have no control over these. Use at own risk
 *
 */
@net.minecraftforge.eventbus.api.Cancelable
public class ForgeEvent extends CustomNPCsEvent {
	public final Event event;
	public ForgeEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return true when the wrapped NeoForge event can be canceled
	 */
	public boolean isCancelable() {
		return event.isCancelable();
	}
	@Override
	public boolean isCanceled() {
		return event.isCancelable() && event.isCanceled();
	}
	@Override
	public void setCanceled(boolean cancel) {
		if(event.isCancelable()) {
			event.setCanceled(cancel);
		}
		else if(cancel) {
			throw new UnsupportedOperationException("Attempted to cancel an uncancelable event: " + event.getClass().getName());
		}
	}

	/**
	 * init <br>
	 * The init event has no NeoForge event
	 */
	public static class InitEvent extends ForgeEvent {
		public InitEvent() {
			super(new NoForgeEvent());
		}
	}

	/**
	 * Placeholder for events which have no NeoForge counterpart, NeoForge's Event is abstract
	 */
	private static final class NoForgeEvent extends Event {
	}

	/**
	 * This event is used for every NeoForge event which extends net.minecraftforge.event.entity.EntityEvent <br>
	 * (this includes LivingEvent and PlayerEvent)
	 */
	@net.minecraftforge.eventbus.api.Cancelable
	public static class EntityEvent extends ForgeEvent {
		public final IEntity entity;

		public EntityEvent(net.minecraftforge.event.entity.EntityEvent event, IEntity entity) {
			super(event);
			this.entity = entity;
		}

	}

	/**
	 * This event is used for every NeoForge event which extends net.minecraftforge.event.level.LevelEvent
	 */
	@net.minecraftforge.eventbus.api.Cancelable
	public static class LevelEvent extends ForgeEvent {
		public final IWorld world;

		public LevelEvent(net.minecraftforge.event.level.LevelEvent event, IWorld world) {
			super(event);
			this.world = world;
		}

	}
}
