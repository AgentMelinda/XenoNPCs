package net.bullettrain.xenonpcs.features.party;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/** Single optional provider registry for future quest/CustomNPC integration. */
public final class PartyObjectives {
    private static volatile PartyObjectiveProvider provider;

    private PartyObjectives() {  }

    public static void register(PartyObjectiveProvider value) { }

    public static void clear(PartyObjectiveProvider value) { }

    public static PartyObjectiveSnapshot snapshot(ServerPlayer viewer, UUID partyId) { return null; }

    public static boolean start(ServerPlayer leader, UUID partyId, String objectiveId) { return false; }
}
