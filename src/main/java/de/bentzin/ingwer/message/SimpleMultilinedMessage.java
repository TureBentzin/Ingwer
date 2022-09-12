package de.bentzin.ingwer.message;

import de.bentzin.ingwer.command.IngwerCommandSender;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class SimpleMultilinedMessage implements MultilinedMessage {

    private final List<OneLinedMessage> messageList;

    public SimpleMultilinedMessage(List<OneLinedMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public void send(@NotNull CommandSender recipient) {
        get().forEachRemaining(oneLinedMessage -> recipient.sendMessage(oneLinedMessage.getOneLinedComponent()));
    }

    @Override
    public void send(@NotNull IngwerCommandSender recipient) {
        get().forEachRemaining(recipient::sendOneLinedMessage);
    }

    @Override
    public Iterator<OneLinedMessage> get() {
        return messageList.iterator();
    }

    @Override
    public String[] getLabel() {
        String[] strings = new String[getDepth()];
        get().forEachRemaining(oneLinedMessage -> {
            //unsave?! dangerous? yes maybe...
            for (int i = 0; i < strings.length; i++) {
                if (strings[i] == null) {
                    strings[i] = LegacyComponentSerializer.legacyAmpersand().serialize(oneLinedMessage.getOneLinedComponent());
                }
            }
        });
        return strings;
    }

    @Override
    public String[] getPlainLabel() {
        String[] strings = new String[getDepth()];
        get().forEachRemaining(oneLinedMessage -> {
            for (int i = 0; i < strings.length; i++) {
                if (strings[i] == null) {
                    strings[i] = PlainTextComponentSerializer.plainText().serialize(oneLinedMessage.getOneLinedComponent());
                }
            }
        });
        return strings;
    }

    @Override
    public int getDepth() {
        return messageList.size();
    }
}
