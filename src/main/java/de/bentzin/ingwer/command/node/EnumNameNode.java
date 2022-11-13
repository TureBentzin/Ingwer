package de.bentzin.ingwer.command.node;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 13.11.2022
 */
public abstract class EnumNameNode<E extends Enum<E>> extends EnumNode<E>{
    public EnumNameNode(String name, Supplier<Collection<E>> supplier) {
        super(name, supplier, Enum::name);
    }

    public EnumNameNode(String name, Collection<E> collection) {
        super(name, collection, Enum::name);
    }
}
