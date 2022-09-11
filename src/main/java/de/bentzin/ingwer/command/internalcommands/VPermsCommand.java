package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;

public class VPermsCommand extends IngwerCommand implements Permissioned {

    public VPermsCommand() {
        super("vperms", "view permissions of an Ingwer Identity");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
        identityIdentityCommand(commandSender, senderType, cmd, (identity, identity2) -> MessageBuilder.prefixed().add("Permisisons of: ").add(C.A, identity2.getName() + ": ").add(C.C, identity2.getPermissions().toString())
                .build().send(identity));
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.INGAME};
    }

    @Override
    public IngwerPermission getPermission() {
        return IngwerPermission.ADMIN;
    }
}
