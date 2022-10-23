package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.utils.CompletableOptional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Ture Bentzin
 * 11.10.2022
 */
public abstract class WildNode<T> extends AbstractNode<T> {

    private final Predicate<String> condition;
    private final CompletableOptional<CommandNode> commandNode = new CompletableOptional<>();

    public WildNode(String name, Predicate<String> condition) {
        super(name);
        this.condition = condition;
    }

    public WildNode(String name, ArrayList<Node<?>> nodes, Predicate<String> condition) {
        super(name, nodes);
        this.condition = condition;
    }

    @Override
    public Collection<String> values() {
        return List.of();
    }

    @Override
    public final boolean resembles(String value) {
        boolean test = condition.test(value);
        getCommandNode().getOrThrow().getLogger().debug("testing: " + value + " --> " + test);
        return test;
    }

    @Override
    public void initialize(CommandNode commandNode) {
        this.commandNode.complete(commandNode);
    }

    @Override
    public CompletableOptional<CommandNode> getCommandNode() {
        return commandNode;
    }

    @Override
    public boolean uncertainValues() {
        return true;
    }

    @Override
    public boolean singleValued() {
        return false;
    }
}