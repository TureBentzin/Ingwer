package de.bentzin.ingwer.preferences;

import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.storage.Storage;
import de.bentzin.ingwer.storage.StorageProvider;
import de.bentzin.ingwer.storage.chunkdb.AsyncChunkDBManager;
import de.bentzin.ingwer.storage.chunkdb.ChunkDB;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record Preferences(@NotNull UUID superadmin, char prefix, @NotNull StartType startType,
                          @NotNull StorageProvider<? extends Storage> storageProvider,
                          @NotNull Logger ingwerLogger, @NotNull JavaPlugin javaPlugin,
                          boolean debug


) {


    public static @NotNull Preferences getDefaults(
            UUID superadmin, StartType startType, JavaPlugin javaPlugin) {

        return new Preferences(
                superadmin, '+', startType, ChunkDB.getProvider(AsyncChunkDBManager.getDefault()), new SystemLogger("Ingwer"), javaPlugin, false);
    }
}
