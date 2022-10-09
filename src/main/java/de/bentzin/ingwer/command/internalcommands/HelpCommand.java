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
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.thrower.IngwerException;
import de.bentzin.ingwer.thrower.ThrowType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HelpCommand extends IngwerCommand {
    private final IngwerCommandManager commandManager;

    public HelpCommand(IngwerCommandManager commandManager) {
        super("help", "List all the available Commands to you.");
        this.commandManager = commandManager;
    }

    @Contract("_ -> new")
    private @NotNull MultipageMessageKeeper helpMessage(@NotNull Identity identity) {
        return new MultipageMessageKeeper(Objects.requireNonNull(identity.getUUID()),
                generate(Objects.requireNonNull(identity)), 8,true);
    }

    private @NotNull List<OneLinedMessage> generate(@NotNull Identity identity) {
        List<OneLinedMessage> oneLinedMessages = new ArrayList<>();
        for (IngwerCommand command : commandManager) {
            if (command instanceof Permissioned permissioned) {
                if (permissioned.checkPermission(identity)) {
                    oneLinedMessages.add(generateMessage(command));

                }
            } else
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
                "<gold>" + prefix + ingwerCommand.getName() + "<hover:show_text:'<gray>" + ingwerCommand.getDescription() + "'>" + "<dark_gray> » <gray>" + trimDescription(ingwerCommand) + "</click>");
    }

    protected String trimDescription(@NotNull IngwerCommand ingwerCommand) {
        int length = ingwerCommand.getDescription().length() + ingwerCommand.getName().length();
        int max = 47;
        int sub = 5;
        int cut = (max - sub) - ingwerCommand.getName().length();
        if (cut < 0) {
            cut = 0;
        }
        //failsave
        if (cut > ingwerCommand.getDescription().length()) {
            cut = ingwerCommand.getDescription().length();
        }
        try {
            if (length > max) {
                return ingwerCommand.getDescription().substring(0, cut) + "...";
            } else
                return ingwerCommand.getDescription();

        } catch (Exception e) {
            throw new IngwerException("text: " + ingwerCommand.getDescription() + " --- length: " + ingwerCommand.getDescription().length(), e, ThrowType.GENERAL);
        }
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, @NotNull CommandTarget senderType) {
        if (senderType.equals(CommandTarget.INGAME)) {
            if (commandSender instanceof Identity identity) {
                if (identity.getUUID() != null) {
                    if (cmd.length == 2) {
                        if (cmd[1].equals("raw")) {
                            MessageBuilder.prefixed().add(generate(identity).stream().map(OneLinedMessage::getOneLinedString).toList().toString()).build().send(identity);

                        }
                    }
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
