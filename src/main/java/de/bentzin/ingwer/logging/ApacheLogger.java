package de.bentzin.ingwer.logging;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

public class ApacheLogger extends Logger {

    @Override
    public void debug(String message) {
        log(message,LogLevel.DEBUG);
    }

    private org.apache.logging.log4j.Logger logger;

    public ApacheLogger(String name, @NotNull Logger parent, org.apache.logging.log4j.Logger logger) {
        super(name, parent);
        this.logger = logger;
    }

    public ApacheLogger(String name, org.apache.logging.log4j.Logger logger) {
        super(name);
        this.logger = logger;
    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        if(logger == null) {
            LogManager.getRootLogger().info(prefix(message,logLevel));
            return;
        }
        switch (logLevel){

            case INFO -> {
                logger.info(prefix(message,logLevel));
            }
            case WARNING -> {
                logger.warn(prefix(message));
            }
            case ERROR -> {
                logger.error(prefix(message));
            }
            case COSMETIC -> {
                logger.info(message);
            }
            case DEBUG -> {
                if(logger.isDebugEnabled())
                     logger.debug(prefix(message,logLevel));
                else if (isDebugEnabled()) {
                    logger.info(prefix(message,logLevel));
                }
            }
        }
    }

    @Override
    public Logger adopt(String name) {
        return new ApacheLogger(name,this,logger);
    }
}
