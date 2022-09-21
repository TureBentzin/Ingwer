package de.bentzin.ingwer.logging.ingwerconsole;

import de.bentzin.ingwer.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class IngwerConsole extends Logger {
    public IngwerConsole(String name, @NotNull Logger parent) {
        super(name, parent);
    }

    public IngwerConsole(String name) {
        super(name);
    }

    @Override
    public void log(String message, LogLevel logLevel) {

    }

    @Override
    public Logger adopt(String name) {
        return null;
    }
}
