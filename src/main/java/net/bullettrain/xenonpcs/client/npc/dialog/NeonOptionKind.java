package net.bullettrain.xenonpcs.client.npc.dialog;

import net.bullettrain.xenonpcs.npc.dialog.XenoDialogue;

/**
 * Which kit button an option is drawn with.
 *
 * <p>The sprite is a promise about what the option does, so it follows the option's type rather
 * than its position: a quest offer is the primary (lime) button, closing is the secondary one,
 * everything else is a plain choice row, and anything that cannot act yet is the disabled row.
 */
public enum NeonOptionKind {
    CHOICE,
    PRIMARY,
    SECONDARY,
    DISABLED;

    public static NeonOptionKind of(XenoDialogue.Option option) {
        if (option == null) {
            return DISABLED;
        }
        return switch (option.type()) {
            // A quest option needs an id to offer; one naming none could only report that, which
            // is not worth a click (the older screen drew the same rule).
            case QUEST, DMZ_QUEST -> option.quest() == null || option.quest().isBlank() ? DISABLED : PRIMARY;
            // No economy roles exist yet: shown disabled rather than pretending to work.
            case ROLE -> DISABLED;
            case QUIT -> SECONDARY;
            case TEXT, COMMAND, TRANSPORT -> CHOICE;
        };
    }

    public boolean usable() {
        return this != DISABLED;
    }
}
