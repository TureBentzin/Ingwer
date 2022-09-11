package de.bentzin.ingwer.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    @Independent
    @Contract("_, _ -> param1")
    public static <E, C extends Collection<E>> @NotNull C flipFlop(@NotNull C collection, E element) {
        if (collection.contains(element)) {
            collection.remove(element);
        } else {
            collection.add(element);
        }
        return collection;
    }

    /**
     * @param consumer boolean is getting populated with "true" if element was added to collection and with "false" when otherwise
     * @param <E>      element
     * @param <C>      collection
     * @return changed collection
     */
    @Independent
    public static <E, C extends Collection<E>> @NotNull C flipFlop(@NotNull C collection, E element, Consumer<Boolean> consumer) {
        if (collection.contains(element)) {
            collection.remove(element);
            consumer.accept(false);
        } else {
            consumer.accept(collection.add(element));
        }
        return collection;
    }

}
