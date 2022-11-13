package de.bentzin.ingwer.command.node;

import java.util.function.Predicate;

/**
 * @author Ture Bentzin
 * 13.11.2022
 */
public abstract class EnumNameNode<E extends Enum<E>> extends EnumNode<E> {
    public EnumNameNode(String name, Class<E> enumClass) {
        super(name, Enum::name, enumClass);
    }

    public EnumNameNode(String name, Class<E> enumClass, Predicate<E> filter) {
        super(name, Enum::name, enumClass, filter);
    }
}
