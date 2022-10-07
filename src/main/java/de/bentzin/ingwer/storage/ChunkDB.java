package de.bentzin.ingwer.storage;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.utils.LoggingClass;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 07.10.2022
 */
public class ChunkDB extends LoggingClass implements Storage {

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
        return null;
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
        return null;
    }

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
        private final Supplier<Collection<World>> worlds;


        protected ChunkDBManager(Supplier<Collection<World>> worlds) {
            this.worlds = worlds;
        }

        @Contract("_ -> new")
        public static @NotNull NamespacedKey genKey(String key) {
            return new NamespacedKey(Ingwer.javaPlugin, key);
        }

        public <T, Z> void save(NamespacedKey key, PersistentDataType<T, Z> dataType, Z data) {
            action(container -> {container.set(key,dataType,data);});
        }

        public <T, Z> Z get(NamespacedKey key, PersistentDataType<T, Z> dataType) {
            return Objects.requireNonNull(findBestMatch(key, dataType)).get(key,dataType);
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
         *
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
            if(!container.has(key)) {
                container.set(key,PersistentDataType.LONG,System.currentTimeMillis());
            }
            return container.get(key,PersistentDataType.LONG);
        }

        /**
         * @param key key to search for
         * @return first found container or null of no container was found
         */
        @Nullable
        protected PersistentDataContainer findBestMatch(NamespacedKey key) {
            for (PersistentDataContainer dataContainer : sortedChunkContainers()) {
                if(dataContainer.has(key)) {
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
        protected <T,Z> PersistentDataContainer findBestMatch(NamespacedKey key, PersistentDataType<T,Z> dataType) {
            for (PersistentDataContainer dataContainer : sortedChunkContainers()) {
                if(dataContainer.has(key, dataType)) {
                    return dataContainer;
                }
            }
            return null;
        }

        private void action(Consumer<PersistentDataContainer> action) {
            sortedChunkContainers().forEach(action);
        }

        private <R> Collection<R> collect(Function<PersistentDataContainer,R> action) {
            return sortedChunkContainers().stream().map(action).toList();
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
