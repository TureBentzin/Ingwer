package de.bentzin.ingwer.storage.chunkdb;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 08.10.2022
 */
@ApiStatus.Internal
class SyncedChunkDBManager extends ChunkDBManager {


    protected SyncedChunkDBManager(Supplier<Collection<World>> worlds) {
        super(worlds);
    }

    @Override
    public void save(NamespacedKey key, String data) {
        action(container -> {
            container.set(key, PERSISTENT_DATA_TYPE, data);
            timestamp(container);
        });
    }

    @Override
    public String get(NamespacedKey key) {
        return Objects.requireNonNull(findBestMatch(key)).get(key,PERSISTENT_DATA_TYPE);
    }

    @Override
    public boolean has(NamespacedKey key) {
        Collection<Boolean> collect = collect(persistentDataContainer -> persistentDataContainer.has(key, PERSISTENT_DATA_TYPE));
        return collect.contains(true);
    }

    @Override
    public void remove(NamespacedKey key) {
        action(container -> container.remove(key));
    }



    @Deprecated
    public long getElseSetTimeStamp(@NotNull PersistentDataContainer container) {
       return getTimestamp(container).or(() -> {
            timestamp(container);
            return getTimestamp(container);
        }).orElseThrow();
    }

    /**
     * @param key key to search for
     * @return first found container or null of no container was found
     */
    @Override
    @Nullable
    protected PersistentDataContainer findBestMatch(NamespacedKey key) {
        for (PersistentDataContainer dataContainer : sortedChunkContainers()) {
            if (dataContainer.has(key)) {
                return dataContainer;
            }
        }
        return null;
    }

}
