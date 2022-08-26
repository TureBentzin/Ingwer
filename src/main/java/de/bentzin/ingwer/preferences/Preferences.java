package de.bentzin.ingwer.preferences;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record Preferences(UUID superadmin, char prefix) {


    @Contract("_ -> new")
    public static @NotNull Preferences getDefaults(UUID superadmin) {
        return new Preferences(superadmin,'+');
    }
}
