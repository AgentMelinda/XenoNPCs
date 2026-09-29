package net.bullettrain.xenonpcs.npc.brain.v6;

public record XenoNpcDecision(XenoNpcActionKind kind, int priority, long cooldownTicks) {
}