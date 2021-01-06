package de.tdrstudios.ingwer.command;

import de.tdrstudios.ingwer.identity.AccessType;

public class IngwerCommand implements IngwerCommandInterface{
    private String name;
    private String description;
    private IngwerCommandExecutorInterface ingwerCommandExecutor;
    private AccessType accessType;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName() {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public IngwerCommandExecutorInterface getExecutor() {
        return ingwerCommandExecutor;
    }

    @Override
    public void setExecutor(IngwerCommandExecutorInterface ingwerCommandExecutor) {
        this.ingwerCommandExecutor = ingwerCommandExecutor;
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }
}
