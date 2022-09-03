package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChatCommand extends IngwerCommand {

    public static MessageBuilder chatMessageBuilder(@NotNull IngwerCommandSender sender){
        return MessageBuilder.empty().add(" [<gradient:yellow:aqua>Chat</gradient>]: ").add(C.A, "@" + sender.getName() +": ").c();
    }

    //<gray>[<gradient:light_purple:blue>Ingwer<gray>]: [<gradient:yellow:aqua>Chat</gradient>]: <gold>@TDR_Minecraft: <gray>Hello i am using Ingwer Chat.

    public ChatCommand() {
        super("chat","Write messages to your fellow Ingwer users currently available.");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String @NotNull [] cmd, CommandTarget senderType) {
        if(cmd.length > 1) {

            OneLinedMessage build = chatMessageBuilder(commandSender).add(gen(cmd)).build();
            Objects.requireNonNull(Ingwer.getStorage().getAllIdentities()).forEach(build::send);

        }else {
            //TODO: bommels05
            if(commandSender.getName().equals("Bommels05")) {
                MessageBuilder.prefixed().add(C.E,"Please care about the usage!");
            }else
                MessageBuilder.prefixed().add(C.E,"Please enter message!");

        }
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.SAVE};
    }

    public String gen(String @NotNull [] cmd) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cmd.length; i++) {
            if(i == 0) {continue;}
            if(i == cmd.length - 1 ) {
                builder.append(cmd[i]);
            }else {
                builder.append(cmd[i] + " ");
            }
        }
        return builder.toString();
    }
}
