package de.bentzin.ingwer.features.test;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;

public class MulipageTestCommand extends IngwerCommand {
    public MulipageTestCommand() {
        super("mulipage", "sends a multipage message");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {

    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[0];
    }
}
