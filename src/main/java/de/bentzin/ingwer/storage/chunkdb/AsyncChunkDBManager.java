package de.bentzin.ingwer.storage.chunkdb;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

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
    public static Supplier<ChunkDBManager> getDefault() {
        return () -> new AsyncChunkDBManager(Bukkit::getWorlds);
    }

    private final Map<NamespacedKey,String> storage = new HashMap<>();

    public AsyncChunkDBManager(Supplier<Collection<World>> worlds) {
        super(worlds);
    }

    @Override
    public void save(NamespacedKey key, String data) {

    }

    @Override
    public String get(NamespacedKey key) {
        return null;
    }

    @Override
    public boolean has(NamespacedKey key) {
        return false;
    }

    @Override
    public void remove(NamespacedKey key) {

    }

    @Override
    public void stop() {

    }

    @Override
    protected @Nullable PersistentDataContainer findBestMatch(NamespacedKey key) {
        return null;
    }

}
