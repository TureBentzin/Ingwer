package de.bentzin.ingwer.logging;

import org.bukkit.Bukkit;

/**
 * @author Ture Bentzin
 * 13.11.2022
 */
public class ConsoleCommandSenderLogger extends CommandSenderLogger {

    public ConsoleCommandSenderLogger(String name, Logger parent) {
        super(name, parent, Bukkit.getConsoleSender());
    }

    public ConsoleCommandSenderLogger(String name) {
        super(name, Bukkit.getConsoleSender());
    }
}
