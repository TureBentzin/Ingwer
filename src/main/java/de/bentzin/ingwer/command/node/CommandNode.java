package de.bentzin.ingwer.command.node;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.logging.Logger;
import org.checkerframework.checker.optional.qual.MaybePresent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Entry point of command building
 */
public abstract class CommandNode implements Node<String>{

    //static -> new
    @Contract("_ -> new")
    public static @NotNull CommandNode createOfIngwerCommand(@NotNull IngwerCommand ingwerCommand, NodeExecutor nodeExecutor) {
        return new CommandNode(ingwerCommand.getLogger(), ingwerCommand.getName(), ingwerCommand.getDescription()) {
            @Override
            public void execute(CommandData commandData, NodeTrace nodeTrace) {
                nodeExecutor.accept(commandData,nodeTrace);
            }

        };
    }

    @Contract("_ -> new")
    public static @NotNull CommandNode createOfIngwerCommand(@NotNull IngwerCommand ingwerCommand) {
        return createOfIngwerCommand(ingwerCommand,NodeExecutor.ignore);
    }


    @NotNull
    private final Logger logger;

    @NotNull
    private final String command_name;
    @Nullable
    private final String description;
    @NotNull
    private final ArrayList<Node> nodes = new ArrayList<>();


    public CommandNode(Logger logger, @NotNull String command_name, @Nullable String description) {
        this.logger = logger;
        this.command_name = command_name;
        this.description = description;
    }


    @Override
    public CommandNode append(Node node) {

        return this;
    }

    @Override
    public @NotNull String getName() {
        return "root";
    }

    @NotNull
    public String getCommandName(){
        return command_name;
    }

    @NotNull
    public String getDescription() {
        if(description == null) return "";
        return description;
    }

    @Override
    public Collection<String> values() {
        return List.of(command_name);
    }

    /**
     *
     * @param input the current argument
     * @param nodeTrace all nodes before this
     * @return the input
     * @throws InvalidParameterException if the nodeTrace is not empty or if the input does not match the commandName
     */
    @NotNull
    @Override
    public String parse(@NotNull String input, @NotNull NodeTrace nodeTrace) throws InvalidParameterException {
        if(!nodeTrace.isEmpty())
            throw new InvalidParameterException("this node is a CommandNode and can only be placed at the beginning of a nodeTrace");
        if(!input.equals(command_name))
            throw new InvalidParameterException("the given input does not match the associated commandName");
        return command_name;
    }

    @Nullable
    @Override
    public ArrayList<Node> getNodes() {
        return (ArrayList<Node>) nodes.clone();
    }

    @Override
    public @MaybePresent Optional<CommandNode> getCommandNode() {
        return Optional.of(this);
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * @param commandNode the commandNode the current initialization is associated to
     * @see CommandNode
     * @see this#getCommandNode()
     */
    @Override
    public void initialize(CommandNode commandNode) {
        logger.info("initializing CommandNode!");
    }

    /**
     *
     * @implNote Waring: calling {@link CommandNode#execute(CommandData, NodeTrace)} on the copy will cause the {@link this#execute(CommandData, NodeTrace)} method of this to get called by default!
     * @return a copy of this CommandNode
     * @throws CloneNotSupportedException
     */
    @Override
    protected CommandNode clone(){
        return new CommandNode(logger,command_name,description) {
            /**
             * @param commandData commandData
             * @param nodeTrace   trace to this (last)
             * @implNote execute is getting called if this is the last node in the trace
             */
            @Override
            public void execute(CommandData commandData, NodeTrace nodeTrace) {
                CommandNode.this.execute(commandData,nodeTrace);
            }
        };
    }
}
