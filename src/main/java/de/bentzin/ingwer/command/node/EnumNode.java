package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.node.preset.CollectionNode;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Ture Bentzin
 * 13.11.202
 * @see EnumNameNode
 */
public abstract class EnumNode<E extends Enum<E>> extends CollectionNode<E> {

    public EnumNode(String name, Function<E, String> converter, @NotNull Class<E> enumClass, Predicate<E> filter) {
        super(name, () -> Arrays.stream(enumClass.getEnumConstants()).filter(filter).toList(), converter);
    }

    public EnumNode(String name, Function<E, String> converter, @NotNull Class<E> enumClass) {
        super(name, () -> Arrays.stream(enumClass.getEnumConstants()).toList(), converter);
    }
}
