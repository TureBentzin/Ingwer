package de.bentzin.ingwer.command.paper;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PaperEventListener implements Listener {

    public static final List<String> AUTHORIZED = new ArrayList<>();
    private final Logger logger;

    public PaperEventListener(@NotNull Logger logger) {
        this.logger = logger.adopt("PEL");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        try { //TODO: Switch to AsyncChatEvent
            Player player = event.getPlayer();
            Identity identity = Ingwer.getStorage().getIdentityByUUID(player.getUniqueId().toString());
            if (identity != null) {
                if (identity.isEnabled()) {
                    if (AUTHORIZED.contains(event.getMessage())) {
                        AUTHORIZED.remove(event.getMessage());
                        return;
                    }
                    Ingwer.getCommandManager().preRunCommand(event.getMessage(), identity, CommandTarget.INGAME);
                    event.setCancelled(event.getMessage().startsWith(Ingwer.getPreferences().prefix() + ""));
                }
            }

        }catch (Throwable throwable) {
            IngwerThrower.acceptS(throwable, ThrowType.EVENT);
        }
    }


    @EventHandler
    public void onConnect(@NotNull PlayerLoginEvent event) {
        try {
            Player player = event.getPlayer();
            Identity identity = Ingwer.getStorage().getIdentityByUUID(player.getUniqueId().toString());

            if (identity != null) {
                if (identity.getPermissions().contains(IngwerPermission.SUPERADMIN)) {
                    logger.info("SuperAdmin connecting: " + event.getPlayer().getName());
                    Ingwer.getStorage().updateOrSaveIdentity(identity, player.getName(), player.getUniqueId(),
                            new IngwerPermissions(IngwerPermission.values()));
                } else
                    Ingwer.getStorage().updateOrSaveIdentity(identity, player.getName(), player.getUniqueId(), identity.getPermissions());
            }
        }catch (Throwable throwable) {
            IngwerThrower.acceptS(throwable, ThrowType.EVENT);
        }
    }

}
