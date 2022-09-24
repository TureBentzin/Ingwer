package de.bentzin.ingwer.command.ext;

import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @see de.bentzin.ingwer.command.node.Node
 * @see de.bentzin.ingwer.command.IngwerCommand
 */
public interface Permissioned {

    @ApiStatus.Experimental
    static void lacking(IngwerCommandSender commandSender, IngwerPermission ingwerPermission) {
        new LackingPermissionMessage(ingwerPermission).send(commandSender);
    }

    @NotNull
    IngwerPermission getPermission();

    default boolean checkPermission(@NotNull IngwerCommandSender ingwerCommandSender) {
        return ingwerCommandSender.getPermissions().contains(getPermission());
    }

    @ApiStatus.Internal
    class LackingPermissionMessage extends MiniMessageMessage {
        private final IngwerPermission ingwerPermission;

        public LackingPermissionMessage(@NotNull IngwerPermission ingwerPermission) {
            super(MessageBuilder.prefixed().add(C.E, "You are lacking permission: ").add(C.A, ingwerPermission.name()).add(C.E, "!").exportBuilder().toString());

            this.ingwerPermission = ingwerPermission;
        }

        public IngwerPermission getIngwerPermission() {
            return ingwerPermission;
        }
    }
}
