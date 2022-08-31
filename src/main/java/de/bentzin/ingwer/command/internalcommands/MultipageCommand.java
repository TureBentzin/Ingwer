package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.message.IngwerMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class MultipageCommand extends IngwerCommand {
    public MultipageCommand() {
        super("page", "for internal purposes");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {


        IngwerMessage.easyFormat("hello!").clickEvent(ClickEvent.runCommand("hello"));
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[0];
    }

}
