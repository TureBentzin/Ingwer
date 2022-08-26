package de.bentzin.ingwer.preferences;

import java.util.UUID;

public class Preferences implements IPreferences {


    private UUID superadmin;
    private char prefix;

    public Preferences(UUID superadmin, char prefix) {
        this.superadmin = superadmin;
        this.prefix = prefix;
    }

    @Override
    public UUID superadmin() {
        return superadmin;
    }

    @Override
    public char prefix() {
        return prefix;
    }
}
