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

public class ChatCommand extends IngwerCommand implements CommandUtils {

    public ChatCommand() {
        super("chat", "Write messages to your fellow Ingwer users currently available.");
    }

    public static MessageBuilder chatMessageBuilder(@NotNull IngwerCommandSender sender) {
        return MessageBuilder.prefixed().add("[<gradient:yellow:aqua>Chat</gradient>]: ").add(C.A, "@" + sender.getName() + ": ").c();
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String @NotNull [] cmd, CommandTarget senderType) {
        if (cmd.length > 1) {

            OneLinedMessage build = chatMessageBuilder(commandSender).add(gen(cmd)).build();
            Objects.requireNonNull(Ingwer.getStorage().getAllIdentities()).forEach(build::send);

        } else //noinspection SpellCheckingInspection
        {
            //TODO: bommels05
            //noinspection SpellCheckingInspection
            if (commandSender.getName().equals("Bommels05")) {
                MessageBuilder.prefixed().add(C.E, "Please care about the usage!").build().send(commandSender);
            } else
                MessageBuilder.prefixed().add(C.E, "Please enter message!").build().send(commandSender);

        }
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.SAVE};
    }

}
