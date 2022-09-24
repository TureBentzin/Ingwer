package de.bentzin.ingwer.message.builder;

import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.utils.Hardcode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Hardcode
public enum C {
    /**
     * Color
     */
    C(IngwerMessage.COLOR_MM, IngwerMessage.COLOR_MM_C),
    /**
     * Accent
     */
    A(IngwerMessage.ACCENT_MM, IngwerMessage.ACCENT_MM_C),

    /**
     * Error
     */
    E(IngwerMessage.ERROR_MM, IngwerMessage.ERROR_MM_C),
    ;
    private final String open;
    private final String close;

    C(String open, String close) {
        this.open = open;

        this.close = close;
    }

    @Contract(pure = true)
    @NotNull String insert(String message) {
        return open + message + close;
    }
}
