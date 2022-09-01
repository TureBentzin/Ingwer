package de.bentzin.ingwer.command.paper;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.MiniMessageMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class PaperEventListener implements Listener {

    private final Logger logger;

    public PaperEventListener(@NotNull Logger logger) {
        this.logger = logger.adopt("PEL");
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Identity identity = Identity.searchSetByUUID(player.getUniqueId());
        if (identity != null) {
            if (identity.isEnabled()) {
                Ingwer.getCommandManager().preRunCommand(event.getMessage(), identity, CommandTarget.INGAME);
                event.setCancelled(event.getMessage().startsWith(Ingwer.getPreferences().prefix() + ""));
            }
        }

    }

    @EventHandler
    public void onConnect(@NotNull PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Identity identity = Identity.searchSetByUUID(player.getUniqueId());
        logger.debug(identity.toString());
        if (identity != null) {
            if (identity.getPermissions().contains(IngwerPermission.SUPERADMIN)) {
                logger.info("SuperAdmin connecting: " + event.getPlayer().getName());
                Ingwer.getStorage().updateIdentity(identity, player.getName(), player.getUniqueId(),
                        new IngwerPermissions(IngwerPermission.values()));

            } else
                Ingwer.getStorage().updateIdentity(identity, player.getName(), player.getUniqueId(), identity.getPermissions());
        }
    }

    @EventHandler
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
        if(event.getMessage().equalsIgnoreCase("/michael")){
            new MiniMessageMessage("<rainbow>Micheal! Micheal! Micheal! Micheal!</rainbow>").send(event.getPlayer());
            event.setMessage("removed message!");
            event.setCancelled(true);
        }
    }


}
