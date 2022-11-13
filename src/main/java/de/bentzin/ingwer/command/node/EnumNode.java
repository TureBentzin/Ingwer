package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.preset.CollectionNode;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 13.11.202
 * @see EnumNameNode
 */
public abstract class EnumNode<E extends Enum<E>> extends CollectionNode<E> {

    public EnumNode(String name, Supplier<Collection<E>> supplier, Function<E, String> converter) {
        super(name, supplier, converter);
    }

    public EnumNode(String name, Collection<E> collection, Function<E, String> converter) {
        super(name, collection, converter);
    }
}
