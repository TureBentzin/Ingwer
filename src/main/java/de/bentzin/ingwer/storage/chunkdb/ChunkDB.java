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

    @ApiStatus.Internal
    protected ChunkDBManager dbManager() {
        return dbManager;
    }

    @Override
    public void init() {
        getLogger().info("running ChunkDB v." + VERSION_STRING);
        dbManager.updateLogger(getLogger().adopt("DBManager"));
        dbManager.start();
    }

    @Override
    public void lateInit() {
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
        String uuid = identity.getUUID().toString();
        // getLogger().debug("saving: " + identity);
        if (containsIdentityWithUUID(uuid)){
            updateIdentity(Objects.requireNonNull(getIdentityByUUID(uuid)),identity.getName(),identity.getUUID(),identity.getPermissions());
            getLogger().warning("someone tried to save identity that was already present: updating!");
            return getIdentityByUUID(uuid);
        }

        NamespacedKey origin = genNextIdentityKey();
        dbManager.save(cloneAppend(origin, "name"), identity.getName());
        dbManager.save(cloneAppend(origin, "uuid"), uuid);
        dbManager.save(cloneAppend(origin, "perms"), Long.toString(identity.getCodedPermissions()));
        dbManager.save(cloneAppend(origin, "flag"), "0");
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
        List<Identity> identities = getAllIdentities().stream().takeWhile(identity -> identity.getName().equals(name))
                .toList();
        if(identities.isEmpty()) return null;
        else return identities.get(0);
    }

    @Override
    public @Nullable Identity getIdentityByUUID(String uuid) {
        List<Identity> identities = getAllIdentities().stream().takeWhile(identity -> identity.getUUID().equals(UUID.fromString(uuid)))
                .toList();
        getLogger().debug("Extraxt: " + getAllIdentities().toString());
        if(identities.isEmpty()) return null;
        else return identities.get(0);
    }

    @Override
    public @NotNull Collection<Identity> getAllIdentities() {
            Collection<Identity> identities = new ArrayList<>();
            Collection<String> keys = allIdentityKeys(true);
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
        NamespacedKey key = getKeyOfIdentity(identity);
        if(key == null){
                throw new InvalidParameterException("the given identity is not in chunkDB!");
        }
        dbManager.remove(cloneAppend(key,"name"));
        dbManager.remove(cloneAppend(key,"uuid"));
        dbManager.remove(cloneAppend(key,"perms"));
        dbManager.remove(cloneAppend(key,"flag"));
    }

    @Override
    public boolean containsIdentityWithUUID(String uuid) {
        for (Identity allIdentity : getAllIdentities()) {
            if(allIdentity.getUUID().equals(UUID.fromString(uuid))){
                return true;
            }
        }
        return false;
    }

    /**
     * @changes Breaking change: now uses uuid to get key
     * @param identity
     * @return
     */
    private @Nullable NamespacedKey getKeyOfIdentity(@NotNull Identity identity) {
        if(getIdentityByUUID(String.valueOf(identity.getUUID())) != null) {
            Collection<String> keys = allIdentityKeys(false);
            int i = -1;
            for (String key : keys) {
                getLogger().debug(key);
                if (key.endsWith("uuid")) {
                    if (UUID.fromString(dbManager.get(key)).equals(identity.getUUID())) {
                        //MATCH!
                        String[] split = key.split("\\.");
                        i = Integer.parseInt(split[1]);
                    }
                }
            }
            if (i == -1) {
                throw new IllegalStateException("Please report this issue! <i is -1>");
            }
           return genKey(IDENTITY_PREFIX + i);
        }else
            return null;
    }

    @Override
    public Identity updateIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions) {
            NamespacedKey origin = getKeyOfIdentity(identity);
            if(origin == null)
                    throw new InvalidParameterException("the given identity is not in chunkDB!");

            dbManager.save(cloneAppend(origin, "name"), name);
            dbManager.save(cloneAppend(origin, "uuid"), String.valueOf(uuid));
            dbManager.save(cloneAppend(origin, "perms"), Long.toString(identity.getCodedPermissions()));
            dbManager.save(cloneAppend(origin, "flag"), Short.valueOf("0").toString());

            return getIdentityByUUID(String.valueOf(uuid)); //Backcheck
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
                        ret.add(IDENTITY_PREFIX + key.getKey().split("\\.")[1]); //yes bad code but works so...
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
            return MessageBuilder.prefixed().add(C.E, "Ingwer Storage is currently not running ChunkDB!").build();
    }

}
