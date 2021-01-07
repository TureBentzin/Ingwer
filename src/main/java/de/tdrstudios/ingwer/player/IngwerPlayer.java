package de.tdrstudios.ingwer.player;

import de.tdrstudios.ingwer.identity.AccessType;
import de.tdrstudios.ingwer.identity.Identity;
import de.tdrstudios.ingwer.player.players.IngwerPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class IngwerPlayer {
    public IngwerPlayer(OfflinePlayer offlinePlayer, Identity identity) {
        setOfflinePlayer(offlinePlayer);
        setIdentity(identity);
        getIdentity().setIngwerPlayer(this);
        getIdentity().setPlayerName(this.offlinePlayer.getName());
        getIdentity().setOfflinePlayer(offlinePlayer);

    }
    private  OfflinePlayer offlinePlayer;
    private Identity identity;

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Identity getIdentity() {
        return identity;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }
    protected void setOfflinePlayer(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }
    private boolean IngwerUser;


    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }
    public Player getOnlinePLayer() {
        if(isOnline()) {
            return getOnlinePLayer().getPlayer();
        }else {
            return null;
        }
    }


    public void setIngwerUser(boolean ingwerUser) {
        IngwerUser = ingwerUser;
    }

    public boolean isIngwerUser() {
        return IngwerUser;
    }

    @Deprecated
    /**
     * @implNote I don´t know if this works!
     */
    public boolean equalsPlayer(Player player) {
        if (this == player) return true;
        if (player == null || getClass() != player.getClass()) return false;
        IngwerPlayer that = (IngwerPlayer) player;
        return Objects.equals(player, that.offlinePlayer);
    }

    //Trolls and stuff

    public static IngwerPlayer getInstance() {
        return new IngwerPlayer(Bukkit.getOfflinePlayer("Player"), new Identity());
    }

    /**
     *
     * @param playerJoinEvent
     * @see de.tdrstudios.ingwer.listeners.JoinEvent
     */
    public void joinEvent(PlayerJoinEvent playerJoinEvent , IngwerPlayer ingwerPlayer) {
        Identity identity = ingwerPlayer.getIdentity();
        IngwerPlayerManager.getIngwerPlayerList().add(ingwerPlayer);
    }
    public void leaveEvent(PlayerQuitEvent playerQuitEvent) {

    }
    public void kickEvent(PlayerKickEvent playerKickEvent) {

    }
}
