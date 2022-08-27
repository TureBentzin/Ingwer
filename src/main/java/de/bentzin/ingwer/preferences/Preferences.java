package de.bentzin.ingwer.preferences;

import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public record Preferences(UUID superadmin, char prefix, StartType startType,
                          @Nullable File custom_sqliteLocation,
                          @NotNull Logger ingwerLogger




                          ) {


    public boolean hasCustomSqliteLocation() {
        return custom_sqliteLocation != null;
    }

    public static @NotNull Preferences getDefaults(
            UUID superadmin, StartType startType) {


        return new Preferences(
                superadmin,'+', startType,null, new SystemLogger("Ingwer"));
    }
}
