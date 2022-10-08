package de.bentzin.ingwer.storage.chunkdb;

import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.storage.Storage;
import de.bentzin.ingwer.storage.StorageProvider;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
    protected @Nullable PersistentDataContainer findBestMatch(NamespacedKey key) {
        return null;
    }

}
