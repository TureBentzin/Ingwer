package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.CommandData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Node that contains one value and only requires an implementation of {@link ArgumentNode#execute(CommandData, NodeTrace)}
 */
public abstract class ArgumentNode extends SimpleNode<String> {


    @ApiStatus.Internal
    @Deprecated
    protected ArgumentNode(String name, ArrayList<Node> nodes, ArrayList<String> values) {
        super(name, nodes, values);
    }

    @ApiStatus.Internal
    @Deprecated
    public ArgumentNode(String name, ArrayList<String> values) {
        super(name, values);
    }

    @ApiStatus.Internal
    @Deprecated
    protected ArgumentNode(String name, ArrayList<Node> nodes, String... values) {
        super(name, nodes, values);
    }

    @ApiStatus.Internal
    @Deprecated
    public ArgumentNode(String name, String... values) {
        super(name, values);
    }

    //single value

    @ApiStatus.Experimental
    protected ArgumentNode(String name, ArrayList<Node> nodes, String value) {
        super(name, nodes, value);
    }

    @ApiStatus.Experimental
    public ArgumentNode(String name, String value) {
        super(name, value);
    }

    public ArgumentNode(String argument) {
        super(argument, argument);
    }


    /**
     * @param input     the current argument
     * @param nodeTrace all nodes before this
     * @return type object parsed out of the current argument
     * @throws InvalidParameterException if input could not be parsed
     */
    @Override
    public @NotNull String parse(@NotNull String input, @NotNull NodeTrace nodeTrace) {
        return input;
    }
}
