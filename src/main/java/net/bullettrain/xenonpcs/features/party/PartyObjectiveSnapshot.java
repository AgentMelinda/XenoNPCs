package net.bullettrain.xenonpcs.features.party;

/** Wire-safe party objective presented by an optional future quest/NPC provider. */
public record PartyObjectiveSnapshot(
        String sourceId,
        String objectiveId,
        String title,
        int progress,
        int goal,
        String state,
        boolean canStart
) {
    public static PartyObjectiveSnapshot EMPTY = null;

    public boolean present() { return false; }
}
