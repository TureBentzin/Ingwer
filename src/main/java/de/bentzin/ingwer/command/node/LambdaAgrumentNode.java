package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.utils.DoNotOverride;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LambdaAgrumentNode extends ArgumentNode{

    @Nullable
    private final NodeExecutor nodeExecutor;

    @Deprecated
    @ApiStatus.Internal
    public LambdaAgrumentNode(String name, ArrayList<Node> nodes, ArrayList<String> values, @Nullable NodeExecutor nodeExecutor) {
        super(name, nodes, values);
        this.nodeExecutor = nodeExecutor;
    }

    @Deprecated
    @ApiStatus.Internal
    public LambdaAgrumentNode(String name, ArrayList<String> values, @Nullable NodeExecutor nodeExecutor) {
        super(name, values);
        this.nodeExecutor = nodeExecutor;
    }

    @Deprecated
    @ApiStatus.Internal
    public LambdaAgrumentNode(String name, ArrayList<Node> nodes, @Nullable NodeExecutor nodeExecutor, String... values) {
        super(name, nodes, values);
        this.nodeExecutor = nodeExecutor;
    }

    @Deprecated
    @ApiStatus.Internal
    public LambdaAgrumentNode(String name, @Nullable NodeExecutor nodeExecutor, String... values) {
        super(name, values);
        this.nodeExecutor = nodeExecutor;
    }

    @ApiStatus.Experimental
    @ApiStatus.Internal
    public LambdaAgrumentNode(String name, ArrayList<Node> nodes, String value, @Nullable NodeExecutor nodeExecutor) {
        super(name, nodes, value);
        this.nodeExecutor = nodeExecutor;
    }

    @ApiStatus.Experimental
    public LambdaAgrumentNode(String name, String value, @Nullable NodeExecutor nodeExecutor) {
        super(name, value);
        this.nodeExecutor = nodeExecutor;
    }

    public LambdaAgrumentNode(String argument, @Nullable NodeExecutor nodeExecutor) {
        super(argument);
        this.nodeExecutor = nodeExecutor;
    }

    /**
     * @param commandData commandData
     * @param nodeTrace   trace to this (last)
     * @implNote execute is getting called if this is the last node in the trace
     */
    @Override
    @DoNotOverride
    public final void execute(CommandData commandData, NodeTrace nodeTrace,String s) {
        if(getNodeExecutor() == null)
            NodeExecutor.ignore.accept(commandData,nodeTrace);
        else
             getNodeExecutor().accept(commandData,nodeTrace);
    }

    /**
     * You may override this. Please consider to override {@link this#setNodeExecutor(NodeExecutor)} too.
     * @return nodeExecutor that should be used. will be ignored if null
     */
   @Nullable
    public NodeExecutor getNodeExecutor() {
       try {
           return nodeExecutor;
       }catch (Exception nodeExecutor) {
           nodeExecutor.printStackTrace();;
       }
       return null;

    }

    /**
     * You may override this. Please consider to override {@link this#getNodeExecutor()} too.
     * The default implementation throws an {@link UnsupportedOperationException} - you may implement the method so it can be used.
     * @implNote any here specified value should be returned by {@link this#getNodeExecutor()}, throwing an {@link IllegalArgumentException} or throwing the {@link UnsupportedOperationException}
     * @param nodeExecutor new nodeExecutor, will be ignored when is null
     * @throws UnsupportedOperationException if changing the nodeExecutor is not supported
     */
    @ApiStatus.Internal
    protected void setNodeExecutor(@Nullable NodeExecutor nodeExecutor) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("this node does not allow this action!");
    }
}
