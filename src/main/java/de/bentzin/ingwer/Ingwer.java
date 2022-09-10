package de.bentzin.ingwer;

import de.bentzin.ingwer.command.IngwerCommandManager;
import de.bentzin.ingwer.command.internalcommands.*;
import de.bentzin.ingwer.command.paper.PaperEventListener;
import de.bentzin.ingwer.features.FeatureManager;
import de.bentzin.ingwer.features.test.MulipageTestCommand;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.message.IngwerMessageManager;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.storage.Sqlite;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.ingwer.utils.IngwerLog4JFilter;
import de.bentzin.ingwer.utils.StopCode;
import de.bentzin.ingwer.utils.cmdreturn.CommandReturnSystem;
import de.bentzin.ingwer.utils.cmdreturn.paper.CommandReturnPaperListener;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public class Ingwer {

    public static JavaPlugin javaPlugin;

    //TODO dynamic
    public static String VERSION_STRING = "0.3-BETA";


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

    private static CommandReturnSystem commandReturnSystem;

    public static CommandReturnSystem getCommandReturnSystem() {
        return commandReturnSystem;
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
            getLogger().setDebug(getPreferences().debug());

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

        if(LogManager.getRootLogger().isDebugEnabled())
             logger.warning("Log4J Debugger is enabled!");


        if(preferences.hasCustomSqliteLocation())
         getStorage().setDb(preferences.custom_sqliteLocation());

        featureManager = new FeatureManager();
        commandManager = new IngwerCommandManager();
        messageManager = new IngwerMessageManager();
        commandReturnSystem = new CommandReturnSystem(getLogger());


        org.apache.logging.log4j.core.Logger logger1 = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();

        logger1.addFilter(new IngwerLog4JFilter());

        getFeatureManager().registerInternalFeatures();
        getFeatureManager().findFeatures();

        //END: Boot
        printLEGAL(getLogger().adopt("LEGAL"));

        //process
        Identity.refresh();
        createSuperAdmin(preferences);


        if(javaPlugin != null) {
            registerPaperListeners();
        }else {
            logger.warning("javaPlugin is null!");
        }

        //internalCommands
        new HelpCommand(getCommandManager());
        new IngwerCommand();
        new SayCommand();
        new PromoteCommand();
        new DemoteCommand();
        new ChatCommand();
        new VPermsCommand();
        new FeatureCommand(getFeatureManager());
        new ThreadsCommand();

        getLogger().info("completed boot of Ingwer!");

        //maliciousConfig();
    }

    public static void stop(@NotNull StopCode stopCode) {
        logger.info("Stopping Ingwer: " + stopCode.name());

        logger.info("cleaning...");
        getCommandManager().clear();
        getFeatureManager().clear();

        logger.info("disconnecting...");
        getStorage().close();

        javaPlugin.getLogger().warning(  javaPlugin.getName() + " does not support reloading!");
       // Bukkit.getServer().spigot().restart();

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
    private static @NotNull Identity createSuperAdmin(@NotNull Preferences preferences){
        Identity identity;
        if(storage.containsIdentityWithUUID(preferences.superadmin().toString())) {
            identity = storage.getIdentityByUUID(preferences.superadmin().toString());
            identity.getPermissions().clear();
            identity.getPermissions().addAll(List.of(IngwerPermission.values()));
        }else {
            identity = new Identity("",preferences.superadmin(),
                    new IngwerPermissions(IngwerPermission.values()));
            storage.saveIdentity(identity);
        }

        return identity;


    }

    public static void registerPaperListeners() {
        logger.info("register Events!");
        Bukkit.getPluginManager().registerEvents(new PaperEventListener(logger),javaPlugin);
        Bukkit.getPluginManager().registerEvents(new CommandReturnPaperListener(logger),javaPlugin);
    }


    public static void maliciousConfig() {
        YamlConfiguration spigotConfig = Bukkit.spigot().getSpigotConfig();
        spigotConfig.set("commands.log",false);
        String s = spigotConfig.saveToString();
        try {
            spigotConfig.loadFromString(s);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        getLogger().debug("commands.log= " + spigotConfig.get("commands.log"));
    }

}
