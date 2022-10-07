package de.bentzin.ingwer.storage;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Ture Bentzin
 * 07.10.2022
 *
 * @implNote Every implementation of this should have a static method <code>public static StorageProvider<ThisClass> getProvider(arguments);</code>
 */
public interface Storage {
    /* */

    void init();

    void close();

    Identity saveIdentity(@NotNull Identity identity);

    @Nullable Identity getIdentityByName(String name);

    @Nullable Identity getIdentityByUUID(String uuid);

    @Nullable Collection<Identity> getAllIdentities();

    @Nullable Identity getIdentityByID(int id);

    void removeIdentity(@NotNull Identity identity);

    boolean containsIdentityWithUUID(String uuid);

    @NotNull Collection<Identity> getIdentities();

    @Contract("_, _, _, _ -> param1")
    Identity updateIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions);

    /**
     * @implNote If Identity is not present this will create a new one based on the given SINGLE parameters. In this case the given Identity would not be used or changed!!!
     */
    @Contract("_, _, _, _ -> param1")
    Identity updateOrSaveIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions);
}
