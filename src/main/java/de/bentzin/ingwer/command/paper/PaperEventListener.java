package de.bentzin.ingwer.command.paper;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

public class PaperEventListener implements Listener {

    private final Logger logger;

    public PaperEventListener(@NotNull Logger logger) {
        this.logger = logger.adopt("PEL");
    }


    @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Identity identity = Identity.searchSetByUUID(player.getUniqueId());
        if (identity != null) {
            if (identity.isEnabled())
                Ingwer.getCommandManager().preRunCommand(event.getMessage(), identity, CommandTarget.INGAME);
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
    public void onSuperAdminJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Identity identity = Identity.searchSetByUUID(player.getUniqueId());
        logger.debug(identity.toString());
        if (identity != null) {

            if(identity.isSuperAdmin()) {
                identity.sendMessage(ChatColor.GOLD + "Welcome to Ingwer, Captain!");
            }
        }
    }
}
