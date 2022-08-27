package de.bentzin.ingwer.command.ext;

import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import org.jetbrains.annotations.NotNull;

public interface Permissioned {
    IngwerPermission getPermission();

     default boolean checkPermission(@NotNull IngwerCommandSender ingwerCommandSender) {
        return ingwerCommandSender.getPermissions().contains(getPermission());
    }
}
