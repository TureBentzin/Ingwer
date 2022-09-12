package de.bentzin.ingwer.utils.cmdreturn;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CommandReturn(String command, Runnable actionToPerform, UUID owner) {

    public void run() {
        actionToPerform.run();
    }

    @Override
    public @NotNull String toString() {
        return this.getClass().getSimpleName() + "#" + command;
    }
}
