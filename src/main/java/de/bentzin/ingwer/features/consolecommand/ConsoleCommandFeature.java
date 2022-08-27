package de.bentzin.ingwer.features.consolecommand;

import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class ConsoleCommandFeature extends SimpleFeature {
    public ConsoleCommandFeature() {
        super("console-command", "This lets you execute commands in Bukkits server console!");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return null;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return false;
    }
}
