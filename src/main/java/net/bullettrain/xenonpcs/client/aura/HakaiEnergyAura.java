package net.bullettrain.xenonpcs.client.aura;

import com.dragonminez.common.stats.StatsData;
import net.bullettrain.xenonpcs.api.dmz.DmzAccess;
import net.minecraft.world.entity.Entity;

/** DMZ's body-following Surge/Sparking border, with no gameplay state changes. */
public final class HakaiEnergyAura {
    private HakaiEnergyAura() {}

    public static boolean active(Entity player) {
        if (player == null || !SecondAuraClientState.on(player.getId())) return false;
        return active(player, DmzAccess.stats(player).orElse(null));
    }

    static boolean active(Entity player, StatsData stats) {
        var character = stats == null ? null : stats.getCharacter();
        return player != null && character != null && HakaiEnergyAuraPolicy.active(
                SecondAuraClientState.on(player.getId()), character.hasActiveForm(), character.getActiveFormGroup());
    }

    public static float[] colour(Entity player, StatsData stats, float[] original) {
        if (!active(player, stats) || player.isInvisible() || !player.isAlive()) return original;
        if (player instanceof net.minecraft.world.entity.player.Player p && p.isSpectator()) return original;
        var form = stats.getCharacter().getActiveFormData();
        float[] colour = form == null ? null : form.getRgbAuraColor();
        if (colour == null || colour.length < 3) colour = stats.getCharacter().getRgbAuraColor();
        return colour == null || colour.length < 3 ? original : colour.clone();
    }
}
