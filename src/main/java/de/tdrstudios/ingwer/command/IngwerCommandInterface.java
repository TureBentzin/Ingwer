package de.tdrstudios.ingwer.command;

import de.tdrstudios.ingwer.identity.AccessType;
import de.tdrstudios.ingwer.player.IngwerPlayer;

public abstract interface IngwerCommandInterface {
    public abstract String getName();
    public abstract void setName();

    public abstract String getDescription();
    public abstract void setDescription(String description);

    public abstract IngwerCommandExecutorInterface getExecutor();
    public abstract void setExecutor(IngwerCommandExecutorInterface ingwerCommandExecutorInterface);

    public abstract AccessType getAccessType();
    public abstract void setAccessType(AccessType accessType);


}
