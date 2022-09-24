package de.bentzin.ingwer.command.paper;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.message.MiniMessageMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.TabCompleteEvent;
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
    public void onChat(@NotNull AsyncPlayerChatEvent event) { //TODO: Switch to AsyncChatEvent
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

    }

    @EventHandler
    public void onTab(TabCompleteEvent event) {
        event.getSender().sendMessage(event.getBuffer());
    }

    @EventHandler
    public void onConnect(@NotNull PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Identity identity = Ingwer.getStorage().getIdentityByUUID(player.getUniqueId().toString());

        if (identity != null) {
            logger.debug(identity.toString());
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
        if (event.getMessage().equalsIgnoreCase("/michael")) {
            new MiniMessageMessage("<rainbow>Michael! Michael! Michael! Michael!</rainbow>").send(event.getPlayer());
            event.setMessage("removed message!");
            event.setCancelled(true);
        }
    }


}
