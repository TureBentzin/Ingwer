package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.utils.CompletableOptional;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Node is the base for the node based CommandSystem.
 * The head of each CommandTree should always be a CommandNode
 *
 * @param <T> the type of node
 * @implNote please implement the {@link this#clone()} & {@link Object#toString()} methods!
 * @implNote This may be an implementation of {@link Permissioned}
 * @see ArgumentNode
 * @see Node
 * @see Permissioned
 */
public abstract class SimpleNode<T> extends AbstractNode<T> {

    private final List<String> values;

    private final CompletableOptional<CommandNode> commandNode = new CompletableOptional<>();

    public SimpleNode(String name, ArrayList<Node> nodes, ArrayList<String> values) {
        super(name, nodes);
        this.values = values;
    }

    public SimpleNode(String name, ArrayList<String> values) {
        super(name);
        this.values = values;
    }

    public SimpleNode(String name, ArrayList<Node> nodes, String... values) {
        super(name, nodes);
        this.values = List.of(values);
    }

    public SimpleNode(String name, String... values) {
        super(name);
        this.values = List.of(values);
    }


    @Override
    public Collection<String> values() {
        return values;
    }

    /**
     * @return the commandNode this node is currently associated with
     * @implNote if this node is used widespread across different commands, then you should think about making this method abstract
     * (if it is not possible to receive the CommandNode over parameter)
     * Its also even possible that the CommandNode can generate Nodes for you (checkout the preset package here)
     */
    @Override
    public CompletableOptional<CommandNode> getCommandNode() {
        return commandNode;
    }

    /**
     * @param commandNode the commandNode the current initialization is associated to
     * @see CommandNode
     * @see this#getCommandNode()
     */
    @Override
    public void initialize(@NotNull CommandNode commandNode) {
        this.commandNode.complete(commandNode);
    }
}
