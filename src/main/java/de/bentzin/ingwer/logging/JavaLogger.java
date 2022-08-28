package de.bentzin.ingwer.logging;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class JavaLogger extends Logger{


    @NotNull
    private final java.util.logging.Logger logger;

    public JavaLogger(String name, @NotNull Logger parent, @NotNull java.util.logging.Logger logger) {
        super(name,parent);
        this.logger = logger;
    }

    public JavaLogger(String name, @NotNull java.util.logging.Logger logger ){
        super(name);
        this.logger = logger;

    }

    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        if(logger == null) {
            Bukkit.getServer().getLogger().info(prefix(message,logLevel));
            return;
        }
        switch (logLevel){

            case INFO, DEBUG -> {
                logger.info(prefix(message,logLevel));
            }
            case WARNING -> {
                logger.warning(prefix(message));
            }
            case ERROR -> {
                logger.log(Level.FINEST, prefix(message,logLevel));
            }
            case COSMETIC -> {
                logger.info(message);
            }
        }
    }

    @Override
    public Logger adopt(String name) {
        return new JavaLogger(name,this, logger);
    }
}
