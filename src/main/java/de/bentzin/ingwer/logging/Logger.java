package de.bentzin.ingwer.logging;

import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.ForOverride;
import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import org.jetbrains.annotations.ApiStatus;
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
        //debug
        debug("creating new logger: " + genName() + "!");

        //notify parent:
        try {
            parent.notifyParent(this);
        }catch (Exception e){
            IngwerThrower.acceptS(e, ThrowType.LOGGING);
        }

    }

    public Logger(String name) {
        this.name = name;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return true when globalDebug is active or the local debug of this is active!
     */
    public final boolean isDebugEnabled() {
        if (Ingwer.isGlobalDebug()) return true;
        return debug;
    }

    /**
     * @return if it is allowed to further handle this message. true if it is and false if it is not
     * @implNote use like this {@code if(checkDebug(logLevel) return;} This can be used like a "isAllowedToPrint" method
     */
    @ApiStatus.Experimental
    protected final boolean checkDebug(LogLevel logLevel) {
        if (logLevel == LogLevel.DEBUG)
            return isDebugEnabled();
        return true;
    }

    /**
     * @param message  message to handle
     * @param logLevel level associated with the message
     * @implNote WARNING: Please check is {@link Logger#isDebugEnabled()} before handling debug messages (logLevel == {@link LogLevel#DEBUG})
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

    public final String getName() {
        return genName();
    }

    @ApiStatus.Internal
    public final String getLastName() {
        return name;
    }

    public abstract Logger adopt(String name);

    public enum LogLevel {
        INFO,
        WARNING,
        ERROR,
        COSMETIC,
        DEBUG,
    }

    //dynamic logging

    /**
     * This will be called by default if a Logger is created with a parent.
     * There is no guarantee that non-dynamic loggers will support this!
     * @param child the child
     */
    @DoNotCall
    @ForOverride
    public void notifyParent(Logger child) {

    }
}
