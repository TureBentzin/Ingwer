package de.bentzin.ingwer.command.node;

import org.checkerframework.checker.optional.qual.MaybePresent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.bentzin.ingwer.command.node.Node.checkCommandNodeAndThrow;

public abstract class SimpleNode<T> implements Node<T> {

    private final String name;
    private final ArrayList<Node> nodes;
    private final List<String> values;

    private Optional<CommandNode> commandNode;

    public SimpleNode(String name, ArrayList<Node> nodes, ArrayList<String> values) {
        this.name = name;
        this.nodes = nodes;
        this.values = values;
        commandNode = Optional.empty();
    }

    public SimpleNode(String name, ArrayList<String> values) {
        this.name = name;
        this.values = values;
        this.nodes = new ArrayList<>();
        commandNode = Optional.empty();
    }

    public SimpleNode(String name, ArrayList<Node> nodes, String... values) {
        this.name = name;
        this.nodes = nodes;
        this.values = List.of(values);
        commandNode = Optional.empty();
    }

    public SimpleNode(String name, String... values) {
        this.name = name;
        this.values = List.of(values);
        this.nodes = new ArrayList<>();
        commandNode = Optional.empty();
    }

    /**
     * Adds the given node to the level below this node
     *
     * @param node the given node
     * @return this
     * @throws IllegalArgumentException if this does not accept a node
     * @implNote Make sure that no CommandNode is added here
     */
    @Override
    public Node append(Node node) throws IllegalArgumentException {
        checkCommandNodeAndThrow(node);
        nodes.add(node);
        return this;
    }

    /**
     * @return all nodes contained (or below) this node. Should be null if there is no node below this
     * @implNote changes on the here given collection should never affect future requests
     */
    @Override
    public @Nullable Collection<Node> getNodes() {
        if (nodes.isEmpty()) {
            return null;
        }
        return nodes;
    }

    @Override
    public Collection<String> values() {
        return values;
    }

    @Override
    public String toString() {
        return name + "<" + nodes.size() + ">";
    }

    /**
     * @return for this trace unique name
     * @implNote the CommandNode at the top of a trace always returns "root"
     */
    @Override
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return the commandNode this node is currently associated with
     * @implNote if this node is used widespread across different commands, then you should think about making this method abstract
     * (if it is not possible to receive the CommandNode over parameter)
     * Its also even possible that the CommandNode can generate Nodes for you (checkout the preset package here)
     */
    @Override
    public @MaybePresent Optional<CommandNode> getCommandNode() {
        return commandNode;
    }

    /**
     * @param commandNode the commandNode the current initialization is associated to
     * @see CommandNode
     * @see this#getCommandNode()
     */
    @Override
    public void initialize(CommandNode commandNode) {
        this.commandNode = Optional.of(commandNode);
    }
}
