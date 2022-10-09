package de.bentzin.ingwer.storage.chunkdb;

import com.google.common.annotations.Beta;
import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.message.FramedMessage;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.storage.Storage;
import de.bentzin.ingwer.storage.StorageProvider;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import de.bentzin.ingwer.utils.LoggingClass;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Supplier;

import static de.bentzin.ingwer.storage.chunkdb.ChunkDBManager.*;


/**
 * @author Ture Bentzin
 * 07.10.2022
 */
@SuppressWarnings("FieldCanBeLocal")
@ApiStatus.Experimental
public class ChunkDB extends LoggingClass implements Storage {

    public final String IDENTITY_PREFIX = "identities.";
    private final ChunkDBManager dbManager;
    private final String VERSION_STRING = "1.0-RELEASE";

    public ChunkDB(ChunkDBManager chunkDBManager) {
        super(Ingwer.getLogger().adopt("ChunkDB"));
        dbManager = chunkDBManager;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull StorageProvider<ChunkDB> getProvider(Supplier<ChunkDBManager> chunkDBManagerSupplier) {
        return new StorageProvider<>(true, false) {
            /**
             * @return Storage or null if something goes really wrong
             */
            @Override
            public @NotNull ChunkDB get() {
                return new ChunkDB(chunkDBManagerSupplier.get());
            }
        };
    }

    @Override
    public void init() {
        getLogger().info("running ChunkDB v." + VERSION_STRING);
        dbManager.updateLogger(getLogger().adopt("DBManager"));
        getLogger().info("registering integrated Feature...");
        try {
            Ingwer.getFeatureManager().register(new ChunkDBFeature(this));
        } catch (Exception e) {
            getLogger().error("failed to register integrated Feature! :: \"" + e.getMessage() + "\"");
        }
    }

    @Override
    public void close() {
        dbManager.stop();
    }

    @Override
    public Identity saveIdentity(@NotNull Identity identity) {
        if ()
            NamespacedKey origin = genNextIdentityKey();
        dbManager.save(cloneAppend(origin, "name"), identity.getName());
        dbManager.save(cloneAppend(origin, "uuid"), identity.getUUID().toString());
        dbManager.save(cloneAppend(origin, "perms"), Long.toString(identity.getCodedPermissions()));
        dbManager.save(cloneAppend(origin, "flag"), Short.valueOf("0").toString());
        return identity;
    }

    /**
     * possible duplicate of {@link ChunkDB#allIdentityKeys(boolean)} with v1 = true
     */
    private @NotNull Collection<Integer> getAllIdentityIDs() {
        Collection<Integer> integers = new ArrayList<>();
        Collection<String> keys = allIdentityKeys(true);
        for (String key : keys) {
            String var = key.replace(IDENTITY_PREFIX, "");
            getLogger().debug("<getAllIdentityIDs()> -> key:" + key);
            integers.add(Integer.parseInt(var));
        }
        return integers;
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
            Collection<Identity> identities = new ArrayList<>();
            Collection<String> keys = allIdentityKeys(false);
            for (String key : keys) {
                String name = null, uuid = null, perms = null;
                boolean flag = false;
                try {
                    if (dbManager.get(key + ".flag").equals("0")) {
                        flag = true;
                    }
                    name = dbManager.get(key + ".name");
                    uuid = dbManager.get(key + ".uuid");
                    perms = dbManager.get(key + ".perms");
                }catch (NullPointerException e){
                    getLogger().error("cant read data from key: \""+ key + "\" -> " + e.getMessage());
                }
                if (flag)
                    identities.add(new Identity(name, UUID.fromString(uuid), IngwerPermission.decodePermissions(Long.parseLong(perms))));
                else
                    getLogger().warning("flag not matching for entry: \"" + key + "\". This data will be ignored!");
            }
            return identities;
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
    protected String nextIdentityKey(boolean withDot) {
        int max = 0;
        Collection<String> keys = allIdentityKeys(true);
        for (String key : keys) {
            String rem = key.replace(IDENTITY_PREFIX, "");
            String[] parts = rem.split("\\.");
            if (parts.length > 0) {
                int n = Integer.parseInt(parts[0]);
                if (n > max) max = n;
            } else {
                throw new InvalidParameterException(NAMESPACE + "::" + key + " -> ERROR!");
            }
        }
        return IDENTITY_PREFIX + (max + 1) + (withDot ? "." : "");
    }

    protected Collection<String> allIdentityKeys(boolean onlyOrigins) {
        Set<String> ret = new HashSet<>();
        for (NamespacedKey key : dbManager.getCurrentIngwerKeys()) {
            if (key.getKey().startsWith(IDENTITY_PREFIX)) {
                if (onlyOrigins) {
                    try {
                        ret.add(key.getKey().split("\\.")[1]);
                    } catch (IndexOutOfBoundsException ignored) {
                        getLogger().warning("malformed key: \"" + key.getKey() + "\" in our namespace!");
                    }
                } else {
                    ret.add(key.getKey());
                }
            }
        }
        return ret;
    }

    public NamespacedKey genNextIdentityKey() {
        return new NamespacedKey("ingwer", nextIdentityKey(false));
    }

    public NamespacedKey cloneAppend(NamespacedKey origin, String @NotNull ... sub) {
        StringJoiner joiner = new StringJoiner(".");
        joiner.add(origin.getKey());
        for (String s : sub) joiner.add(s);
        return new NamespacedKey(origin.namespace(), joiner.toString());
    }

    @ApiStatus.Internal
    protected void clean() {
        dbManager.clean();
        ;
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

    @ApiStatus.Internal
    public IngwerMessage getStatusMessage() {
        List<OneLinedMessage> messages = new ArrayList<>();
        if (Ingwer.getStorage() == this) {
            messages.add(MessageBuilder.empty().add(C.A, "Ingwer Storage").add(C.C, " is currently running ")
                    .add(C.A, "ChunkDB").add(C.C, "!").build());
            messages.add(MessageBuilder.empty().add(C.C, "Manager: ").add(C.A, dbManager.getClass().getSimpleName()).build());
            StringJoiner worlds = new StringJoiner(", ");
            dbManager.getWorlds().forEach(world -> worlds.add(world.getName()));
            messages.add(MessageBuilder.empty().add(C.C, "Worlds: ").add(C.A, worlds.toString()).build());
            StringJoiner chunks = new StringJoiner(",");
            dbManager.getWorlds().forEach(world -> chunks.add(Long.toString(getChunk.apply(world).getChunkKey())));
            messages.add(MessageBuilder.empty().add(C.C, "Chunks: ").add(C.A, chunks.toString()).build());
            messages.add(MessageBuilder.empty().add(C.C, "Keys: ").add(C.A, String.valueOf(dbManager.getCurrentIngwerKeys().size())).build());
            return new FramedMessage(messages);
        } else
            return MessageBuilder.prefixed().add(C.E, "Ingwer Storage is not running ChunkDB currently!").build();
    }

}
