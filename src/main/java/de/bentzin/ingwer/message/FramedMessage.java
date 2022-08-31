package de.bentzin.ingwer.message;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FramedMessage extends SimpleMultilinedMessage{

    public static Component FRAME = IngwerMessage.mm("<gray>--------------- " + IngwerMessage.INGWER + "<gray> ---------------");

    public static @NotNull List<OneLinedMessage> frame(Collection<OneLinedMessage> content) {
        ComponentMessage componentMessage = new ComponentMessage(FRAME);
        List<OneLinedMessage> oneLinedMessages = new ArrayList<>();
        oneLinedMessages.add(componentMessage);
        oneLinedMessages.addAll(content);
        oneLinedMessages.add(componentMessage);
        return oneLinedMessages;
    }

    public FramedMessage(Collection<OneLinedMessage> messageList) {
        super(frame(messageList));
    }
}
