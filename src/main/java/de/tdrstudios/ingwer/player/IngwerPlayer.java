package de.tdrstudios.ingwer.player;

import de.tdrstudios.ingwer.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class IngwerPlayer {
    public IngwerPlayer(OfflinePlayer offlinePlayer, Identity identity) {
        setOfflinePlayer(offlinePlayer);
        setIdentity(identity);
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
        return new IngwerPlayer(Bukkit.getOfflinePlayer("Player"));
    }
}
