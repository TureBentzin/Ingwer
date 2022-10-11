package de.bentzin.ingwer.command.node;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.ForOverride;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.utils.CompletableOptional;
import de.bentzin.ingwer.utils.DoNotOverride;
import org.apache.commons.lang.NotImplementedException;
import org.checkerframework.checker.optional.qual.MaybePresent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Node is the base for the node based CommandSystem.
 * The head of each CommandTree should always be a CommandNode
 *
 * @param <T> the type of node
 * @implNote please implement the {@link this#clone()} & {@link Object#toString()} methods!
 * @implNote This may be an implementation of {@link Permissioned}
 * @see CommandNode
 */
@SuppressWarnings("rawtypes")
public interface Node<T> extends Cloneable {

    /**
     * @param node the suspected CommandNod
     * @return if the node is not a CommandNode
     * @implNote This should be called in every {@link Node#append(Node)} method!
     */
    static boolean checkCommandNode(Node node) {
        return !(node instanceof CommandNode);
    }

    /**
     * @param node the suspected CommandNod
     * @implNote This should be called in every {@link Node#append(Node)} method!
     * @tip import this method into your node: "import static de.bentzin.ingwer.command.node.Node.checkCommandNodeAndThrow;"
     */
    static void checkCommandNodeAndThrow(Node node) {
        if (!checkCommandNode(node))
            throw new IllegalArgumentException("the given node: \"" + node.getName() + "\"" + " is a commandNode and cannot be added here!");
    }


    /**
     * Adds the given node to the level below this node
     *
     * @param node the given node
     * @return this
     * @throws IllegalArgumentException if this does not accept a node
     * @implNote Make sure that no CommandNode is added here
     */
    Node<T> append(Node node) throws IllegalArgumentException;

    /**
     * @return all nodes contained (or below) this node. Should be null if there is no node below this
     * @implNote changes on the here given collection should never affect future requests
     */
    @Nullable
    Collection<Node> getNodes();

    /**
     * @return if this has nodes below
     */
    @DoNotOverride
    default boolean hasNodes() {
        return getNodes() != null;
    }

    /**
     * @param input     the current argument
     * @param nodeTrace all nodes before this
     * @return type object parsed out of the current argument
     * @throws InvalidParameterException if input could not be parsed
     */
    @NotNull
    T parse(@NotNull String input, @NotNull NodeTrace nodeTrace) throws InvalidParameterException;

    /**
     * Checks if the given input can be parsed by {@link this#parse(String, NodeTrace)}
     *
     * @param input     given input
     * @param nodeTrace node trace
     * @return true if the argument could be parsed, false if parsing fails
     */
    default boolean accepts(String input, NodeTrace nodeTrace) {
        try {
            T parse = parse(input, nodeTrace);
            return true;
        } catch (InvalidParameterException ignored) {
            return false;
        }
    }

    /**
     * @param commandData commandData
     * @param nodeTrace   trace to this (last)
     * @implNote execute is getting called if this is the last node in the trace
     */

    @ForOverride
    void execute(CommandData commandData, NodeTrace nodeTrace);

    /**
     * @return for this trace unique name
     * @implNote the CommandNode at the top of a trace always returns "root"
     */
    @NotNull
    String getName();

    /**
     * @return Type of this node
     */
    @ApiStatus.Experimental
    @Beta
    default Type getType() {
        return new TypeToken<T>(getClass()) {
        }.getType();
    }

    /**
     * @return a collection of all values the Node should react to. This may only contain one value!
     * @implNote values needs to contain at least one String. Should never be used for checking use {@link Node#resembles(String)} instead
     */
    @ForOverride
    Collection<String> values();

    default boolean singleValued() {
        return values().size() == 1;
    }

    /**
     * @return if the {@link Node#values()} does not return all possible values. (Only used if the amount of possible values is too big to handle)
     * @implNote if you return true here, you should override {@link Node#resembles(String)} and {@link Node#singleValued()}
     */
    default boolean uncertainValues() {
        return false;
    }

    /**
     * @param value the value to check
     * @return if this nodes values contain the given value
     */
    default boolean resembles(String value) {
        if (uncertainValues()) {
            throw new NotImplementedException("an internal error accord while checking \"" + value + "\" for node " + getName() + " : Seems like the resembles(String) method does not support uncertain values! Please report this issue!");
        }
        return values().contains(value);
    }

    /**
     * @return the commandNode this node is currently associated with
     * @implNote if this node is used widespread across different commands, then you should think about making this method abstract
     * (if it is not possible to receive the CommandNode over parameter)
     * Its also even possible that the CommandNode can generate Nodes for you (checkout the preset package here)
     * @implNote is present after the nodeTree was initialized by the CommandNode
     */
    CompletableOptional<CommandNode> getCommandNode();

    /**
     * @param commandNode the commandNode the current initialization is associated to
     * @see CommandNode
     * @see this#getCommandNode()
     */
    void initialize(CommandNode commandNode);

    /**
     * collects all nodes in the tree (in which this Node is the Head)
     *
     * @return collection with all nodes below this in the Tree
     * @implNote this may contain duplicates
     */
    default Collection<Node> collect() {
        return collect(false);
    }

    /**
     * collects all nodes in the tree (in which this Node is the Head)
     *
     * @return collection with all nodes below this in the Tree
     * @implNote this may contain duplicates
     */
    default Collection<Node> collect(boolean checkForDuplicates) {
        Collection<Node> collection = new ArrayList<>();
        collection.add(this);
        if (hasNodes()) {
            Objects.requireNonNull(getNodes())
                    .forEach(!checkForDuplicates ? node -> collection.addAll(node.collect()) : node -> {
                        node.collect(true).forEach(node2 -> {
                            if (!collection.contains(node2)) collection.add((Node) node2);
                        });
                    });
        }
        return collection;
    }

    default Collection<Node> find(Predicate<Node> nodePredicate) {
        return collect(false).stream().takeWhile((Predicate<? super Node>) nodePredicate).toList();
    }

    /**
     * like foreach()
     *
     * @param function action to perform. Boolean says if forest() should return the current node.
     */
    default Node forest(@NotNull Function<Node, Boolean> function) {
        Boolean exit = function.apply(this);
        if (exit) {
            return this;
        }
        if (hasNodes()) {
            Objects.requireNonNull(getNodes()).forEach(function::apply);
        }
        return this;
    }

    /**
     * @param argumentQueue queue with the remaining unparsed Arguments
     * @param traceBuilder  should be build while executing
     * @param data          data to be passed to execute
     * @return the final node or null if node could not be found!
     */
    @DoNotOverride
    default Node<T> walk(Queue<String> argumentQueue, NodeTraceBuilder traceBuilder, CommandData data) throws ExecutionException, InterruptedException {
        if (getCommandNode().isEmpty()) {
            //initialization error
            throw new IllegalStateException("Node is not yet initialized!");
        }
        //rec
        traceBuilder.append(this);
        final boolean last = argumentQueue.isEmpty();
        if (last) {
            getPermission().ifPresentOrElse(ingwerPermission -> {
                if (data.commandSender().getPermissions().contains(ingwerPermission)) {
                    execute(data, traceBuilder.build());
                } else {
                    Permissioned.lacking(data.commandSender(), ingwerPermission);
                }
            }, () -> execute(data, traceBuilder.build()));
            return this;
        }
        String argument = argumentQueue.poll();

        //code


        if (hasNodes()) {
            for (Node node : Objects.requireNonNull(getNodes())) {
                if (node.resembles(argument)) {
                    //found
                    getCommandNode().getOrThrow().getLogger().info("walk: " + node + " for " + argument);
                    return node.walk(argumentQueue, traceBuilder, data);
                }
            }
            getCommandNode().getOrThrow().getLogger().debug(getName() + " could not find a matching node for: " + argument + "!");

            getCommandNode().getOrThrow().usage().accept(data, traceBuilder.build());
        } else {
            getCommandNode().getOrThrow().usage().accept(data, traceBuilder.build());
        }
                return null;
    }

    /**
     * Get the permission that is required to execute THIS node
     * (that should not affect children of this)
     *
     * @return
     * @implNote The Permission might be present.
     * @see Optional<IngwerPermission>
     * @see Node#execute(CommandData, NodeTrace)
     */
    @MaybePresent
    default Optional<IngwerPermission> getPermission() {
        if (this instanceof Permissioned p)
            return Optional.of(p.getPermission());
        return Optional.empty();
    }

    /**
     * @return true if the Optional from {@link this#getPermission()} is present!
     * @implNote should only be used if further investigation of a permission is not required
     * if you need to read out a permission use: {@link Node#getPermission()} & {@link Optional#isPresent()} or {@link Optional#ifPresent(Consumer)}
     */
    default boolean hasPermission() {
        return getPermission().isPresent();
    }


    /**
     * Used to execute a node
     */
    interface NodeExecutor extends BiConsumer<CommandData, NodeTrace> {

        /**
         * This NodeExecutor does nothing when executed
         */
        NodeExecutor ignore = (ignored, ignored1) -> {
        };

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
