package de.tdrstudios.ingwer.identity;

import de.tdrstudios.ingwer.permissions.IngwerPermission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Identity {
    //Static
    private static Identity adminIdentity = new Identity(Bukkit.getOfflinePlayer("Admin") , AccessType.UNKNOWN);
    public static void setAdminIdentity(Identity adminIdentity) {
        adminIdentity = adminIdentity;
    }
    public static Identity getAdminIdentity() {
        return adminIdentity;
    }

    //Object

    public Identity(OfflinePlayer player ,AccessType accessType) {
        setPlayer(player);
        setAccessType(accessType);
        setPlayerName(player.getName());
        setPermissionArrayList(new ArrayList<>());
    }
    private OfflinePlayer player;
    private String playerName;
    private AccessType accessType;
    private ArrayList<IngwerPermission> permissionArrayList;

    public AccessType getAccessType() {
        return accessType;
    }

    public ArrayList<IngwerPermission> getPermissionArrayList() {
        return permissionArrayList;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public void setPermissionArrayList(ArrayList<IngwerPermission> permissionArrayList) {
        this.permissionArrayList = permissionArrayList;
    }

    public void setPlayer(OfflinePlayer player) {
        this.player = player;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @return a new Identity
     */
    public static Identity getInstance(Player player) {
        return new Identity(player , AccessType.UNKNOWN);
    }
}
