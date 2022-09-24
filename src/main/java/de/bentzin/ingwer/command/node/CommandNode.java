package de.bentzin.ingwer.command.node;
import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.preset.UsageNodeExecutor;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.utils.CompletableOptional;
import de.bentzin.ingwer.utils.FinalCompletableOptional;
import org.checkerframework.checker.optional.qual.MaybePresent;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Entry point of command building
 */
public abstract class CommandNode implements Node<String>{

    //static -> new

    /**
     *
     * @param ingwerCommand initialized ingwerCommand
     * @param nodeExecutor executor
     * @implNote Never call in constructor of an IngwerCommand
     * @return
     */
    @Contract("_,_ -> new")
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

    /**
     * generates a new UsageNodeExecutor based on this CommandNode
     * @return new UsageNodeExecutor
     */
    public UsageNodeExecutor usage() {
        return UsageNodeExecutor.generate(this);
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
        Node.checkCommandNodeAndThrow(node);
        nodes.add(node);
        return this;
    }

    /**
     * This needs to be called before the CommandNode can be used and execute a command
     * after calling this you should not add more nodes to the tree or modify it. After modification there
     * is a high chance that nodes may not work properly or execution fails due to a lack of initialization
     * of the individual nodes
     * @return this
     */
    public CommandNode finish() {
        getLogger().debug("starting to initialize command: " + command_name + "!");
        Collection<Node> collect = collect();
        getLogger().debug("collected " + collect.size() + " nodes!");
        collect.forEach(node -> node.initialize(this));
        getLogger().info("successfully initialized node tree of: "+ command_name);
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


    /**
     *
     * @param v1 executor
     * @param v2 should contain at lease the commandName
     * @param v3 targets as defined...
     * @return the node {@link this#walk(Queue, NodeTraceBuilder, CommandData)} or null if no node was found
     */
    @Nullable
    @ApiStatus.Internal
    protected Node startWalking(IngwerCommandSender v1, String[] v2, CommandTarget v3) {
        CommandData data = new CommandData(v1,v2,v3);
        Queue<String> argumentQueue = new LinkedList<>();
        Collections.addAll(argumentQueue, v2);
        NodeTraceBuilder nodeTraceBuilder = new NodeTraceBuilder();
        String poll = argumentQueue.poll();
        if(poll.equals(command_name))
            getLogger().debug("dequeue because start... " + poll);
        else {
            throw new IllegalStateException("cant match commandNode and commandName");
        }
        try {
            return walk(argumentQueue,nodeTraceBuilder,data);
        } catch (Exception e){
            IngwerThrower.acceptS(e);
        }

        return this;
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
    public final String parse(@NotNull String input, @NotNull NodeTrace nodeTrace) throws InvalidParameterException {
        if(!nodeTrace.isEmpty())
            throw new InvalidParameterException("this node is a CommandNode and can only be placed at the beginning of a nodeTrace");
        if(!input.equals(command_name))
            throw new InvalidParameterException("the given input does not match the associated commandName");
        return command_name;
    }

    @Override
    public String toString() {
        return command_name;
    }

    @Nullable
    @Override
    public ArrayList<Node> getNodes() {
        return (ArrayList<Node>) nodes.clone();
    }

    @Override
    public CompletableOptional<CommandNode> getCommandNode() {
        return new FinalCompletableOptional<>(this);
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
