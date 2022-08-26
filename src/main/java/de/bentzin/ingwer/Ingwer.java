package de.bentzin.ingwer;

import de.bentzin.ingwer.preferences.Preferences;
import org.jetbrains.annotations.UnknownNullability;

public class Ingwer {


    @UnknownNullability
    private static Preferences preferences;

    public static Preferences getPreferences() {
        return preferences;
    }

    public static void start(Preferences preferences) {
        Ingwer.preferences = preferences;
    }


    public static void main(String[] args) {
        //entrypoint for Ingwer
        start(new Preferences());
    }

}
