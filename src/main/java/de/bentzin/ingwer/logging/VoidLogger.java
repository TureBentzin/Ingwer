package de.bentzin.ingwer.logging;

import org.jetbrains.annotations.NotNull;

public class VoidLogger extends Logger {
    public VoidLogger(String name, @NotNull Logger parent) {
        super(name, parent);
    }

    public VoidLogger(String name) {
        super(name);
    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        if (logLevel.equals(LogLevel.DEBUG) && isDebugEnabled()) {
            if (hasParent()) {
                assert getParent() != null;
                getParent().debug(".> " + logLevel);
            }
        }
        //void
    }

    @Override
    public Logger adopt(String name) {
        return new VoidLogger(name, this);
    }
}
