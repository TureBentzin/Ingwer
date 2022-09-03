package de.bentzin.ingwer.utils;

import de.bentzin.ingwer.logging.Logger;

public class LoggingClass implements Logging {
    private final Logger logger;

    public LoggingClass(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
