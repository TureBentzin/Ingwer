package de.bentzin.ingwer.utils;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class BooleanUtils {

    private BooleanUtils() {
    }

    public static boolean flip(boolean b, @NotNull Consumer<Boolean> action) {
        action.accept(!b);
        b = !b;
        return b;
    }

    public static boolean flip(boolean b, Runnable onFalse, Runnable onTrue) {
        return flip(b, bool -> {
            if (bool) onTrue.run();
            else onFalse.run();
        });
    }
}
