package de.bentzin.ingwer.utils.cmdreturn.paper;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandReturnPaperListener implements Listener {

    private final Logger logger;

    public CommandReturnPaperListener(@NotNull Logger parent) {
        //noinspection SpellCheckingInspection
        this.logger = parent.adopt("CRPL");
        logger.setDebug(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(@NotNull PlayerCommandPreprocessEvent preprocessEvent) {
        UUID uniqueId = preprocessEvent.getPlayer().getUniqueId();
        String message = preprocessEvent.getMessage();
        if (Ingwer.getCommandReturnSystem().runThrough(message, uniqueId)) {
            logger.debug("handled returnCommand of " + uniqueId);
            preprocessEvent.setCancelled(true);
            preprocessEvent.setMessage("/?");
        }
    }

}
