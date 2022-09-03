package de.bentzin.ingwer.logging;

import org.jetbrains.annotations.NotNull;

public class Log4JLogger extends Logger{

    private final org.apache.logging.log4j.Logger logger;

    public Log4JLogger(String name, @NotNull Logger parent, org.apache.logging.log4j.Logger logger) {
        super(name, parent);
        this.logger = logger;
    }

    public Log4JLogger(String name, org.apache.logging.log4j.Logger logger) {
        super(name);
        this.logger = logger;
    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {

        switch (logLevel){

            case INFO, DEBUG -> {
                logger.info(prefix(message,logLevel));
            }
            case WARNING -> {
                logger.warn(prefix(message));
            }
            case COSMETIC -> {
                logger.info(message);
            }
            case ERROR -> {
                logger.error(prefix(message));
            }
        }
    }

    @Override
    public Logger adopt(String name) {
        return new Log4JLogger(name,this,logger);
    }
}
