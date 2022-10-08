package de.bentzin.ingwer.storage.chunkdb;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 08.10.2022
 */
@ApiStatus.Internal
class ChunkDBManager {

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
