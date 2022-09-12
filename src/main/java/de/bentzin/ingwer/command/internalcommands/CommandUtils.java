package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CommandUtils {

    /**
     * @param cmd the full command
     * @return regenerated message excluding the command identifier
     * @example {"say","hello","this","is","a","test!"} -> "hello this is a test!"
     */
    default String gen(String @NotNull [] cmd) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cmd.length; i++) {
            if (i == 0) {
                continue;
            }
            if (i == cmd.length - 1) {
                builder.append(cmd[i]);
            } else {
                builder.append(cmd[i]).append(" ");
            }
        }
        return builder.toString();
    }

    default Identity getOrCreateIdentity(@NotNull Player player) {
        Identity target = null;
        if (Ingwer.getStorage().containsIdentityWithUUID(String.valueOf(player.getUniqueId()))) {
            target = Ingwer.getStorage().getIdentityByUUID(String.valueOf(player.getUniqueId()));
        }
        if (target == null) {
            target = new Identity(player.getName(), player.getUniqueId(), new IngwerPermissions());
        }
        return target;
    }
}
