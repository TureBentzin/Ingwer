package de.bentzin.ingwer.logging;

import de.bentzin.ingwer.Ingwer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class Logger {

    private final String name;
    @Nullable
    private Logger parent = null;

    private boolean debug = false;


    public Logger(String name, @NotNull Logger parent) {
        this.name = name;
        this.parent = parent;
        debug = this.parent.isDebugEnabled();
        //debug
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

    /**
     * @implNote use like this {@code if(checkDebug(logLevel) return;} This can be used like a "isAllowedToPrint" method
     * @return if it is allowed to further handle this message. true if it is and false if it is not
     */
    @ApiStatus.Experimental
    protected final boolean checkDebug(LogLevel logLevel) {
        if(logLevel == LogLevel.DEBUG)
            return isDebugEnabled();
        return true;
    }

    /**
     * @implNote WARNING: Please check is {@link Logger#isDebugEnabled()} before handling debug messages (logLevel == {@link LogLevel#DEBUG})
     * @param message message to handle
     * @param logLevel level associated with the message
     */
    public abstract void log(String message, @NotNull LogLevel logLevel);

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
    } //check is only here for fail safety and to avoid redundant calls

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
