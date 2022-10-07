package de.bentzin.ingwer.preferences;

import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.storage.Storage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public record Preferences(UUID superadmin, char prefix, StartType startType,
                          Storage storageProvider,
                          @NotNull Logger ingwerLogger, JavaPlugin javaPlugin,
                          boolean debug


) {


    public static @NotNull Preferences getDefaults(
            UUID superadmin, StartType startType, JavaPlugin javaPlugin) {


        return new Preferences(
                superadmin, '+', startType, null, new SystemLogger("Ingwer"), javaPlugin, false);
    }
}
