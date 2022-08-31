package de.bentzin.ingwer;

import de.bentzin.ingwer.command.IngwerCommandManager;
import de.bentzin.ingwer.command.paper.PaperEventListener;
import de.bentzin.ingwer.features.FeatureManager;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.message.IngwerMessageManager;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.storage.Sqlite;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.ingwer.utils.StopCode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class Ingwer {

    public static JavaPlugin javaPlugin;

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

    private static IngwerMessageManager messageManager;

    public static IngwerMessageManager getMessageManager() {
        return messageManager;
    }

    private static FeatureManager featureManager;

    public static FeatureManager getFeatureManager() {
        return featureManager;
    }

    private static IngwerCommandManager commandManager;

    public static IngwerCommandManager getCommandManager() {
        return commandManager;
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
        javaPlugin = preferences.javaPlugin();


        getLogger().cosmetic(BANNER);

        //Boot
        ingwerThrower = new IngwerThrower();

        try {
            storage = new Sqlite();
        } catch (URISyntaxException e) {
            getIngwerThrower().accept(e);
        } catch (SQLException e) {
            getIngwerThrower().accept(e, ThrowType.STORAGE);
        } catch (IOException e) {
            getIngwerThrower().accept(e);
        }

        if(preferences.hasCustomSqliteLocation())
         getStorage().setDb(preferences.custom_sqliteLocation());

        featureManager = new FeatureManager();
        commandManager = new IngwerCommandManager();
        messageManager = new IngwerMessageManager();


        getFeatureManager().registerInternalFeatures();
        getFeatureManager().findFeatures();

        //END: Boot
        printLEGAL(getLogger().adopt("LEGAL"));

        //process
        Identity.refresh();
        createSuperAdmin(preferences);


        if(javaPlugin != null) {
            registerPaperListeners();
        }

    }

    public static void stop(@NotNull StopCode stopCode) {
        logger.info("Stopping Ingwer: " + stopCode.name());
        getStorage().close();

        logger.info("cleaning...");
        getCommandManager().clear();
        getFeatureManager().clear();

    }


    public static void main(String[] args) {
        //entrypoint for Ingwer
        start(Preferences.getDefaults(Identity.DEVELOPER_UUID, StartType.JAVA_PLUGIN_LIBRARY, null));
        //exit-point for Ingwer
        stop(StopCode.FINISHED);
    }


    protected static @NotNull String printLEGAL(@NotNull Logger logger) {
        String legal =
                    "\n ------------------------------------------------------------------------------------------------------------------ \n" +
                    "   Ingwer v." + VERSION_STRING +" by Ture Bentzin \n"+
                    "   Ingwer is a piece of educational Software meant to be used for educational purpose only.\n" +
                    "   Ingwer can be used as admin software. You are only authorised to install / run / maintain Ingwer with the agreement \n" +
                            "   of the authorised personal running the server Ingwer is being used on! \n" +
                            " ------------------------------------------------------------------------------------------------------------------";
                logger.cosmetic(legal);
        return legal;
    }

    @Contract(pure = true)
    private static  Identity createSuperAdmin(@NotNull Preferences preferences){
        if(storage.containsIdentityWithUUID(preferences.superadmin().toString())) {
          return storage.getIdentityByUUID(preferences.superadmin().toString());
        }else {
            Identity identity = new Identity("",preferences.superadmin(),
                    new IngwerPermissions(IngwerPermission.values()));
            storage.saveIdentity(identity);
            return identity;
        }


    }

    public static void registerPaperListeners() {
        logger.info("register Events!");
        Bukkit.getPluginManager().registerEvents(new PaperEventListener(logger),javaPlugin);
    }

}
