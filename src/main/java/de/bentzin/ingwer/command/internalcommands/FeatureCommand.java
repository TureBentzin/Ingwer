package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.features.Feature;
import de.bentzin.ingwer.features.FeatureManager;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @see HelpCommand
 */
public class FeatureCommand extends IngwerCommand implements Permissioned {
    private final FeatureManager featureManager;

    public FeatureCommand(FeatureManager featureManager) {
        super("features", "List all the loaded features.");
        this.featureManager = featureManager;
    }

    @Contract("_ -> new")
    private @NotNull MultipageMessageKeeper helpMessage(@NotNull Identity identity) {
        return new MultipageMessageKeeper(Objects.requireNonNull(identity.getUUID()),
                generate(Objects.requireNonNull(identity)), 8);
    }

    private @NotNull List<OneLinedMessage> generate(@NotNull Identity identity) {
        List<OneLinedMessage> oneLinedMessages = new ArrayList<>();
        for (Feature feature : featureManager) {
            oneLinedMessages.add(generateMessage(feature));
        }
        return oneLinedMessages;
    }

    @NotNull
    private OneLinedMessage generateMessage(@NotNull Feature feature) {
        return new MiniMessageMessage(
                "<gold>" + feature.getName() + "<dark_gray> » <gray>" +  "<hover:show_text:'<gray>" + feature.getDescription() + "'>" +  trimDescription(feature));
    }

    protected String trimDescription(@NotNull Feature feature) {
        if (feature.getDescription().length() > 40) {
            return feature.getDescription().substring(0, 35) + "...";
        } else
            return feature.getDescription();

    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, @NotNull CommandTarget senderType) {
        if (senderType.equals(CommandTarget.INGAME)) {
            if (commandSender instanceof Identity) {
                Identity identity = (Identity) commandSender;
                if (identity.getUUID() != null) {
                    if (cmd.length == 2) {
                        if (cmd[1].equals("raw")) {
                            MessageBuilder.prefixed().add(generate(identity).stream().map(OneLinedMessage::getOneLinedString).toList().toString()).build().send(identity);

                        }
                    }
                    MultipageMessageKeeper multipageMessageKeeper = helpMessage(identity);
                    multipageMessageKeeper.send(1);
                }
            }
        }
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return CommandTarget.SAVE.fullfill();
    }

    @Override
    public IngwerPermission getPermission() {
        return IngwerPermission.SUPERADMIN;
    }
}
