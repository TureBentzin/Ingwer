package de.tdrstudios.ingwer.identity;

import de.tdrstudios.ingwer.permissions.IngwerPermission;
import de.tdrstudios.ingwer.player.IngwerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Identity {
    //Static
    private static Identity adminIdentity = new Identity(IngwerPlayer.getInstance() , AccessType.UNKNOWN);
    public static void setAdminIdentity(Identity adminIdentity) {
        adminIdentity = adminIdentity;
    }
    public static Identity getAdminIdentity() {
        return adminIdentity;
    }

    //Object

    @Deprecated
    public Identity(OfflinePlayer offlinePlayer, AccessType accessType) {
        setOfflinePlayer(offlinePlayer);
        setAccessType(accessType);
        setPlayerName(offlinePlayer.getName());
        setPermissionArrayList(new ArrayList<>());
        IngwerPlayer ingwerPlayer = new IngwerPlayer(getOfflinePlayer().getPlayer(), this);
    }
    public Identity(IngwerPlayer ingwerPlayer, AccessType accessType) {
        setIngwerPlayer(ingwerPlayer);
        setOfflinePlayer(ingwerPlayer.getOfflinePlayer());
        setAccessType(accessType);
        setPlayerName(offlinePlayer.getName());
        setPermissionArrayList(new ArrayList<>());
        getIngwerPlayer().setIdentity(this);
    }
    public Identity(AccessType accessType) {
        setAccessType(accessType);
    }
    private OfflinePlayer offlinePlayer;
    private IngwerPlayer ingwerPlayer;
    private String playerName;
    private AccessType accessType;
    private ArrayList<IngwerPermission> permissionArrayList;

    public AccessType getAccessType() {
        return accessType;
    }

    public IngwerPlayer getIngwerPlayer() {
        return ingwerPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setIngwerPlayer(IngwerPlayer ingwerPlayer) {
        this.ingwerPlayer = ingwerPlayer;
    }


    public ArrayList<IngwerPermission> getPermissionArrayList() {
        return permissionArrayList;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public void setPermissionArrayList(ArrayList<IngwerPermission> permissionArrayList) {
        this.permissionArrayList = permissionArrayList;
    }

    public void setOfflinePlayer(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;

    }

    public boolean equalsPlayer(Player player) {
        return getOfflinePlayer().equals(player);
    }


    /**
     * @return a new Identity
     */
    public static Identity getInstance(Player player) {
        return new Identity(player , AccessType.UNKNOWN);
    }
}
