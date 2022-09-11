package de.bentzin.ingwer.features.test;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;

import java.util.ArrayList;

public class MulipageTestCommand extends IngwerCommand {
    private final ArrayList<OneLinedMessage> oneLinedMessages = new ArrayList<>();

    public MulipageTestCommand() {
        super("pages", "Sends a multipage message");
        for (int i = 0; i < 44; i++) {
            oneLinedMessages.add(new MiniMessageMessage("<gray>" + i + ": Welcome to Ingwer!"));
        }
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {

        if (commandSender instanceof Identity) {
            Identity identity = (Identity) commandSender;
            MultipageMessageKeeper multipageMessageKeeper = new MultipageMessageKeeper(identity.getUUID(), oneLinedMessages, 8);
            multipageMessageKeeper.send(1);
        }

    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.INGAME};
    }
}
