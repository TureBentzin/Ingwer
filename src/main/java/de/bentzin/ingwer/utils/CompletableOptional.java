package de.bentzin.ingwer.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CompletableOptional<E> {


    @Contract(" -> new")
    public static @NotNull CompletableOptional empty() {
        return new CompletableOptional();
    }

    /**
     *
     * @param type only for Type Reference will be ignored
     * @return new {@link CompletableOptional<T>}
     * @param <T> type reference
     */
    @ApiStatus.Experimental
    public static @NotNull <T> CompletableOptional<T> empty(T type) {
        return new CompletableOptional<T>();
    }

    /**
     *
     * @param initialValue initialValue
     * @return new {@link CompletableOptional<T>}
     * @param <T> type reference
     */
    public static @NotNull <T> CompletableOptional<T> create(T initialValue) {
        return new CompletableOptional<T>(initialValue);
    }


    private E value = null;
    private boolean set = false;
    private final Collection<Consumer<E>> consumerCollection = new ArrayList<>();
    private final Collection<Thread> interrupted = new ArrayList<>();

    public final boolean isSet() {
        return set;
    }

    public final boolean isEmpty() {
        return !isSet();
    }

    @NotNull
    @ApiStatus.Internal
    protected final E getValue() {
        return value;
    }

    public CompletableOptional() {

    }

    public CompletableOptional(@Nullable E initialValue) {
        complete(initialValue);
    }

    @NotNull
    public CompletableOptional<E> complete(@Nullable E value) {
        this.value = value;
        set = true;
        consumerCollection.forEach(eConsumer -> {
            eConsumer.accept(value);
        });

        for (Thread thread : interrupted) {
            notify();
        }

        return this;
    }

    @Nullable
    public final E getOrNull() {
        if(set)
            return value;
        return null;
    }

    @NotNull
    public final E getOrThrow() {
        if(set)
            return value;
        throw new NoSuchElementException("Value is not present!");
    }

    @NotNull
    public final CompletableOptional<E> onComplete(@NotNull Consumer<E> consumer) {
        consumerCollection.add(Objects.requireNonNull(consumer));
        return this;
    }

    @NotNull
    public final E await() {
        CompletableFuture<String > stringCompletableFuture = new CompletableFuture<>();
        interrupted.add(Thread.currentThread());
        Thread.currentThread().interrupt();
        interrupted.remove(Thread.currentThread());
        return getOrThrow();
    }


}
