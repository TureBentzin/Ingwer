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
     * Color
     */
    A(IngwerMessage.ACCENT_MM, IngwerMessage.ACCENT_MM_C),
    ;
    private final String open;
    private final String close;

    @Contract(pure = true)
    protected @NotNull String insert(String message) {
        return open + message + close;
    }

    C(String open, String close) {
        this.open = open;

        this.close = close;
    }
}
