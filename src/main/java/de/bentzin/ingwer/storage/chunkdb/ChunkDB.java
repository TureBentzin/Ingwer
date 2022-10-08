package de.bentzin.ingwer.storage.chunkdb;

import com.google.common.annotations.Beta;
import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.storage.Storage;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import de.bentzin.ingwer.utils.LoggingClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.UUID;

import static de.bentzin.ingwer.storage.chunkdb.ChunkDBManager.NAMESPACE;
import static de.bentzin.ingwer.storage.chunkdb.ChunkDBManager.genKey;


/**
 * @author Ture Bentzin
 * 07.10.2022
 */
@SuppressWarnings("FieldCanBeLocal")
@ApiStatus.Experimental
public class ChunkDB extends LoggingClass implements Storage {

    public final String IDENTITY_PREFIX = "identities.";
    private final ChunkDBManager dbManager = new SyncedChunkDBManager(Bukkit::getWorlds);
    private final String VERSION_STRING = "1.0-RELEASE";

    public ChunkDB() {
        super(Ingwer.getLogger().adopt("ChunkDB"));
    }


    @Override
    public void init() {
        getLogger().info("running ChunkDB v." + VERSION_STRING);
    }

    @Override
    public void close() {

    }

    @Override
    public Identity saveIdentity(@NotNull Identity identity) {
        NamespacedKey origin = genNextIdentityKey();
        dbManager.save(cloneAppend(origin, "name"), identity.getName());
        dbManager.save(cloneAppend(origin, "uuid"), identity.getUUID().toString());
        dbManager.save(cloneAppend(origin, "perms"), Long.toString(identity.getCodedPermissions()));
        dbManager.save(cloneAppend(origin, "flag"), Short.valueOf("0").toString());
        return identity;
    }

    @Override
    public @Nullable Identity getIdentityByName(String name) {
        return null;
    }

    @Override
    public @Nullable Identity getIdentityByUUID(String uuid) {
        return null;
    }

    @Override
    public @Nullable Collection<Identity> getAllIdentities() {
        return null;
    }

    @Override
    public @Nullable Identity getIdentityByID(int id) {
        try {
            NamespacedKey key = genKey(IDENTITY_PREFIX + id + ".");

            String name = dbManager.get(cloneAppend(key, "name"));
            UUID uuid = UUID.fromString(dbManager.get(cloneAppend(key, "uuid")));
            long coded = Long.parseLong(dbManager.get(cloneAppend(key, "perms")));

            IngwerPermissions ingwerPermissions = IngwerPermission.decodePermissions(coded);

            return new Identity(name, uuid, ingwerPermissions);
        } catch (Exception e) {
            IngwerThrower.acceptS(e, ThrowType.STORAGE);
            return null;
        }
    }

    @Beta
    @Override
    public void removeIdentity(@NotNull Identity identity) {
    }

    @Override
    public boolean containsIdentityWithUUID(String uuid) {
        return false;
    }

    @Override
    public @NotNull Collection<Identity> getIdentities() {
        return null;
    }

    @Override
    public Identity updateIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions) {
        return null;
    }

    /**
     * @return IDENTITY_PREFIX + n+1 + "." -> "identities.3."
     */
    protected String nextIdentityKey() {
        int max = 0;
        for (NamespacedKey namespacedKey : dbManager.namespacedKeys()) {
            if (namespacedKey.getNamespace().equals(NAMESPACE)) {
                if (namespacedKey.getKey().startsWith(IDENTITY_PREFIX)) {
                    String rem = namespacedKey.getKey().replace(IDENTITY_PREFIX, "");
                    String[] parts = rem.split("\\.");
                    if (parts.length > 1) {
                        int n = Integer.parseInt(parts[0]);
                        if (n > max) max = n;
                    } else {
                        throw new InvalidParameterException(namespacedKey.getNamespace() + "::" + namespacedKey.getKey() + " -> ERROR!");
                    }
                }
            }
        }
        return IDENTITY_PREFIX + max + 1 + ".";
    }

    public NamespacedKey genNextIdentityKey() {
        return new NamespacedKey("ingwer", nextIdentityKey());
    }

    public NamespacedKey cloneAppend(NamespacedKey origin, String @NotNull ... sub) {
        StringJoiner joiner = new StringJoiner(".");
        for (String s : sub) joiner.add(s);
        return new NamespacedKey(origin.namespace(), joiner.toString());
    }

    /**
     * @param identity          the identity to update
     * @param name              new name
     * @param uuid              new uuid
     * @param ingwerPermissions new permissions
     * @implNote If Identity is not present this will create a new one based on the given SINGLE parameters. In this case the given Identity would not be used or changed!!!
     */
    @Override
    public Identity updateOrSaveIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions) {
        return null;
    }


}
