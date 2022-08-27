package de.bentzin.ingwer.logging;

import org.jetbrains.annotations.NotNull;

public abstract class Logger {

    private String name;


    public Logger(String name) {
        this.name = name;
    }

    public abstract void log(String message, LogLevel logLevel);

    public void info(String message) {
        log(message,LogLevel.INFO);
    }

    public void waring(String message) {
        log(message,LogLevel.WARING);
    }

    public void error(String message) {
        log(message,LogLevel.ERROR);
    }

    public void debug(String message) {
        log(message,LogLevel.DEBUG);
    }

    public void cosmetic(String message) {
        log(message,LogLevel.COSMETIC);
    }

    public enum LogLevel{
        INFO,
        WARING,
        ERROR,
        COSMETIC,
        DEBUG,
    }

    public String prefix(String message) {
        return "[" + name + "]: " + message;
    }

    public String prefix(String message, @NotNull LogLevel logLevel) {
        return logLevel.name() +" >> " + prefix(message);
    }
}
