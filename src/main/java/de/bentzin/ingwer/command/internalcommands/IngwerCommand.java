package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.message.FramedMessage;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Collection;

import static de.bentzin.ingwer.message.IngwerMessage.*;


/*
 *
 Thank you for using Ingwer v.0.3-BETA
 Ingwer was developed to help people understand the dangers of simple things like JavaPlugins.
 My target is to educate people about this hazard for paper servers.
 I prohibit any use of this software without the consent of the managing authority
 for the server Ingwer is used on. If you have any questions or concerns about Ingwer
 then please reach out to me on GitHub.
 Ture Bentzin - Developer
 */


public final class IngwerCommand extends de.bentzin.ingwer.command.IngwerCommand {

    public static @NotNull Collection<OneLinedMessage> message(IngwerCommandSender sender) {
        ArrayDeque<OneLinedMessage> arrayDeque = new ArrayDeque<>();
        arrayDeque.addLast(new MiniMessageMessage(COLOR_MM + "      Thank you for using " + ACCENT_MM + "Ingwer" +  " v. " +  Ingwer.VERSION_STRING + COLOR_MM + "!"));
        arrayDeque.addLast(EMPTY_LINE);
        arrayDeque.addLast(new MiniMessageMessage(COLOR_MM + " Ingwer was developed to help people understand the dangers of simple things like JavaPlugins. My target is to educate people about this hazard for paper servers. I prohibit any use of this software without the consent of the managing authority for the server Ingwer is used on. If you have any questions or concerns about Ingwer then please reach out to me on <gray><click:open_url:'https://github.com/TureBentzin/Ingwer/discussions'><u>GitHub</u></click></gray>."));
        arrayDeque.addLast(EMPTY_LINE);
        arrayDeque.addLast(new MiniMessageMessage(ACCENT_MM + "Ture Bentzin - Developer"));
        return arrayDeque;

     }

    public IngwerCommand() {
        super("ingwer","Ingwer v. " + Ingwer.VERSION_STRING);
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
        Collection<OneLinedMessage> messageList = message(commandSender);
        FramedMessage message = new FramedMessage(messageList);
        message.send(commandSender);
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.SAVE};
    }
}
