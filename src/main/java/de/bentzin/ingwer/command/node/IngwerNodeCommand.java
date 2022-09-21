package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import org.jetbrains.annotations.NotNull;

public class IngwerNodeCommand extends IngwerCommand {
    private final CommandNode commandNode;

    private final CommandTarget[] commandTargets;

    public IngwerNodeCommand(@NotNull CommandTarget @NotNull[] commandTargets, @NotNull CommandNode commandNode) {
        super(commandNode.getCommandName(), commandNode.getDescription());
        this.commandNode = commandNode;
        this.commandTargets = commandTargets;
    }

    public CommandNode getCommandNode() {
        return commandNode;
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {


    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return commandTargets;
    }
}
