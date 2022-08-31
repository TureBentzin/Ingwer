package de.bentzin.ingwer.utils.cmdreturn.paper;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandReturnPaperListener implements Listener {

    private final Logger logger;

    public CommandReturnPaperListener(@NotNull Logger parent) {
        this.logger = parent.adopt("CRPL");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @ApiStatus.Experimental
    public void onCommand(@NotNull PlayerCommandPreprocessEvent preprocessEvent) {
        UUID uniqueId = preprocessEvent.getPlayer().getUniqueId();
        String message = preprocessEvent.getMessage();
        if(Ingwer.getCommandReturnSystem().runThrough(message,uniqueId)) {
            logger.debug("handled returnCommand of " + uniqueId);
            preprocessEvent.setCancelled(true);
            preprocessEvent.setMessage("/?");
        }else {
            LogManager.getRootLogger().info(preprocessEvent.getPlayer().getName() + " issued server command: " + message);
        }
    }
}
