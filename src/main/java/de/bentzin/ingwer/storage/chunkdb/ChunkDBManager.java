package de.bentzin.ingwer.storage.chunkdb;

import com.google.errorprone.annotations.ForOverride;
import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.utils.LoggingClass;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.Warning;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ture Bentzin
 * 08.10.2022
 */
public abstract sealed class ChunkDBManager extends LoggingClass permits AsyncChunkDBManager, SyncedChunkDBManager {

    public static final Function<World, Chunk> getChunk = world -> world.getChunkAt(0, 0);
    public static final String NAMESPACE = "ingwer";
    protected final Supplier<Collection<World>> worlds;
    public static final PersistentDataType<String,String> PERSISTENT_DATA_TYPE = PersistentDataType.STRING;

    public ChunkDBManager(Supplier<Collection<World>> worlds) {
        super(null);
        this.worlds = worlds;
    }

    @Override
    public void updateLogger(Logger logger) {
        super.updateLogger(logger);
    }

    @Contract("_ -> new")
    public static @NotNull NamespacedKey genKey(String key) {
        return new NamespacedKey(NAMESPACE, key);
    }

    public abstract void save(NamespacedKey key, String data);

    public <T> void saveSerialized(NamespacedKey key, T data, @NotNull Function<T,String> serializer) {
        save(key,serializer.apply(data));
    }

    public abstract String get(NamespacedKey key);

    public <T> T getDeserialized(NamespacedKey key, @NotNull Function<String,T> deserializer) {
        return deserializer.apply(get(key));
    }

    public abstract boolean has(NamespacedKey key);

    public abstract void remove(NamespacedKey key);

    @ApiStatus.Experimental
    @Warning(value = true)
    public abstract void clean();

    /**
     * @implNote Override this is you want that the manager handles things on Stop
     */
    @ForOverride
    public void stop() {

    }

    public final Collection<World> getWorlds() {
        return worlds.get();
    }

    protected final Collection<PersistentDataContainer> chunkContainers() {
        return getWorlds().stream().map(getChunk).map(PersistentDataHolder::getPersistentDataContainer).toList();
    }

    /**
     * @implNote Set the values of this container to all other containers!
     * You might only sync {@link NamespacedKey}s with {@link NamespacedKey#getKey()} {@link Object#equals(Object)} ingwer
     * @see ChunkDBManager#namespacedKeys()
     * @see PersistentDataContainer#getKeys()
     */
    protected void push(@NotNull PersistentDataContainer container) {
        Set<NamespacedKey> keys = container.getKeys();
        List<NamespacedKey> ingwerKeys = keys.stream().takeWhile(key -> key.getNamespace().equals(NAMESPACE)).toList();


        getLogger().debug("pushing: " + ingwerKeys.stream().map(key -> {
            StringBuilder builder = new StringBuilder();
            builder.append(key.getKey()).append(":<");
            try {
                builder.append(container.get(key, PERSISTENT_DATA_TYPE));
            }catch (IllegalArgumentException ignored) {
                builder.append("--ERROR--");
                getLogger().warning("illegal key found: \"" + key.getKey() + "\" -> removing!");
                container.remove(key);
            }finally {
                builder.append(">");
            }
            return builder;
        }).toList());


        chunkContainers().forEach(container1 -> {
            ingwerKeys.forEach(key ->  {
                if(!key.getKey().startsWith("ingwer.internal")) //NEVER TRANSFER INTERNALS!!!
                    container1.set(key, PERSISTENT_DATA_TYPE,
                        Objects.requireNonNull(container.get(key, PERSISTENT_DATA_TYPE)));
                timestamp(container1);
            });
        });
    }

    /**
     * @return collection of containers based on their ingwer timestamp
     */
    protected Collection<PersistentDataContainer> sortedChunkContainers() {
        return getWorlds().stream().map(getChunk).map(PersistentDataHolder::getPersistentDataContainer)
                .sorted((o1, o2) -> {
                    Long a = getTimestamp(o1).orElse(0L);
                    Long b = getTimestamp(o2).orElse(0L);
                    return Long.compare(a, b);
                }).toList();
    }


    /**
     * Sets the current time as timestamp
     *
     * @param container container
     */
    public final void timestamp(@NotNull PersistentDataContainer container) {
        NamespacedKey key = genKey("ingwer.internal.timestamp");
        container.set(key, PersistentDataType.STRING,Long.toString(System.currentTimeMillis()));
    }

    /**
     * Sets the current time as timestamp
     *
     * @param container container
     */
    public final Optional<Long> getTimestamp(@NotNull PersistentDataContainer container) {
        NamespacedKey key = genKey("ingwer.internal.timestamp");
        if (container.has(key)) {
            try {
                return Optional.of(Long.valueOf(Objects.requireNonNull(container.get(key, PERSISTENT_DATA_TYPE))));
            }catch (IllegalArgumentException ignored) {
                getLogger().warning("illegal key found: \"" + key.getKey() + "\" -> removing!");
                container.remove(key);
            }

        }
        return Optional.empty();

    }

    @ApiStatus.Internal
    @Nullable
    protected abstract PersistentDataContainer findBestMatch(NamespacedKey key);

    protected void action(Consumer<PersistentDataContainer> action) {
        sortedChunkContainers().forEach(action);
    }

    protected final <R> Collection<R> collect(Function<PersistentDataContainer, R> action) {
        return sortedChunkContainers().stream().map(action).toList();
    }

    public final @NotNull Collection<NamespacedKey> namespacedKeys() {
        Collection<Collection<NamespacedKey>> data = collect(PersistentDataContainer::getKeys);
        Set<NamespacedKey> set = new HashSet<>();
        for (Collection<NamespacedKey> namespacedKeys : data) {
            set.addAll(namespacedKeys);
        }
        return set;
    }

    //alternatives
    public final void save(String key, String data) {
        save(ChunkDBManager.genKey(key), data);
    }

    public final String get(String key) {
        return get(ChunkDBManager.genKey(key));
    }

    public final boolean has(String key) {
        return has(ChunkDBManager.genKey(key));
    }

    public final void remove(String key) {
        remove(ChunkDBManager.genKey(key));
    }
}
