package de.bentzin.ingwer.logging;

import de.bentzin.ingwer.Ingwer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Logger {

    private final String name;
    @Nullable
    private Logger parent = null;

    private boolean debug = false;


    public Logger(String name, @NotNull Logger parent) {
        this.name = name;
        this.parent = parent;
        debug = this.parent.isDebugEnabled();
        debug("creating new logger: " + genName() + "!");
    }

    public Logger(String name) {
        this.name = name;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     *
     * @return true when globalDebug is active or the local debug of this is active!
     */
    public final boolean isDebugEnabled() {
        if(Ingwer.isGlobalDebug()) return true;
        return debug;
    }

    public abstract void log(String message, LogLevel logLevel);

    public void info(String message) {
        log(message, LogLevel.INFO);
    }

    public void warning(String message) {
        log(message, LogLevel.WARNING);
    }

    public void error(String message) {
        log(message, LogLevel.ERROR);
    }

    public void debug(String message) {
        if (isDebugEnabled()) log(message, LogLevel.DEBUG);
    }

    public void cosmetic(String message) {
        log(message, LogLevel.COSMETIC);
    }

    public String prefix(String message) {
        return "[" + genName() + "]: " + message;
    }

    public String prefix(String message, @NotNull LogLevel logLevel) {
        return logLevel.name() + " >> " + prefix(message);
    }

    public @Nullable Logger getParent() {
        return parent;
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    protected String genName() {
        if (parent != null)
            return parent.genName() + "/" + name;
        else return name;
    }

    public abstract Logger adopt(String name);

    public enum LogLevel {
        INFO,
        WARNING,
        ERROR,
        COSMETIC,
        DEBUG,
    }
}
