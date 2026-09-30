package net.bullettrain.xenonpcs.api.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a defender's Zanzoken window catches an incoming blow. Posted on
 * {@code MinecraftForge.EVENT_BUS}, server side only.
 */
public abstract class ZanzokenEvent extends Event {

	private final ServerPlayer defender;

	protected ZanzokenEvent(ServerPlayer defender) {
		this.defender = defender;
	}

	public ServerPlayer getDefender() {
		return defender;
	}

	/**
	 * Fired when a dodge is about to succeed, before the window is consumed and before the damage
	 * is zeroed.
	 *
	 * <p>Cancelling makes the dodge fail: the blow lands for its full damage and the window is left
	 * armed, so a later blow in the same window can still be dodged. Note this resolves ahead of
	 * guarding, so a cancelled dodge can still be blocked normally.
	 */
	@net.minecraftforge.eventbus.api.Cancelable
	public static class Dodge extends ZanzokenEvent {

		private final LivingEntity attacker;
		private final float damage;

		public Dodge(ServerPlayer defender, LivingEntity attacker, float damage) {
			super(defender);
			this.attacker = attacker;
			this.damage = damage;
		}

		/** The attacker whose blow is being dodged. Never null - a dodge needs a living attacker. */
		public LivingEntity getAttacker() {
			return attacker;
		}

		/** The damage that will be negated if the dodge is allowed to stand. */
		public float getDamage() {
			return damage;
		}
	}
}
