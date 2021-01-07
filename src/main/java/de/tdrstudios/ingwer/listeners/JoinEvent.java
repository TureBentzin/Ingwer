package de.tdrstudios.ingwer.listeners;

import de.tdrstudios.ingwer.identity.AccessType;
import de.tdrstudios.ingwer.identity.Identity;
import de.tdrstudios.ingwer.player.IngwerPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sun.jvm.hotspot.ui.ObjectHistogramPanel;

public class JoinEvent implements Listener {

    /**
     * @see IngwerPlayer
     * @param e
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Identity identity = new Identity(AccessType.UNKNOWN);
        IngwerPlayer ingwerPlayer = new IngwerPlayer(player,identity);
        ingwerPlayer.joinEvent(e, ingwerPlayer);

    }
}
