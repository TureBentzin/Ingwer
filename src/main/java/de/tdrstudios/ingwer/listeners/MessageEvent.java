package de.tdrstudios.ingwer.listeners;

import de.tdrstudios.ingwer.Ingwer;
import de.tdrstudios.ingwer.identity.Identity;
import de.tdrstudios.ingwer.player.IngwerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;


public class MessageEvent implements Listener {
    private Identity admin = Ingwer.getPreferences().getAdminIdentity();
    @EventHandler
    public void onMessage(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if(Ingwer.isActive()) {

        }
    }
}
