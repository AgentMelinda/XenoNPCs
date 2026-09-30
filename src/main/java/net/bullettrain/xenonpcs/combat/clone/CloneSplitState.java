package net.bullettrain.xenonpcs.combat.clone;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Server-owned membership: recalling bodies remain owned until arrival or loss. */
final class CloneSplitState<T> {
    private Set<T> members;
    private int bodies;
    private boolean recalling;

    CloneSplitState(Collection<T> copies) {  }

    int bodies() { return 0; }

    List<T> members() { return java.util.List.of(); }

    boolean contains(T member) { return false; }

    boolean isEmpty() { return false; }

    boolean isRecalling() { return false; }

    boolean beginRecall() { return false; }

    boolean remove(T member) { return false; }

    /** Consumes membership before the caller restores health, preventing duplicate refunds. */
    float arrive(T member, float survivingHealth) { return 0.0f; }

    static float healthShare(float currentHealth, int bodies) { return 0.0f; }

    static float reunitedHealth(float ownerHealth, float maxHealth, float returnedHealth) { return 0.0f; }
}
