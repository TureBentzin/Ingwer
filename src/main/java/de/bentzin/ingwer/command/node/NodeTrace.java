package de.bentzin.ingwer.command.node;

import com.google.common.annotations.Beta;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Immutable
public class NodeTrace {

    private final ArrayList<Node> nodes;
    private final Map<String,Node> map = new HashMap<>();

    /**
     * @param nodes make shure that the Names of the given nodes are unique!
     */
    public NodeTrace(ArrayList<Node> nodes) {
        this.nodes = Objects.requireNonNull(nodes);
        nodes.stream().collect(Collectors.toUnmodifiableMap(Node::getName,node -> node));
    }

    public Collection<Node> cloneNodes() {
        return Collections.unmodifiableCollection(nodes);
    }

    public Node get(int index) {
        return nodes.get(index);
    }

    public Node get(String name) {
        Node node = map.get(name);
        if(node == null) throw new NoSuchElementException("Node : \"" + name + "\" could not be found!");
        return node;
    }

    @ApiStatus.Experimental
    @Beta
    public <E> List<Node<E>> getNodesFromType(@NotNull Class<E> clazz) {
        List<Node<E>> list = new ArrayList<>();
        String name = clazz.getName();
        for (Node node : nodes)
            if(node.getType().getTypeName().equals(name))
                list.add(node);
        return list;
    }

    public Node[] trace() {
        return nodes.toArray(new Node[0]);
    }

    public int size() {
        return nodes.size();
    }

    public void forEach(Consumer<Node> action) {
        nodes.forEach(action);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @ApiStatus.Experimental
    @ApiStatus.Internal
    private void clean() {
        map.clear();
        nodes.clear();
    }

    public Node last() {
        return nodes.get(nodes.size() - 1);
    }


    public Node first() {
        return nodes.get(0);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NodeTrace: [");
        boolean first = true;
        for (Node node : nodes) {
            if(first)
                builder.append(node);
            else builder.append(" -> ").append(node);
            first = false;
        }
        builder.append("] ").append("<").append(size()).append(">");
        return builder.toString();
    }
}
