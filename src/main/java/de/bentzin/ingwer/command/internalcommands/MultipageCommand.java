package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.message.*;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultipageCommand extends IngwerCommand {
    public MultipageCommand() {
        super("page", "for internal purposes");
        for (int i = 0; i < 44; i++) {
            oneLinedMessageList.add(new StraightLineStringMessage(i + ": Test         -  TEST"));
        }
    }
    private List<OneLinedMessage> oneLinedMessageList = new ArrayList<>();

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {

        ArrayList<FramedMessage> generate;
      //  generate = new FramedMultipageMessageGenerator(oneLinedMessageList).generate(10,
       //         integer -> generate.get(integer).send(commandSender));
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[0];
    }

}
