package de.bentzin.ingwer.command;

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
        return getFulfilled() == null;
    }

    public CommandTarget[] getFulfilled() {
        return commandTargets;
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
