package de.bentzin.ingwer.preferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public record Preferences(UUID superadmin, char prefix, StartType startType,@Nullable File custom_sqliteLocation) {


    public boolean hasCustomSqliteLocation() {
        return custom_sqliteLocation != null;
    }

    public static @NotNull Preferences getDefaults(
            UUID superadmin, StartType startType) {


        return new Preferences(
                superadmin,'+', startType,null);
    }
}
