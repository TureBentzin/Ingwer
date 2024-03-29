package de.bentzin.ingwer.storage.chunkdb;

import com.google.common.annotations.Beta;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 08.10.2022
 */
@ApiStatus.Internal
public final class SyncedChunkDBManager extends ChunkDBManager {


    @Contract(" -> new")
    public static Supplier<ChunkDBManager> getDefault() {
        return () -> new SyncedChunkDBManager(Bukkit::getWorlds);
    }

    public SyncedChunkDBManager(Supplier<Collection<World>> worlds) {
        super(worlds);
    }

    @Override
    public void save(NamespacedKey key, String data) {
        onRecentContainer(container -> {
            container.set(key, PERSISTENT_DATA_TYPE, data);
            push(container);
        });
    }

    @Override
    @NotNull
    public String get(NamespacedKey key) {
        return Objects.requireNonNull(getMostRecentContainer().orElseThrow().get(key, PERSISTENT_DATA_TYPE));
    }

    @Override
    public boolean has(NamespacedKey key) {
        Collection<Boolean> collect = collect(persistentDataContainer -> persistentDataContainer.has(key, PERSISTENT_DATA_TYPE));
        return collect.contains(true);
    }

    @Override
    public void remove(NamespacedKey key) {
        onRecentContainer(container -> {
            container.remove(key);
            push(container);
        });

    }

    @Override
    public void clean() {
        //TODO ConcurrentModificationException
        for (NamespacedKey key : getMostRecentContainer().orElseThrow().getKeys()) {
            if(key.getNamespace().equals(NAMESPACE)) {
                remove(key);
            }
        }
    }

    @Override
    protected Collection<NamespacedKey> getCurrentKeys(@NotNull String namespace) {
        return streamKeysWithNamespace(bestContainer(), namespace).toList();
    }


    @Deprecated
    @ApiStatus.Internal
    public long getElseSetTimeStamp(@NotNull PersistentDataContainer container) {
       return getTimestamp(container).or(() -> {
            timestamp(container);
            return getTimestamp(container);
        }).orElseThrow();
    }




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
