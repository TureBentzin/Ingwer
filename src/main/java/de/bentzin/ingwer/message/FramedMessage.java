package de.bentzin.ingwer.message;

import com.google.common.annotations.Beta;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class FramedMessage extends SimpleMultilinedMessage {

    public static final Component FRAME;

    static {
        Component mm = IngwerMessage.mm("<gray>--------------- ");
        Component nC = Component.empty();
        FRAME = nC.append(mm).append(IngwerMessage.INGWER_HEAD).append(mm);
    }

    public FramedMessage(@NotNull Collection<MessageLike> messageList) {
        super(frame(messageList));
    }

    /**
     * This is highly experimental!
     * This may be removed
     * @param messageLikes
     */
    @ApiStatus.Experimental
    @Beta
    public FramedMessage(@NotNull Collection<MessageLike> messageLikes) {
        super(frame(messageLikes.stream().map(messageLike -> {
            if (messageLike instanceof OneLinedMessage olm)
                return olm;
            else
                return null;
        }).filter(Objects::nonNull).toList()));
    }

    public FramedMessage(Collection<OneLinedMessage> messageList, Component footer) {
        super(frame(footer(messageList, footer)));
    }

    public static @NotNull List<OneLinedMessage> frame(Collection<OneLinedMessage> content) {
        ComponentMessage componentMessage = new ComponentMessage(FRAME);
        List<OneLinedMessage> oneLinedMessages = new ArrayList<>();
        oneLinedMessages.add(IngwerMessage.EMPTY_LINE);
        oneLinedMessages.add(componentMessage);
        oneLinedMessages.addAll(content);
        oneLinedMessages.add(componentMessage);
        return oneLinedMessages;
    }

    private static @NotNull Collection<OneLinedMessage> footer(@NotNull Collection<OneLinedMessage> collection, Component footer) {
        collection.add(new ComponentMessage(footer));
        return collection;
    }
}
