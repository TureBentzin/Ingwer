package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.Permissioned;
import org.checkerframework.checker.optional.qual.MaybePresent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.bentzin.ingwer.command.node.Node.checkCommandNodeAndThrow;

/**
 *  Node is the base for the node based CommandSystem.
 *  The head of each CommandTree should always be a CommandNode
 * @implNote please implement the {@link this#clone()} & {@link Object#toString()} methods!
 * @implNote This may be an implementation of {@link Permissioned}
 * @param <T> the type of node
 * @see ArgumentNode
 * @see Node
 * @see Permissioned
 */
public abstract class SimpleNode<T> extends AbstractNode<T> {

    private final List<String> values;

    private Optional<CommandNode> commandNode;

    public SimpleNode(String name, ArrayList<Node> nodes, ArrayList<String> values) {
        super(name,nodes);
        this.values = values;
        commandNode = Optional.empty();
    }

    public SimpleNode(String name, ArrayList<String> values) {
        super(name);
        this.values = values;
        commandNode = Optional.empty();
    }

    public SimpleNode(String name, ArrayList<Node> nodes, String... values) {
        super(name,nodes);
        this.values = List.of(values);
        commandNode = Optional.empty();
    }

    public SimpleNode(String name, String... values) {
        super(name);
        this.values = List.of(values);
        commandNode = Optional.empty();
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
    public @MaybePresent Optional<CommandNode> getCommandNode() {
        return commandNode;
    }

    /**
     * @param commandNode the commandNode the current initialization is associated to
     * @see CommandNode
     * @see this#getCommandNode()
     */
    @Override
    public void initialize(@NotNull CommandNode commandNode) {
        this.commandNode = Optional.of(commandNode);
    }
}
