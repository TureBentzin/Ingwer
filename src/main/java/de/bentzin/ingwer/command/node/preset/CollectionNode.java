package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.AbstractNode;
import de.bentzin.ingwer.command.node.CommandNode;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.utils.CompletableOptional;
import org.checkerframework.checker.optional.qual.MaybePresent;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CollectionNode<E> extends AbstractNode<E> {

    private final Supplier<Collection<E>> collectionSupplier;
    private final Function<E, String> converter;

    private final CompletableOptional<CommandNode> commandNode = new CompletableOptional<>();

    public CollectionNode(String name, Supplier<Collection<E>> supplier, Function<E,String> converter) {
        super(name);
        this.collectionSupplier = supplier;
        this.converter = converter;
    }

    public CollectionNode(String name, Collection<E> collection, Function<E,String> converter) {
        super(name);
        this.collectionSupplier = () -> collection;
        this.converter = converter;
    }

    /**
     * @param input     the current argument
     * @param nodeTrace all nodes before this
     * @return type object parsed out of the current argument
     * @throws InvalidParameterException if input could not be parsed
     */
    @Override
    public @NotNull E parse(@NotNull String input, @NotNull NodeTrace nodeTrace) throws InvalidParameterException {
        AtomicReference<E> value = new AtomicReference<>(null);
        Collection<E> collection = collectionSupplier.get();
        collection.forEach(element -> {
            if(input.equals(converter.apply(element))) {
                if(value.get() != null) {
                    throw new InvalidParameterException("input: \"" + input + "\" matches to more then one collection member!");
                }
                value.set(element);
            }
        });
        if(value.get() != null) {
            return value.get();
        }else {
            throw new InvalidParameterException("input: \"" + input + "\" cant be matched to a collection member!");
        }
    }

    /**
     * @return a collection of all values the Node should react to. This may only contain one value!
     * @implNote values needs to contain at least one String
     */
    @Override
    public Collection<String> values() {
        return collectionSupplier.get().stream().map(converter).toList();
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
    public void initialize(@NotNull CommandNode commandNode) {
        this.commandNode.complete(commandNode);
    }

}
