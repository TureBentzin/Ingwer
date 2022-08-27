package de.bentzin.ingwer.logging;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import org.jetbrains.annotations.NotNull;

public class SystemLogger extends Logger{


    public SystemLogger(String name) {
        super(name);
    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        switch (logLevel) {

            case INFO, DEBUG, WARING -> {
                System.out.println(prefix(message,logLevel));
            }
            case ERROR -> {
                System.err.println(prefix(message,logLevel));
            }
            case COSMETIC -> {
                System.out.println(prefix(message));
            }
            default -> IngwerThrower.accept(new IllegalStateException("Unexpected value: " + logLevel), ThrowType.LOGGING);
        }
    }
}
