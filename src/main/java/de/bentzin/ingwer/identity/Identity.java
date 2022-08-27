package de.bentzin.ingwer.identity;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.storage.Sqlite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Identity implements IngwerCommandSender {


    public static UUID DEVELOPER_UUID = UUID.fromString("be6e2c93-694b-4cdf-827f-83d6f2d42fb9");

    public static Set<Identity> IDENTITY_SET = new HashSet<>();

    public static void refresh() {
        IDENTITY_SET.clear();
        IDENTITY_SET.addAll(Ingwer.getStorage().getIdentities());
    }

    @Contract(pure = true)
    public static @Nullable Identity searchSetByUUID(@NotNull UUID uuid) {
        for (Identity identity : IDENTITY_SET) {
            if(identity.getUUID().equals(uuid)){
                return identity;
            }
        }
        return null;
    }



    //Identity
    private final String name;
    private final UUID uuid;
    private final IngwerPermissions permissions;

    public boolean isSuperAdmin() {
        return permissions.contains(IngwerPermission.SUPERADMIN);
    }

    public boolean isEnabled() {
        return permissions.contains(IngwerPermission.USE);
    }

    public Identity(String name, UUID uuid, IngwerPermissions permissions) {
        this.name = name;
        this.uuid = uuid;
        this.permissions = permissions;
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
        if(player.isOnline()){
            return true;
        }
        //case: remote
        return false;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public void sendMessage(IngwerMessage ingwerMessage) {
        Player player = Bukkit.getPlayer(uuid);
        if(player.isOnline()){
            player.sendMessage(ingwerMessage.toString());
        }
    }

    @Override
    public void sendMessage(String raw) {
        Player player = Bukkit.getPlayer(uuid);
        if(player.isOnline()){
            player.sendMessage(raw);
        }
    }

    @Override
    public void sendMessage(Object o) {
        Player player = Bukkit.getPlayer(uuid);
        if(player.isOnline()){
            player.sendMessage(o.toString());
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Identity{");
        sb.append("name='").append(name).append('\'');
        sb.append(", uuid=").append(uuid);
        sb.append(", permissions=").append(permissions);
        sb.append('}');
        return sb.toString();
    }

    //generator
}
