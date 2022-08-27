package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
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
        start(Preferences.getDefaults(Identity.DEVELOPER_UUID, StartType.JAVA_PLUGIN_LIBRARY));
    }

}
