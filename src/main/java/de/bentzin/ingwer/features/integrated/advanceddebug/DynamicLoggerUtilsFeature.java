package de.bentzin.ingwer.features.integrated.advanceddebug;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.dynamic.DynamicLoggerContainer;
import de.bentzin.ingwer.message.FramedMessage;
import de.bentzin.ingwer.message.MessageLike;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ture Bentzin
 * 13.11.2022
 */
public class DynamicLoggerUtilsFeature extends SimpleFeature {
    public DynamicLoggerUtilsFeature() {
        super("DynamicLoggerUtils","This Feature is active in the case that a DynamicLoggerContainer is used for Ingwer Logging.");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.ADMIN;
    }

    @Override
    public void onEnable() {
        getLogger().info(getClass().getSimpleName() + " enabled because of a DynamicLoggerContainer is set in Ingwers Main Logger");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return Ingwer.getLogger() instanceof DynamicLoggerContainer;
    }


    public class SwitchLoggerCommand extends IngwerNodeCommand {
        public static final String COMMAND_DESCRIPTION = "This command provides you with a debug and test option to switch between some pre-defined loggers for Ingwer!";
        public SwitchLoggerCommand() {
            super(CommandTarget.SAVE.fullfill(), "switchLogger", COMMAND_DESCRIPTION,
                    (data, nodeTrace) -> {
                        Logger main = Ingwer.getLogger();
                        ArrayList<OneLinedMessage> oneLinedMessages = new ArrayList<>();
                        oneLinedMessages.add(MessageBuilder.empty().add(COMMAND_DESCRIPTION).build());
                        oneLinedMessages.add(MessageBuilder.empty().add(C.C, "Current Logger:")
                                .add(C.A, main.getName() + " ").add(C.C, "Instance: ").add(C.A, main.getClass().getSimpleName()).build());
                        oneLinedMessages.add(MessageBuilder.empty().add(C.C, "debug: ").add(C.A, (main.isDebugEnabled()) ? "<green>enabled" : "<red>disabled").build());
                        new FramedMessage(oneLinedMessages).send(data.commandSender());
                    });


        }

    }
}
