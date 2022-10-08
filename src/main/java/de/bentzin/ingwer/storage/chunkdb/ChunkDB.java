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
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.bentzin.ingwer.storage.chunkdb.ChunkDB.ChunkDBManager.NAMESPACE;
import static de.bentzin.ingwer.storage.chunkdb.ChunkDB.ChunkDBManager.genKey;


/**
 * @author Ture Bentzin
 * 07.10.2022
 */
@ApiStatus.Experimental
public class ChunkDB extends LoggingClass implements Storage {

    public final String IDENTITY_PREFIX = "identities.";
    private final ChunkDBManager dbManager = new ChunkDBManager(Bukkit::getWorlds);
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
        dbManager.save(cloneAppend(origin,"name"),PersistentDataType.STRING,identity.getName());
        dbManager.save(cloneAppend(origin,"uuid"),PersistentDataType.STRING,identity.getUUID().toString());
        dbManager.save(cloneAppend(origin,"perms"),PersistentDataType.LONG,identity.getCodedPermissions());
        dbManager.save(cloneAppend(origin,"flag"),PersistentDataType.SHORT,Short.valueOf("0"));
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
            PersistentDataContainer bestMatch = dbManager.findBestMatch(cloneAppend(key, ""));
            if (bestMatch == null) return null;
            String name = bestMatch.get(cloneAppend(key, "name"), PersistentDataType.STRING);
            UUID uuid = UUID.fromString(bestMatch.get(cloneAppend(key, "uuid"), PersistentDataType.STRING));
            long coded = bestMatch.get(cloneAppend(key, "perms"), PersistentDataType.LONG);
            IngwerPermissions ingwerPermissions = IngwerPermission.decodePermissions(coded);
            return new Identity(name, uuid, ingwerPermissions);
        }catch (Exception e) {
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



    }

    @Override
    public Identity updateIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions) {
        return null;
    }

    /**
     *
     * @return IDENTITY_PREFIX + n+1 + "." -> "identities.3."
     */
    protected String nextIdentityKey() {
        int max = 0;
        for (NamespacedKey namespacedKey : dbManager.namespacedKeys()) {
            if (namespacedKey.getNamespace().equals(NAMESPACE)) {
                if(namespacedKey.getKey().startsWith(IDENTITY_PREFIX)) {
                    String rem = namespacedKey.getKey().replace(IDENTITY_PREFIX,"");
                    String[] parts = rem.split("\\.");
                    if(parts.length > 1) {
                        int n = Integer.parseInt(parts[0]);
                        if(n > max) max = n;
                    }else {
                        throw new InvalidParameterException(namespacedKey.getNamespace() + "::" + namespacedKey.getKey() + " -> ERROR!");
                    }
                }
            }
        }
        return IDENTITY_PREFIX + max+1 + ".";
    }

    public NamespacedKey genNextIdentityKey() {
        return new NamespacedKey("ingwer",nextIdentityKey());
    }

    public NamespacedKey cloneAppend(NamespacedKey origin, String @NotNull ... sub) {
        StringJoiner joiner = new StringJoiner(".");
        for (String s : sub) joiner.add(s);
        return new NamespacedKey(origin.namespace(), joiner.toString());
    }

    /**
     * @param identity
     * @param name
     * @param uuid
     * @param ingwerPermissions
     * @implNote If Identity is not present this will create a new one based on the given SINGLE parameters. In this case the given Identity would not be used or changed!!!
     */
    @Override
    public Identity updateOrSaveIdentity(@NotNull Identity identity, String name, @NotNull UUID uuid, IngwerPermissions ingwerPermissions) {
        return null;
    }


    @ApiStatus.Internal
    protected static class ChunkDBManager {

        public static final Function<World, Chunk> getChunk = world -> world.getChunkAt(0, 0);
        public static final String NAMESPACE = "ingwer";
        private final Supplier<Collection<World>> worlds;


        protected ChunkDBManager(Supplier<Collection<World>> worlds) {
            this.worlds = worlds;
        }

        @Contract("_ -> new")
        public static @NotNull NamespacedKey genKey(String key) {
            return new NamespacedKey(NAMESPACE, key);
        }

        public <T, Z> void save(NamespacedKey key, PersistentDataType<T, Z> dataType, Z data) {
            action(container -> {
                container.set(key, dataType, data);
            });
        }

        public <T, Z> Z get(NamespacedKey key, PersistentDataType<T, Z> dataType) {
            return Objects.requireNonNull(findBestMatch(key, dataType)).get(key, dataType);
        }

        public <T, Z> boolean has(NamespacedKey key, PersistentDataType<T, Z> dataType) {
            Collection<Boolean> collect = collect(persistentDataContainer -> persistentDataContainer.has(key, dataType));
            return collect.contains(true);
        }

        public void remove(NamespacedKey key) {
            action(container -> container.remove(key));
        }


        public Collection<World> getWorlds() {
            return worlds.get();
        }

        protected Collection<PersistentDataContainer> chunkContainers() {
            return getWorlds().stream().map(getChunk).map(PersistentDataHolder::getPersistentDataContainer).toList();
        }

        /**
         * @return collection of containers based on their ingwer timestamp
         */
        protected Collection<PersistentDataContainer> sortedChunkContainers() {
            return getWorlds().stream().map(getChunk).map(PersistentDataHolder::getPersistentDataContainer)
                    .sorted((o1, o2) -> {
                        long a = getElseSetTimeStamp(o1);
                        long b = getElseSetTimeStamp(o2);
                        return Long.compare(a, b);
                    }).toList();
        }


        public long getElseSetTimeStamp(@NotNull PersistentDataContainer container) {
            NamespacedKey key = genKey("ingwer.internal.timestamp");
            if (!container.has(key)) {
                container.set(key, PersistentDataType.LONG, System.currentTimeMillis());
            }
            return container.get(key, PersistentDataType.LONG);
        }

        /**
         * @param key key to search for
         * @return first found container or null of no container was found
         */
        @Nullable
        protected PersistentDataContainer findBestMatch(NamespacedKey key) {
            for (PersistentDataContainer dataContainer : sortedChunkContainers()) {
                if (dataContainer.has(key)) {
                    return dataContainer;
                }
            }
            return null;
        }

        /**
         * @param key key to search for
         * @return first found container or null of no container was found
         */
        @Nullable
        protected <T, Z> PersistentDataContainer findBestMatch(NamespacedKey key, PersistentDataType<T, Z> dataType) {
            for (PersistentDataContainer dataContainer : sortedChunkContainers()) {
                if (dataContainer.has(key, dataType)) {
                    return dataContainer;
                }
            }
            return null;
        }

        private void action(Consumer<PersistentDataContainer> action) {
            sortedChunkContainers().forEach(action);
        }

        private <R> Collection<R> collect(Function<PersistentDataContainer, R> action) {
            return sortedChunkContainers().stream().map(action).toList();
        }

        public Collection<NamespacedKey> namespacedKeys() {
            Collection<Collection<NamespacedKey>> data = collect(PersistentDataContainer::getKeys);
            Set<NamespacedKey> set = new HashSet<>();
            for (Collection<NamespacedKey> namespacedKeys : data) {
                set.addAll(namespacedKeys);
            }
            return set;
        }

        //alternatives
        public <T, Z> void save(String key, PersistentDataType<T, Z> dataType, Z data) {
            save(genKey(key), dataType, data);
        }

        public <T, Z> Z get(String key, PersistentDataType<T, Z> dataType) {
            return get(genKey(key), dataType);
        }

        public <T, Z> boolean has(String key, PersistentDataType<T, Z> dataType) {
            return has(genKey(key), dataType);
        }

        public <T, Z> void remove(String key) {
            remove(genKey(key));
        }
    }
}
