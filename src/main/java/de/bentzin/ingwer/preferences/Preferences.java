package de.bentzin.ingwer.preferences;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record Preferences(UUID superadmin, char prefix, StartType startType) {



    public static @NotNull Preferences getDefaults(
            UUID superadmin, StartType startType) {


        return new Preferences(
                superadmin,'+', startType);
    }
}
