package de.bentzin.ingwer.identity.permissions;

import de.bentzin.ingwer.Ingwer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class IngwerPermissions extends ArrayList<IngwerPermission> {

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *                                  is negative
     */
    public IngwerPermissions(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public IngwerPermissions() {
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public IngwerPermissions(@NotNull Collection<? extends IngwerPermission> c) {
        super(c);
    }

    public IngwerPermissions(@NotNull IngwerPermission @NotNull ... permissions) {
        super();
        this.addAll(Arrays.asList(permissions));
    }
}
