package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.CommandData;

import java.util.ArrayList;

public class LambdaAgrumentNode extends ArgumentNode{

    private final NodeExecutor nodeExecutor;

    public LambdaAgrumentNode(String name, ArrayList<Node> nodes, ArrayList<String> values, NodeExecutor nodeExecutor) {
        super(name, nodes, values);
        this.nodeExecutor = nodeExecutor;
    }

    public LambdaAgrumentNode(String name, ArrayList<String> values, NodeExecutor nodeExecutor) {
        super(name, values);
        this.nodeExecutor = nodeExecutor;
    }

    public LambdaAgrumentNode(String name, ArrayList<Node> nodes, NodeExecutor nodeExecutor, String... values) {
        super(name, nodes, values);
        this.nodeExecutor = nodeExecutor;
    }

    public LambdaAgrumentNode(String name, NodeExecutor nodeExecutor, String... values) {
        super(name, values);
        this.nodeExecutor = nodeExecutor;
    }

    public LambdaAgrumentNode(String name, ArrayList<Node> nodes, String value, NodeExecutor nodeExecutor) {
        super(name, nodes, value);
        this.nodeExecutor = nodeExecutor;
    }

    public LambdaAgrumentNode(String name, String value, NodeExecutor nodeExecutor) {
        super(name, value);
        this.nodeExecutor = nodeExecutor;
    }

    /**
     * @param commandData commandData
     * @param nodeTrace   trace to this (last)
     * @implNote execute is getting called if this is the last node in the trace
     */
    @Override
    public void execute(CommandData commandData, NodeTrace nodeTrace) {
        nodeExecutor.accept(commandData,nodeTrace);
    }

    public NodeExecutor getNodeExecutor() {
        return nodeExecutor;
    }
}
