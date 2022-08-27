package de.bentzin.ingwer.identity;

import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Identity {


    public static UUID DEVELOPER_UUID = UUID.fromString("be6e2c93-694b-4cdf-827f-83d6f2d42fb9");


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

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }


    //generator
    @Contract(pure = true)
    public static @Nullable Identity deserialize() {

        return null;
    };
}
