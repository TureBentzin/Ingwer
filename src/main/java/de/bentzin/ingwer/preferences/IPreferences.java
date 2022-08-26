package de.bentzin.ingwer.preferences;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IPreferences {

    UUID superadmin();
    char prefix();



    //statics

    @Contract(value = "_ -> new", pure = true)
    static @NotNull IPreferences getDefaults(UUID superadmin) {
        return new IPreferences() {
            @Override
            public UUID superadmin() {
                return superadmin;
            }

            @Override
            public char prefix() {
                return '+';
            }
        };
    }
}
