package de.bentzin.ingwer.command.node;

import com.google.common.annotations.Beta;
import de.bentzin.ingwer.command.ext.CommandData;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.security.PrivilegedActionException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unused"})
@Immutable
public class NodeTrace {

    private final ArrayList<Node> nodes;
    private final Map<String, Node> map;

    /**
     * @param nodes make shure that the Names of the given nodes are unique!
     */
    public NodeTrace(ArrayList<Node> nodes) {
        this.nodes = Objects.requireNonNull(nodes);
        map = nodes.stream().collect(Collectors.toUnmodifiableMap(Node::getName, node -> node));
    }

    public Collection<Node> cloneNodes() {
        return Collections.unmodifiableCollection(nodes);
    }

    public Node get(int index) {
        return nodes.get(index);
    }

    public final <T> Optional<Node<T>> getOptional(int i) {
        try {
            return Optional.of(get(i));
        } catch (NoSuchElementException ignored) {
            return Optional.empty();
        }
    }

    public Node get(String name) {
        Node node = map.get(name);
        if (node == null) throw new NoSuchElementException("Node : \"" + name + "\" could not be found!");
        return node;
    }

    public final <T> Optional<Node<T>> getOptional(String name) {
        try {
            return Optional.of(get(name));
        } catch (NoSuchElementException ignored) {
            return Optional.empty();
        }
    }

    /**
     * @param data used for parsing
     * @return new nodeParser
     * @implNote The NodeParser can be used to get the parsed values of the Nodes in this NodeTrace
     */
    @NotNull
    public NodeParser parser(CommandData data) {
        return new NodeParser(this, data);
    }

    @ApiStatus.Experimental
    @Beta
    public <E> List<Node<E>> getNodesFromType(@NotNull Class<E> clazz) {
        List<Node<E>> list = new ArrayList<>();
        String name = clazz.getName();
        for (Node node : nodes)
            if (node.getType().getTypeName().equals(name))
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

    @SuppressWarnings("ConstantConditions")
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


    //wrapped to ArrayList

    /**
     * Returns the index of the first occurrence of the specified element
     * in this trace, or -1 if this trace does not contain the element.
     */
    public <T> @Range(from = -1, to = Integer.MAX_VALUE) int indexOf(Node<T> node) {
        return nodes.indexOf(node);
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this trace, or -1 if this trace does not contain the element.
     */
    public <T> @Range(from = -1, to = Integer.MAX_VALUE) int lastIndexOf(Node<T> node) {
        return nodes.lastIndexOf(node);
    }

    public <T> boolean contains(Node<T> node) {
        return indexOf(node) != -1;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NodeTrace: [");
        boolean first = true;
        for (Node node : nodes) {
            if (first)
                builder.append(node);
            else builder.append(" -> ").append(node);
            first = false;
        }
        builder.append("] ").append("<").append(size()).append(">");
        return builder.toString();
    }


    @ApiStatus.Internal
    public static final class NodeParser {

        private final NodeTrace current;
        private final CommandData data;

        public NodeParser(NodeTrace current, CommandData data) {
            this.current = current;
            this.data = data;
        }

        @NotNull
        public <T> T parse(String name) throws NodeParserException {
            try {
                Node<T> node = current.get(name);
                return node.parse(data.cmd()[current.indexOf(node)], current);
            } catch (Exception exception) {
                throw new NodeParserException(exception, current, data, name, false);
            }
        }

        @NotNull
        public <T> T parse(int index) throws NodeParserException {
            try {
                Node<T> node = current.get(index);
                return node.parse(data.cmd()[current.indexOf(node)], current);
            } catch (Exception exception) {
                throw new NodeParserException(exception, current, data, index + "", true);
            }
        }

        @NotNull
        @ApiStatus.Internal
        public <T> T parse(@NotNull Supplier<String> name) throws NodeParserException {
            return parse(name.get());
        }

        @NotNull
        @ApiStatus.Internal
        public static class NodeParserException extends Exception {

            public final NodeTrace current;
            public final CommandData data;
            public final String queryString;
            public final boolean queryWasIndex;

            /**
             * Constructs a new exception with {@code null} as its detail message.
             * The cause is not initialized, and may subsequently be initialized by a
             * call to {@link #initCause}.
             *
             * @param current       the nodeTrace that the node should be in
             * @param data          the data that was used to parse the node
             * @param queryString   the query as string
             * @param queryWasIndex true when the query was an index
             */
            public NodeParserException(NodeTrace current, CommandData data, String queryString, boolean queryWasIndex) {
                this.current = current;
                this.data = data;
                this.queryString = queryString;
                this.queryWasIndex = queryWasIndex;
            }

            /**
             * Constructs a new exception with the specified detail message.  The
             * cause is not initialized, and may subsequently be initialized by
             * a call to {@link #initCause}.
             *
             * @param message       the detail message. The detail message is saved for
             *                      later retrieval by the {@link #getMessage()} method.
             * @param current       the nodeTrace that the node should be in
             * @param data          the data that was used to parse the node
             * @param queryString   the query as string
             * @param queryWasIndex true when the query was an index
             */
            public NodeParserException(@Nullable String message, NodeTrace current, CommandData data, String queryString, boolean queryWasIndex) {
                super(message);
                this.current = current;
                this.data = data;
                this.queryString = queryString;
                this.queryWasIndex = queryWasIndex;
            }

            /**
             * Constructs a new exception with the specified detail message and
             * cause.  <p>Note that the detail message associated with
             * {@code cause} is <i>not</i> automatically incorporated in
             * this exception's detail message.
             *
             * @param message       the detail message (which is saved for later retrieval
             *                      by the {@link #getMessage()} method).
             * @param cause         the cause (which is saved for later retrieval by the
             *                      {@link #getCause()} method).  (A {@code null} value is
             *                      permitted, and indicates that the cause is nonexistent or
             *                      unknown.)
             * @param current       the nodeTrace that the node should be in
             * @param data          the data that was used to parse the node
             * @param queryString   the query as string
             * @param queryWasIndex true when the query was an index
             * @since 1.4
             */
            public NodeParserException(@Nullable String message, Throwable cause, NodeTrace current, CommandData data, String queryString, boolean queryWasIndex) {
                super(message, cause);
                this.current = current;
                this.data = data;
                this.queryString = queryString;
                this.queryWasIndex = queryWasIndex;
            }

            /**
             * Constructs a new exception with the specified cause and a detail
             * message of {@code (cause==null ? null : cause.toString())} (which
             * typically contains the class and detail message of {@code cause}).
             * This constructor is useful for exceptions that are little more than
             * wrappers for other throwables (for example, {@link
             * PrivilegedActionException}).
             *
             * @param cause         the cause (which is saved for later retrieval by the
             *                      {@link #getCause()} method).  (A {@code null} value is
             *                      permitted, and indicates that the cause is nonexistent or
             *                      unknown.)
             * @param current       the nodeTrace that the node should be in
             * @param data          the data that was used to parse the node
             * @param queryString   the query as string
             * @param queryWasIndex true when the query was an index
             * @since 1.4
             */
            public NodeParserException(Throwable cause, NodeTrace current, CommandData data, String queryString, boolean queryWasIndex) {
                super(cause);
                this.current = current;
                this.data = data;
                this.queryString = queryString;
                this.queryWasIndex = queryWasIndex;
            }

            @Override
            public String getMessage() {
                if (super.getMessage() != null) {
                    return "Can´t parse Node! Query \"" + (queryWasIndex ? "index: " : "") + queryString + "\" failed on [" + current.nodes + "] with "
                            + Arrays.toString(data.cmd()) + "!";
                } else {
                    return super.getMessage();
                }
            }
        }
    }
}
