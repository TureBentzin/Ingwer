package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.CommandData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * used for node based commands
 */
public class IngwerNodeCommand extends IngwerCommand {
    private final CommandNode commandNode;

    private final CommandTarget[] commandTargets;

    /**
     * @implNote Only used if there is a custom implemented commandNode
     * @param commandTargets targets
     * @param commandNode the custom node
     * @see IngwerNodeCommand#IngwerNodeCommand(CommandTarget[], String, String, Node.NodeExecutor)
     */
    public IngwerNodeCommand(@NotNull CommandTarget @NotNull[] commandTargets, @NotNull CommandNode commandNode) {
        super(commandNode.getCommandName(), commandNode.getDescription());
        this.commandNode = commandNode;
        this.commandTargets = commandTargets;
    }

    @ApiStatus.Experimental
    public IngwerNodeCommand(@NotNull CommandTarget @NotNull[] commandTargets, String commandName,
                             String description, Node.NodeExecutor node_action) {
        super(commandName, description);
        this.commandNode = new CommandNode(getLogger(),getName(),getDescription()) {
            @Override
            public void execute(CommandData commandData, NodeTrace nodeTrace) {
                node_action.accept(commandData,nodeTrace);
            }
        };
        this.commandTargets = commandTargets;
    }

    /**
     * Access the CommandNod
     * @return commandNode
     */
    protected final CommandNode getCommandNode() {
        return commandNode;
    }

    /**
     * Copy the CommandNode
     * @return commandNode
     */
    public final CommandNode getCommandNodeClone() {
        return commandNode.clone();
    }

    @Override
    public final void execute(IngwerCommandSender v1, String[] v2, CommandTarget v3) {
        Node node = getCommandNode().startWalking(v1, v2, v3);
        if(node == null) {
            getLogger().warning("failed to execute: \"" + Arrays.toString(v2) + "\"");
        }else
            getLogger().info("finished at: " + node.getName());
    }

    @Override
    public final CommandTarget[] getCommandTargets() {
        return commandTargets;
    }
}
