package de.bentzin.ingwer.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CollectionUtils {

    @Independent
    @Contract("_, _ -> param1")
    public static <E,C extends Collection<E>> @NotNull C flipFlop(@NotNull C collection, E element) {
        if(collection.contains(element)) {
            collection.remove(element);
        }else {
            collection.add(element);
        }
        return collection;
    }
    
}
