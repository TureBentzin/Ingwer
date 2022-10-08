package de.bentzin.ingwer.utils;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LoggingClass implements Logging {
    private Logger logger;

    public LoggingClass(@Nullable Logger logger) {
        this.logger = Objects.requireNonNullElseGet(logger, Ingwer::getNullLogger);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * @implNote Override and execute super if you want to enable people to update the logger your class is using
     * You may also use this to set your logger if you set null in the constructor
     * @param logger new logger
     */
    protected void updateLogger(Logger logger) {
        this.logger = logger;
    }
}
