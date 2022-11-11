package de.bentzin.ingwer.identity;

import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.message.OneLinedMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Identity implements IngwerCommandSender {


    public static final UUID DEVELOPER_UUID = UUID.fromString("be6e2c93-694b-4cdf-827f-83d6f2d42fb9");

    //Identity
    private final String name;
    private final UUID uuid;
    private final IngwerPermissions permissions;

    public Identity(String name, UUID uuid, IngwerPermissions permissions) {
        this.name = name;
        this.uuid = uuid;
        this.permissions = permissions;
    }


    public boolean isSuperAdmin() {
        return permissions.contains(IngwerPermission.SUPERADMIN);
    }

    public boolean isEnabled() {
        return permissions.contains(IngwerPermission.USE);
    }

    public IngwerPermissions getPermissions() {
        return permissions;
    }

    public long getCodedPermissions() {
        return IngwerPermission.generatePermissions(getPermissions());
    }

    @Override
    public boolean isReachable() {
        //case: Ingame
        Player player = Bukkit.getPlayer(uuid);
        return player != null && player.isOnline();
        //case: remote
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }


    @Override
    public void sendMessage(String raw) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.sendMessage(raw);
        }
    }

    @Override
    public void sendMessage(Object o) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.sendMessage(o.toString());
        }
    }

    /**
     * @param oneLinedMessage message meant to be sent
     * @implNote implement sending of message here
     */
    @Override
    public void sendOneLinedMessage(OneLinedMessage oneLinedMessage) {
        //TODO: Add
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.sendMessage(oneLinedMessage.getOneLinedComponent());
        }

    }

    @Override
    public String toString() {
        String sb = "Identity{" + "name='" + name + '\'' +
                ", uuid=" + uuid +
                ", permissions=" + permissions +
                '}';
        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identity identity)) return false;

        if (!getName().equals(identity.getName())) return false;
        if (!Objects.equals(uuid, identity.uuid)) return false;
        return getPermissions() != null ? getPermissions().equals(identity.getPermissions()) : identity.getPermissions() == null;
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (getPermissions() != null ? getPermissions().hashCode() : 0);
        return result;
    }

    //generator
}
