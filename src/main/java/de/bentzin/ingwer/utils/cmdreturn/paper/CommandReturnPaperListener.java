package de.bentzin.ingwer.utils.cmdreturn.paper;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandReturnPaperListener implements Listener {

    private final Logger logger;

    public CommandReturnPaperListener(@NotNull Logger parent) {
        this.logger = parent.adopt("CRPL");
        logger.setDebug(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent preprocessEvent) {
        logger.debug("call");
        UUID uniqueId = preprocessEvent.getPlayer().getUniqueId();
        String message = preprocessEvent.getMessage();
        if(Ingwer.getCommandReturnSystem().runThrough(message,uniqueId)) {
            logger.debug("handled returnCommand of " + uniqueId);
            preprocessEvent.setCancelled(true);
            preprocessEvent.setMessage("/?");
        }else {
           // logger.debug("fake output:");
           // LogManager.getRootLogger().info("INGWER" + preprocessEvent.getPlayer().getName() + " issued server command: " + message);
        }
    }

}
