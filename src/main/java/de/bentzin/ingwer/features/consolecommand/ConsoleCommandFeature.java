package de.bentzin.ingwer.features.consolecommand;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.tools.register.Registerator;

//@NewFeature(author = "Ture Bentzin", version = "1.0")
public class ConsoleCommandFeature extends SimpleFeature {

    private final SudoConsoleCommand sudoConsoleCommand;

    public ConsoleCommandFeature() {
        super("console-command", "This lets you execute commands in Bukkits server console!");
        getLogger().info("registered sudo command");
        sudoConsoleCommand = new SudoConsoleCommand(this);

    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.SUDO_COMMAND;
    }

    @Override
    public void onEnable() {
        try {
            Ingwer.getCommandManager().register(sudoConsoleCommand);
        } catch (Registerator.DuplicateEntryException e) {
            getLogger().error("Error while registering commands!");
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onLoad() {
        return false;
    }
}
