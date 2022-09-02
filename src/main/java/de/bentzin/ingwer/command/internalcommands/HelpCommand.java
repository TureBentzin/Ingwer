package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandManager;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;


public class HelpCommand extends IngwerCommand {
    public HelpCommand( IngwerCommandManager commandManager) {
        super("help","List all the available Commands to you. longer long dese long ah<dhasbgkguki");
        this.commandManager = commandManager;
    }

    private final IngwerCommandManager commandManager;

    @Contract("_ -> new")
    private @NotNull MultipageMessageKeeper helpMessage(@NotNull Identity identity) {
            return new MultipageMessageKeeper(Objects.requireNonNull(identity.getUUID()),
                    generate(Objects.requireNonNull(identity)), 8);
    }

    private @NotNull List<OneLinedMessage> generate(@NotNull Identity identity) {
        List<OneLinedMessage> oneLinedMessages = new ArrayList<>();
        for (IngwerCommand command : commandManager) {
            if(command instanceof Permissioned) {
                Permissioned permissioned = (Permissioned) command;
                if(permissioned.checkPermission(identity)){
                    oneLinedMessages.add(generateMessage(command));
                }
            }
            oneLinedMessages.add(generateMessage(command));
        }
        return oneLinedMessages;
    }

    //cmdName   : cmdDesc
    // <click:suggest_command:'+say'><hover:show_text:'<gray>Full Description'><gray>+say   : Say things stating with +</click></hover>
    @NotNull
    private OneLinedMessage generateMessage(@NotNull IngwerCommand ingwerCommand) {
        char prefix = Ingwer.getPreferences().prefix();
        return new MiniMessageMessage("<click:suggest_command:'" + prefix + ingwerCommand.getName() + "'>" +
                "<hover:show_text:'<gray>" +ingwerCommand.getDescription() + "'>" +
                "<gold>"+ prefix + ingwerCommand.getName() + "<dark_gray> » <gray>" +  trimDescription(ingwerCommand) +"</click>");
    }

    protected String trimDescription(@NotNull IngwerCommand ingwerCommand) {
        if(ingwerCommand.getDescription().length() > 40) {
            return ingwerCommand.getDescription().substring(0,35) + "...";
        }else
            return ingwerCommand.getDescription();

    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, @NotNull CommandTarget senderType) {
        if(senderType.equals(CommandTarget.INGAME)) {
            if(commandSender instanceof Identity) {
                Identity identity = (Identity) commandSender;
                if(identity.getUUID() != null) {
                    MultipageMessageKeeper multipageMessageKeeper = helpMessage(identity);
                    getLogger().info("send help to: " + identity.getName());
                    multipageMessageKeeper.send(1);
                }
            }
        }
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return CommandTarget.SAVE.fullfill();
    }
}
