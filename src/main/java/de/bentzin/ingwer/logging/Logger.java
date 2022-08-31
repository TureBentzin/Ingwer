package de.bentzin.ingwer.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Logger {

    private String name;
    @Nullable
    private Logger parent = null;

    private boolean debug = false;


    public Logger(String name,@NotNull Logger parent) {
        this.name = name;
        this.parent = parent;
        debug("creating new logger: " + genName() + "!");
    }

    public Logger(String name) {
        this.name = name;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public abstract void log(String message, LogLevel logLevel);

    public void info(String message) {
        log(message,LogLevel.INFO);
    }

    public void waring(String message) {
        log(message,LogLevel.WARNING);
    }

    public void error(String message) {
        log(message,LogLevel.ERROR);
    }

    public void debug(String message) {
        if(isDebugEnabled()) log(message,LogLevel.DEBUG);
    }

    public void cosmetic(String message) {
        log(message,LogLevel.COSMETIC);
    }

    public enum LogLevel{
        INFO,
        WARNING,
        ERROR,
        COSMETIC,
        DEBUG,
    }

    public String prefix(String message) {
        return "[" + genName() + "]: " + message;
    }

    public String prefix(String message, @NotNull LogLevel logLevel) {
        return logLevel.name() +" >> " + prefix(message);
    }

    public Logger getParent() {
        return parent;
    }

    protected String genName() {
        if(parent != null)
         return parent.genName() +"/"+ name;
        else return  name;
    }

    public abstract Logger adopt(String name);
}
