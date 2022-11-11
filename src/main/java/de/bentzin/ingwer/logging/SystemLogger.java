package de.bentzin.ingwer.logging;

import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import org.jetbrains.annotations.NotNull;

public class SystemLogger extends Logger {


    public SystemLogger(String name, @NotNull Logger parent) {
        super(name, parent);
    }

    public SystemLogger(String name) {
        super(name);
    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        if (checkDebug(logLevel))
            switch (logLevel) {

                case INFO, DEBUG, WARNING -> System.out.println(prefix(message, logLevel));
                case ERROR -> System.err.println(prefix(message, logLevel));
                case COSMETIC -> System.out.println(message);
                default ->
                        IngwerThrower.acceptS(new IllegalStateException("Unexpected value: " + logLevel), ThrowType.LOGGING);
            }
    }

    @Override
    public Logger adopt(String name) {
        return new SystemLogger(name, this);
    }
}
