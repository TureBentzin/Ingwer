package de.bentzin.ingwer.message;

import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.utils.Hardcode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ture Bentzin
 * 13.11.2022
 */
public interface MessageLike{
    static MessageLike fromMessage(IngwerMessage ingwerMessage) {
        return ingwerMessage;
    }

    static MessageLike fromBuilder(@NotNull MessageBuilder messageBuilder) {
        return messageBuilder.build();
    }

    @Contract("_ -> new")
    @Hardcode
    static @NotNull MessageLike fromMiniMessage(String miniMessage) {
        return new MiniMessageMessage(miniMessage);
    }

    /**
     * This should never be made final
     * @return the Message
     */
    IngwerMessage toMessage();
}
