package de.tdrstudios.ingwer.player;

import de.tdrstudios.ingwer.Ingwer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class IngwerPlayer {
    public IngwerPlayer(Player player) {
        setPlayer(player);
    }
    private  Player player;
    public Player getPlayer() {
        return player;
    }
    protected void setPlayer(Player player) {
        this.player = player;
    }
    private boolean IngwerUser;

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
        return Objects.equals(player, that.player);
    }

}
