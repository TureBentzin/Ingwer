package de.bentzin.ingwer.tests;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TempReturnCommandSystem {

    /**
     *
     * @param action
     * @return the command
     */
    @Contract(pure = true)
    public static @NotNull String addReturn(Runnable action) {
        return "/command";
    }
}
