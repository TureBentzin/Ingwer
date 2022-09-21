package de.bentzin.ingwer.command.node;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.stream.Stream;

@ApiStatus.Experimental
public class NodeTraceBuilder implements Cloneable {

    ArrayList<Node> nodeArrayDeque = new ArrayList<>();

    public NodeTraceBuilder()  {

    }

    public NodeTraceBuilder append(Node node) {
        nodeArrayDeque.add(node);
        return this;
    }

    public Stream<Node> stream() {
        return nodeArrayDeque.stream();
    }

    public NodeTrace build() {
        return new NodeTrace(nodeArrayDeque);
    }
}
