package de.bentzin.ingwer.message;

import org.jetbrains.annotations.NotNull;

public class PrefixedStraightLineStringMessage extends StraightLineStringMessage {

    public PrefixedStraightLineStringMessage(@NotNull String message) {
        super(prefix(message));
    }

    public static @NotNull String prefix(String message) {
        return IngwerMessage.deserialize(IngwerMessage.mm(IngwerMessage.INGWER + message));
    }
}
