package de.bentzin.ingwer.command;

import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.message.IngwerMessage;

public interface IngwerCommandSender {

    String getName();
    void sendMessage(IngwerMessage ingwerMessage);
    void sendMessage(String raw);
    void sendMessage(Object o);

    IngwerPermissions getPermissions();
    long getCodedPermissions();

    boolean isReachable();
}
