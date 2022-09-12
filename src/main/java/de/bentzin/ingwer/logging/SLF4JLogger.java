package de.bentzin.ingwer.logging;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class SLF4JLogger extends Logger {


    private final org.slf4j.Logger logger;

    public SLF4JLogger(String name, @NotNull Logger parent, org.slf4j.Logger logger) {
        super(name, parent);
        this.logger = logger;
    }

    public SLF4JLogger(String name, org.slf4j.Logger logger) {
        super(name);
        this.logger = logger;

    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        if (logger == null) {
            Bukkit.getServer().getLogger().info(prefix(message, logLevel));
            return;
        }
        switch (logLevel) {

            case INFO, DEBUG, ERROR -> logger.info(prefix(message, logLevel));
            case WARNING -> logger.warn(prefix(message));
            case COSMETIC -> logger.info(message);
        }
    }

    @Override
    public Logger adopt(String name) {
        return new SLF4JLogger(name, this, logger);
    }
}
