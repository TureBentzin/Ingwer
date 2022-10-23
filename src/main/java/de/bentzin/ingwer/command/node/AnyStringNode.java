package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.CommandData;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * @author Ture Bentzin
 * 23.10.2022
 */
public class AnyStringNode extends WildNode<String>{
    private final NodeExecutor executor;

    /**
     *
     * @param name
     * @param executor
     */
    public AnyStringNode(String name,@Nullable NodeExecutor executor) {
        super(name, s -> true);
        this.executor = executor;
    }

    public AnyStringNode(String name, ArrayList<Node> nodes,@Nullable NodeExecutor executor) {
        super(name,nodes,s -> true);
        this.executor = executor;
    }

    @Override
    public void execute(CommandData commandData, NodeTrace nodeTrace, String s) throws NodeTrace.NodeParser.NodeParserException {
        if(executor != null)
            executor.accept(commandData,nodeTrace);
        else {
            throw new NotImplementedException("Somebody forgot to implement a custom execute method here...");
        }
    }

    /**
     * @param input     the current argument
     * @param nodeTrace all nodes before this
     * @return type object parsed out of the current argument
     * @throws InvalidParameterException if input could not be parsed
     * @implNote You may want to override this?
     */
    @Override
    public @NotNull String parse(@NotNull String input, @NotNull NodeTrace nodeTrace) throws InvalidParameterException {
        return input;
    }
}
