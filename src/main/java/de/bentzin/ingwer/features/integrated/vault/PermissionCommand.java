package de.bentzin.ingwer.features.integrated.vault;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PermissionCommand extends IngwerCommand {
    public PermissionCommand() {
        super("perm", "add, view or remove individual permissions");
    }

    /*
     * perm - view own
     * perm user [user] - view user
     * perm group [group] - view group
     * perm user/group [user/group] add [permission] - add [permission] to [user/group]
     * perm user/group [user/group] remove [permission] - remove [permission] to [user/group]
     *
     * perm user [user] join [group]
     * perm user [user] leave [group]
     */

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {

        //playerCommand()

    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[0];
    }

}
