package de.bentzin.ingwer.identity;

import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;

import java.util.UUID;

public class Identity {


    public static UUID DEVELOPER_UUID = UUID.fromString("be6e2c93-694b-4cdf-827f-83d6f2d42fb9");


    //Identity
    private final UUID uuid;
    private final IngwerPermissions permissions;

    public boolean isSuperAdmin() {
        return permissions.contains(IngwerPermission.SUPERADMIN);
    }

    public boolean isEnabled() {
        return permissions.contains(IngwerPermission.USE);
    }

    public Identity(UUID uuid, IngwerPermissions permissions) {
        this.uuid = uuid;
        this.permissions = permissions;
    }

    public IngwerPermissions getPermissions() {
        return permissions;
    }

    public UUID getUUID() {
        return uuid;
    }
}
