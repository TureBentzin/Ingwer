package de.bentzin.ingwer.thow;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ThrowType {


    GENERAL(),
    STORAGE("An error has occurred handling Ingwer Storage!"),
    LOGGING("An error has occurred within Ingwers logging! Feel free to report this issue!"),
    FEATURE("An error has occurred within Ingwers feature system! This my not be an official Ingwer issue!"),
    COMMAND("Error while executing IngwerCommand procedures!")

    ;
    private String message;

    public boolean hasMessage() {
        return message != null;
    }

    @Contract(pure = true)
    public @NotNull String getMessage() {
        if(message == null) {
            return "";
        }
        return message +  " <> Running Ingwer v." + de.bentzin.ingwer.Ingwer.VERSION_STRING + "!";
    }

    ThrowType(String message) {
        this.message = message;
    }
    ThrowType() {
        this.message = null;
    }
}
