package de.bentzin.ingwer.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FinalCompletableOptional<E> extends CompletableOptional<E> {


    public FinalCompletableOptional() {
    }

    public FinalCompletableOptional(E initialValue) {
        super(initialValue);
    }

    @Override
    public @NotNull FinalCompletableOptional<E> complete(@Nullable E value) {
        if (isSet()) {
            throw new IllegalStateException("FinalCompletableOptional was already completed!");
        } else
            return (FinalCompletableOptional<E>) super.complete(value);
    }
}
