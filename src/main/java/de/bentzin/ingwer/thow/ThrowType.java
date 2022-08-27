package de.bentzin.ingwer.thow;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ThrowType {


    GENERAL(),
    STORAGE("An error has occurred handling Ingwer Storage!"),
    LOGGING("An error has occurred within Ingwers logging! Feel free to report this issue!")

    ;
    private String message;

    @Contract(pure = true)
    public @NotNull String getMessage() {
        return message +  " <> Running Ingwer v." + de.bentzin.ingwer.Ingwer.VERSION_STRING + "!";
    }

    ThrowType(String message) {
        this.message = message;
    }
    ThrowType() {
        this.message = "An unknown error has occurred!";
    }
}
