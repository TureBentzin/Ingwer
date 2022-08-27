package de.bentzin.ingwer.features.consolecommand;

import de.bentzin.ingwer.command.IngwerCommandManager;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.tools.register.Registerator;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class ConsoleCommandFeature extends SimpleFeature {

    private final SudoConsoleCommand sudoConsoleCommand;

    public ConsoleCommandFeature() {
        super("console-command", "This lets you execute commands in Bukkits server console!");
        sudoConsoleCommand = new SudoConsoleCommand(this);
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.SUDO_COMMAND;
    }

    @Override
    public void onEnable() {
        try {

            IngwerCommandManager.getInstance().register(sudoConsoleCommand);
        } catch (Registerator.DuplicateEntryException e) {
            IngwerThrower.acceptS(e, ThrowType.COMMAND);
        }
    }

    @Override
    public void onDisable() {
        try {
            IngwerCommandManager.getInstance().unregister(sudoConsoleCommand);
        } catch (Registerator.NoSuchEntryException e) {
            IngwerThrower.acceptS(e, ThrowType.COMMAND);
        }
    }

    @Override
    public boolean onLoad() {
        return false;
    }
}
