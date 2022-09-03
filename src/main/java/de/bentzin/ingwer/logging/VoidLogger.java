package de.bentzin.ingwer.logging;

import org.jetbrains.annotations.NotNull;

public class VoidLogger extends Logger{
    public VoidLogger(String name, @NotNull Logger parent) {
        super(name, parent);
    }

    public VoidLogger(String name) {
        super(name);
    }

    @Override
    public void log(String message, LogLevel logLevel) {
        //void
    }

    @Override
    public Logger adopt(String name) {
        return new VoidLogger(name,this);
    }
}
