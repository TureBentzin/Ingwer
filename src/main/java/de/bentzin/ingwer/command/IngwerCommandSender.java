package de.bentzin.ingwer.command;

import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import org.jetbrains.annotations.NotNull;

public interface IngwerCommandSender {

    String getName();

    /**
     * @param ingwerMessage
     * @implNote NEVER CALL FROM A {@link IngwerMessage#send(IngwerCommandSender)}!!!
     */
    default void sendMessage(@NotNull IngwerMessage ingwerMessage){
        ingwerMessage.send(this);
    }
    void sendMessage(String raw);
    void sendMessage(Object o);

    /**
     *
     * @param oneLinedMessage message meant to be sent
     * @implNote implement sending of message here
     */
    void sendOneLinedMessage(OneLinedMessage oneLinedMessage);

    IngwerPermissions getPermissions();
    long getCodedPermissions();

    boolean isReachable();
}
