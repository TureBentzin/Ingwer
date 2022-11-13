package de.bentzin.ingwer.logging;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.logging.log4j.LogManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 13.11.2022
 */


/*
<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><gray>Cosmetic Text
<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><green>INFO: </green><gray>This is an Information!
<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><yellow>WARN: </yellow><color:#ffcf21>This is a Warning!
<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><dark_red>ERROR: </dark_red><red>This is an Error!
<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><blue>DEBUG: </blue><gray>This is debug text <CraftPlayer:28>
 */

public class CommandSenderLogger extends Logger{

    private final Supplier<CommandSender[]> commandSenders;

    public CommandSenderLogger(String name, @NotNull Logger parent,@NotNull CommandSender @NotNull ... commandSenders) {
        super(name, parent);
        this.commandSenders = () -> commandSenders;
    }

    public CommandSenderLogger(String name, @NotNull CommandSender @NotNull ... commandSenders) {
        super(name);
        this.commandSenders = () -> commandSenders;
    }

    public CommandSenderLogger(String name, @NotNull Logger parent,@NotNull Supplier<CommandSender[]> commandSenders) {
        super(name, parent);
        this.commandSenders = commandSenders;
    }

    public CommandSenderLogger(String name, @NotNull Supplier<CommandSender[]> commandSenders) {
        super(name);
        this.commandSenders = commandSenders;
    }

    /**
     * @param message  message to handle
     * @param logLevel level associated with the message
     * @implNote WARNING: Please check is {@link Logger#isDebugEnabled()} before handling debug messages (logLevel == {@link LogLevel#DEBUG})
     */
    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        for (CommandSender commandSender : getCommandSenders()) {
            logForSender(message,logLevel,commandSender);
        }
    }

    @ApiStatus.Internal
    private void logForSender(String message, @NotNull LogLevel v1, CommandSender v2) {
        Objects.requireNonNull(v2); Objects.requireNonNull(v1);

        //DEBUG:
        logOneLine(message,v1,v2);

    }

    @ApiStatus.Internal
    private void logOneLine(String message, @NotNull LogLevel logLevel, CommandSender commandSender){
                /*
                Important:
                A Use of Ingwer Messaging is not allowed here to deny recursive calls.
                This is hardcoded to a Theme that can not be changed!
                 */
        switch (logLevel){
            case INFO -> commandSender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><green>INFO: </green><gray>" + message));
            case WARNING -> commandSender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><yellow>WARN: </yellow><color:#ffcf21>" + message));
            case ERROR -> commandSender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><dark_red>ERROR: </dark_red><red>" + message));
            case DEBUG -> commandSender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><blue>DEBUG: </blue><gray>" + message));
            case COSMETIC -> commandSender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<dark_green>[<aqua>Log</aqua>]<dark_gray>: </dark_green><gray>" + message));
        }
    }

    public CommandSender[] getCommandSenders() {
        return commandSenders.get();
    }

    @Override
    public Logger adopt(String name) {
        return null;
    }
}
