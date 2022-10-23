package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.command.node.AbstractNode;
import de.bentzin.ingwer.command.node.CommandNode;
import de.bentzin.ingwer.command.node.Node;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.utils.CompletableOptional;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class NumberNode extends AbstractNode<Integer> {


    /*
    public static final  Collection<Integer> INTEGERS = IntStream.rangeClosed(Integer.MIN_VALUE, Integer.MAX_VALUE).boxed().toList();
    public static final Collection<String> INTEGERS_STRING = INTEGERS.stream().map(Object::toString).toList();
     */

    private final Predicate<Integer> condition;
    private final CompletableOptional<CommandNode> commandNode = new CompletableOptional<>();

    public NumberNode(String name) {
        super(name);
        condition = integer -> true;
    }

    public NumberNode(String name, ArrayList<Node<?>> nodes) {
        super(name, nodes);
        condition = integer -> true;
    }

    public NumberNode(String name, Predicate<Integer> condition) {
        super(name);
        this.condition = condition;
    }

    public NumberNode(String name, ArrayList<Node<?>> nodes, Predicate<Integer> condition) {
        super(name, nodes);
        this.condition = condition;
    }

    /**
     * @param input     the current argument
     * @param nodeTrace all nodes before this
     * @return type object parsed out of the current argument
     * @throws InvalidParameterException if input could not be parsed
     */
    @Override
    public @NotNull Integer parse(@NotNull String input, @NotNull NodeTrace nodeTrace) throws InvalidParameterException {
        Integer integer = Integer.parseInt(input);
        if (condition.test(integer)) {
            return integer;
        } else {
            throw new InvalidParameterException("the given input: \"" + input + "\" is not allowed here!");
        }
    }

    /**
     * @return a collection of all values the Node should react to. This may only contain one value!
     * @implNote values needs to contain at least one String
     */
    @Override
    public Collection<String> values() {
        return List.of("0");
    }

    @Override
    public boolean uncertainValues() {
        return true;
    }

    @Override
    public boolean resembles(String value) {
        return condition.test(Integer.parseInt(value));
    }

    @Override
    public boolean singleValued() {
        return false;
    }

    /**
     * @return the commandNode this node is currently associated with
     * @implNote if this node is used widespread across different commands, then you should think about making this method abstract
     * (if it is not possible to receive the CommandNode over parameter)
     * Its also even possible that the CommandNode can generate Nodes for you (checkout the preset package here)
     * @implNote is present after the nodeTree was initialized by the CommandNode
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
    public void initialize(CommandNode commandNode) {
        this.commandNode.complete(commandNode);
    }
}
