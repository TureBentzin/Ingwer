package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.CommandData;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

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
    public void execute(IngwerCommandSender v1, String[] v2, CommandTarget v3) {
        Node node = commandNode.startWalking(v1, v2, v3);
        getLogger().info("finished at: " + node.getName());
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return commandTargets;
    }
}
