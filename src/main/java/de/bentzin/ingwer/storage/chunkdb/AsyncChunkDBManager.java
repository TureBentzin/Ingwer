package de.bentzin.ingwer.storage.chunkdb;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.Permission;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 08.10.2022
 */
public final class AsyncChunkDBManager extends ChunkDBManager {

    @Contract(" -> new")
    public static @NotNull Supplier<ChunkDBManager> getDefault() {
        return () -> new AsyncChunkDBManager(Bukkit::getWorlds);
    }

    private final Map<NamespacedKey,String> storage = new HashMap<>();

    public AsyncChunkDBManager(Supplier<Collection<World>> worlds) {
        super(worlds);
    }

    @Override
    public void save(NamespacedKey key, String data) {
        storage.put(key,data);
    }

    @Override
    public String get(NamespacedKey key) {
        return storage.get(key);
    }

    @Override
    public boolean has(NamespacedKey key) {
        return storage.containsKey(key);
    }

    @Override
    public void remove(NamespacedKey key) {
        storage.remove(key);
    }

    @Override
    public void clean() {
        storage.clear();
    }

    @Override
    public void stop() {
        push(bestContainer());
    }

    @Override
    public void start() {
        //TODO: load keys back into storage
    }

    @Contract(pure = true)
    @Override
    protected @NotNull Collection<NamespacedKey> getCurrentKeys(String namespace) {
        return storage.keySet();
    }
}
