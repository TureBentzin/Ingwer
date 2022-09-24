package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.utils.DoNotOverride;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractNode<T> implements Node<T> {


    private final String name;
    private final Collection<Node> nodes;

    public AbstractNode(String name) {
        this.name = name;
        this.nodes = new ArrayList<>();
    }

    public AbstractNode(String name, ArrayList<Node> nodes) {
        this.name = name;
        this.nodes = nodes;
    }

    /**
     * @param commandData commandData
     * @param nodeTrace   trace to this (last)
     */
    @Override
    public final void execute(@NotNull CommandData commandData, NodeTrace nodeTrace) {
        String last = commandData.cmd()[commandData.cmd().length - 1];
        execute(commandData, nodeTrace, parse(last, nodeTrace));
    }

    public abstract void execute(CommandData commandData, NodeTrace nodeTrace, T t);

    /**
     * @return for this trace unique name
     * @implNote the CommandNode at the top of a trace always returns "root"
     */
    @Override
    public final @NotNull String getName() {
        return name;
    }

    @Override
    public final Node<T> append(Node node) throws IllegalArgumentException {
        Node.checkCommandNodeAndThrow(node);
        nodes.add(node);
        return this;
    }

    @Nullable
    @Override
    public final Collection<Node> getNodes() {
        return nodes;
    }


    @Override
    @DoNotOverride
    public String toString() {
        if (hasNodes())
            return getName() + "<" + getNodes().size() + ">";
        return getName() + "<0>";
    }
}
