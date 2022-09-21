package de.bentzin.ingwer.command.node;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeToken;
import de.bentzin.ingwer.command.ext.CommandData;
import org.checkerframework.checker.optional.qual.MaybePresent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *  Node is the base for the node based CommandSystem.
 *  The head of each CommandTree should always be a CommandNode
 * @implNote please implement the {@link this#clone()} method!
 * @param <T> the type of node
 * @see CommandNode
 */
public interface Node<T> extends Cloneable {

    /**
     *
     * @implNote This should be called in every {@link Node#append(Node)} method!
     * @param node the suspected CommandNod
     * @return if the node is not a CommandNode
     */
    static boolean checkCommandNode(Node node) {
        return ! (node instanceof CommandNode);
    }

    /**
     *
     * @implNote This should be called in every {@link Node#append(Node)} method!
     * @param node the suspected CommandNod
     * @tip import this method into your node: "import static de.bentzin.ingwer.command.node.Node.checkCommandNodeAndThrow;"
     */
    static void checkCommandNodeAndThrow(Node node) {
        if(!(node instanceof CommandNode))
            throw new IllegalArgumentException("the given node: \"" + node.getName() + "\"" + " is a commandNode and cannot be added here!");
    }


    /**
     * Adds the given node to the level below this node
     * @implNote Make sure that no CommandNode is added here
     * @param node the given node
     * @return this
     * @throws IllegalArgumentException if this does not accept a node
     */
    Node<T> append(Node node) throws IllegalArgumentException;

    /**
     * @return all nodes contained (or below) this node. Should be null if there is no node below this
     * @implNote changes on the here given collection should never affect future requests
     */
    @Nullable
    Collection<Node> getNodes();

    /**
     *
     * @return if this has nodes below
     */
    default boolean hasNodes() {
        return getNodes() != null;
    }

    /**
     *
     * @param input the current argument
     * @param nodeTrace all nodes before this
     * @return type object parsed out of the current argument
     * @throws InvalidParameterException if input could not be parsed
     */
    @NotNull
    T parse(@NotNull String input,@NotNull NodeTrace nodeTrace) throws InvalidParameterException;

    /**
     * Checks if the given input can be parsed by {@link this#parse(String, NodeTrace)}
     * @param input given input
     * @param nodeTrace node trace
     * @return true if the argument could be parsed, false if parsing fails
     */
    @Deprecated
    default boolean accepts(String input, NodeTrace nodeTrace) {
        try{
            T parse = parse(input, nodeTrace);
            return true;
        }catch (InvalidParameterException ignored) {
            return false;
        }finally {

        }
    }

    /**
     * @implNote execute is getting called if this is the last node in the trace
     * @param commandData commandData
     * @param nodeTrace trace to this (last)
     */
    void execute(CommandData commandData, NodeTrace nodeTrace);

    /**
     *
     * @implNote the CommandNode at the top of a trace always returns "root"
     * @return for this trace unique name
     */
    @NotNull
    String getName();

    /**
     *
     * @return Type of this node
     */
    @ApiStatus.Experimental
    @Beta
    default Type getType() {
        return new TypeToken<T>(getClass()){}.getType();
    }

    /**
     * @return a collection of all values the Node should react to. This may only contain one value!
     * @implNote values needs to contain at least one String
     */
    Collection<String> values();

    default boolean singleValued() {
        return values().size() == 1;
    }

    /**
     *
     * @param value the value to check
     * @return if this nodes values contain the given value
     */
    default boolean resembles(String value) {
        return values().contains(value);
    }

    /**
     * @return the commandNode this node is currently associated with
     * @implNote if this node is used widespread across different commands, then you should think about making this method abstract
     * (if it is not possible to receive the CommandNode over parameter)
     * Its also even possible that the CommandNode can generate Nodes for you (checkout the preset package here)
     * @implNote is present after the nodeTree was initialized by the CommandNode
     */
    @MaybePresent
    Optional<CommandNode> getCommandNode();

    /**
     *
     * @param commandNode the commandNode the current initialization is associated to
     * @see CommandNode
     * @see this#getCommandNode()
     */
    void initialize(CommandNode commandNode);

    /**
     * collects all nodes in the tree (in which this Node is the Head)
     * @return collection with all nodes below this in the Tree
     * @implNote this may contain duplicates
     */
    default Collection<Node> collect() {
        return collect(false);
    }

    /**
     * collects all nodes in the tree (in which this Node is the Head)
     * @return collection with all nodes below this in the Tree
     * @implNote this may contain duplicates
     */
    default Collection<Node> collect(boolean checkForDuplicates) {
        Collection<Node> collection = new ArrayList<>();
        collection.add(this);
        if(hasNodes()) {
            Objects.requireNonNull(getNodes())
                    .forEach(!checkForDuplicates? node -> collection.addAll(node.collect()) : node -> {
                        node.collect(true).forEach( node2 -> {
                            if(!collection.contains(node2)) collection.add((Node) node2);
                        });
                    });
        }
        return collection;
    }

    /**
     * like foreach()
     * @param function action to perform. Boolean says if forest() should return the current node.
     */
    default Node forest(@NotNull Function<Node, Boolean> function) {
        Boolean exit = function.apply(this);
        if(exit) {
            return this;
        }
        if(hasNodes()) {
            Objects.requireNonNull(getNodes()).forEach(function::apply);
        }
    }

    /**
     *
     * @param argumentStack stack with the remaining unparsed Arguments
     * @param traceBuilder should be build while executing
     * @param data data to be passed to execute
     * @return the final node
     */
    default Node<T> walk(Stack<String> argumentStack, NodeTraceBuilder traceBuilder, CommandData data) {
        if(getCommandNode().isEmpty()) {
            //initialization error
            return this;
        }
        //rec
        String argument = argumentStack.pop();
        final boolean last = argumentStack.isEmpty();
        traceBuilder.append(this);
        //code

        if (last) {
            execute(data,traceBuilder.build());
            return this;
            //last -> execute
        }else {
            if(hasNodes()) {
                boolean found = false;
                for (Node node : Objects.requireNonNull(getNodes())) {
                    if(node.resembles(argument)) {
                        //found
                        found = true;
                        return node.walk(argumentStack,traceBuilder,data);
                    }
                }
                //usage wrong

            }else {
                //usage wrong
            }
        }
        throw new IllegalStateException("method reached dead code!");
    }


    interface NodeExecutor extends BiConsumer<CommandData, NodeTrace> {

        /**
         * This NodeExecutor does nothing when executed
         */
        NodeExecutor ignore = (ignored, ignored1) -> {};

        /**
         * Performs this operation on the given arguments.
         *
         * @param data      the commandData
         * @param nodeTrace the nodeTrace to this point
         */
        @Override
        void accept(CommandData data, NodeTrace nodeTrace);

        /**
         * Returns a composed {@code BiConsumer} that performs, in sequence, this
         * operation followed by the {@code after} operation. If performing either
         * operation throws an exception, it is relayed to the caller of the
         * composed operation.  If performing this operation throws an exception,
         * the {@code after} operation will not be performed.
         *
         * @param after the operation to perform after this operation
         * @return a composed {@code BiConsumer} that performs in sequence this
         * operation followed by the {@code after} operation
         * @throws NullPointerException if {@code after} is null
         */
        @NotNull
        @Override
        default BiConsumer<CommandData, NodeTrace> andThen(@NotNull BiConsumer<? super CommandData, ? super NodeTrace> after) {
            return BiConsumer.super.andThen(after);
        }
    }
}
