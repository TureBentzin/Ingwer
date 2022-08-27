package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.storage.Sqlite;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.utils.StopCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.net.URISyntaxException;

public class Ingwer {

    //TODO dynamic
    public static String VERSION_STRING = "0.1-BETA";


    public static String BANNER = "\n" +
            "██╗███╗░░██╗░██████╗░░██╗░░░░░░░██╗███████╗██████╗░\n" +
            "██║████╗░██║██╔════╝░░██║░░██╗░░██║██╔════╝██╔══██╗\n" +
            "██║██╔██╗██║██║░░██╗░░╚██╗████╗██╔╝█████╗░░██████╔╝\n" +
            "██║██║╚████║██║░░╚██╗░░████╔═████║░██╔══╝░░██╔══██╗\n" +
            "██║██║░╚███║╚██████╔╝░░╚██╔╝░╚██╔╝░███████╗██║░░██║\n" +
            "╚═╝╚═╝░░╚══╝░╚═════╝░░░░╚═╝░░░╚═╝░░╚══════╝╚═╝░░╚═╝\n" +
            "Ingwer v." + VERSION_STRING + " by Ture Bentzin \n";

    @UnknownNullability
    private static Preferences preferences;

    public static Preferences getPreferences() {
        return preferences;
    }

    private static IngwerThrower ingwerThrower;

    public static IngwerThrower getIngwerThrower() {
        return ingwerThrower;
    }

    private static Sqlite storage;

    public static Sqlite getStorage() {
        return storage;
    }

    @NotNull
    private static Logger logger;

    @NotNull
    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(@NotNull Logger logger) {
        Ingwer.logger = logger;
    }

    public static void start(@NotNull Preferences preferences) {
        Ingwer.preferences = preferences;

        setLogger(preferences.ingwerLogger());
        getLogger().info("Booting Ingwer v." + VERSION_STRING);

        getLogger().cosmetic(BANNER);

        //Boot
        ingwerThrower = new IngwerThrower();

        try {
            storage = new Sqlite();
        } catch (URISyntaxException e) {
            getIngwerThrower().accept(e);
        }

        if(preferences.hasCustomSqliteLocation())
         getStorage().setDb(preferences.custom_sqliteLocation());




        //END: Boot
        printLEGAL(new SystemLogger("LEGAL",getLogger()));
    }

    public static void stop(StopCode stopCode) {
        getStorage().close();
    }


    public static void main(String[] args) {
        //entrypoint for Ingwer
        start(Preferences.getDefaults(Identity.DEVELOPER_UUID, StartType.JAVA_PLUGIN_LIBRARY));

        //exit-point for Ingwer
        stop(StopCode.FINISHED);
    }


    protected static @NotNull String printLEGAL(@NotNull Logger logger) {
        String legal =
                    "-------------------------------------------------------------------------------------------- \n" +
                    "Ingwer v." + VERSION_STRING +" by Ture Bentzin \n"+
                    "Ingwer is a piece of educational Software meant to be used for educational purpose only.\n" +
                    "Ingwer can be used as admin software. You are only authorised to install / run / maintain Ingwer with the agreement \n" +
                            "of the authorised personal running the server Ingwer is being used on! \n" +
                            "-------------------------------------------------------------------------------------------- ";
                logger.cosmetic(legal);
        return legal;
    }

}
