package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.command.node.LambdaAgrumentNode;
import de.bentzin.ingwer.command.node.Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UsageNode extends LambdaAgrumentNode {
    public UsageNode(String name, ArrayList<Node> nodes, ArrayList<String> values) {
        super(name, nodes, values, null);
    }

    public UsageNode(String name, ArrayList<String> values) {
        super(name, values, null);
    }

    public UsageNode(String name, ArrayList<Node> nodes, String... values) {
        super(name, nodes, null, values);
    }

    public UsageNode(String name, String... values) {
        super(name, null, values);
    }

    public UsageNode(String name, ArrayList<Node> nodes, String value) {
        super(name, nodes, value, null);
    }

    public UsageNode(String name, String value) {
        super(name, value, null);
    }

    public UsageNode(String argument) {
        super(argument, null);
    }

    @Override
    public @NotNull NodeExecutor getNodeExecutor(){
        return getCommandNode().getOrThrow().usage();
    }
}
