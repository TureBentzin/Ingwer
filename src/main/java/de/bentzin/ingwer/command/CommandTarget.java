package de.bentzin.ingwer.command;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum CommandTarget {

    INGAME(true),
    SERVER_CONSOLE(true),
    INGWER_CONSOLE(),
    REMOTE(),
    COMMAND_BLOCK(true),

    LOCAL(INGAME,SERVER_CONSOLE,COMMAND_BLOCK,INGWER_CONSOLE),
    SAVE(INGAME,REMOTE,INGWER_CONSOLE)

    ;
    private CommandTarget[] commandTargets;
    private boolean comesWithPrefix;

    public boolean comesWithPrefix() {
        return comesWithPrefix;
    }

    public boolean isLast() {
        return getFullfilled() == null;
    }

    public CommandTarget[] getFullfilled() {
        return commandTargets;
    }

    public CommandTarget @NotNull [] fullfill() {
        Set<CommandTarget> set = new HashSet<>();
        if(isLast()) set.add(this); else
            for (CommandTarget commandTarget : getFullfilled())
                set.addAll(List.of(commandTarget.fullfill()));
        return set.toArray(new CommandTarget[0]);
    }

    CommandTarget(CommandTarget... commandTargets) {
        this.commandTargets = commandTargets;
        comesWithPrefix = false;
    }
    CommandTarget(boolean comesWithPrefix) {
        this.commandTargets = null;
        this.comesWithPrefix = comesWithPrefix;
    }

}
