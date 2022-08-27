package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.storage.Sqlite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class Ingwer {

    //TODO dynamic
    public static String VERSION_STRING = "0.1-BETA";


    @UnknownNullability
    private static Preferences preferences;

    public static Preferences getPreferences() {
        return preferences;
    }

    public static void start(@NotNull Preferences preferences) {
        Ingwer.preferences = preferences;

        if(preferences.hasCustomSqliteLocation())
         Sqlite.setDb(preferences.custom_sqliteLocation());
    }


    public static void main(String[] args) {
        //entrypoint for Ingwer
        start(Preferences.getDefaults(Identity.DEVELOPER_UUID, StartType.JAVA_PLUGIN_LIBRARY));
    }

}
