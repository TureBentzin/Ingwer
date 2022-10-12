package de.bentzin.ingwer;

import de.bentzin.ingwer.command.IngwerCommandManager;
import de.bentzin.ingwer.command.internalcommands.*;
import de.bentzin.ingwer.command.paper.PaperEventListener;
import de.bentzin.ingwer.features.FeatureManager;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.message.IngwerMessageManager;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.storage.Storage;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import de.bentzin.ingwer.utils.IngwerLog4JFilter;
import de.bentzin.ingwer.utils.StopCode;
import de.bentzin.ingwer.utils.cmdreturn.CommandReturnSystem;
import de.bentzin.ingwer.utils.cmdreturn.paper.CommandReturnPaperListener;
import de.bentzin.tools.console.Console;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public final class Ingwer {

    //TODO dynamic
    public static final String VERSION_STRING = "0.5-BETA";
    public static final String BANNER = "\n" +
            "██╗███╗░░██╗░██████╗░░██╗░░░░░░░██╗███████╗██████╗░\n" +
            "██║████╗░██║██╔════╝░░██║░░██╗░░██║██╔════╝██╔══██╗\n" +
            "██║██╔██╗██║██║░░██╗░░╚██╗████╗██╔╝█████╗░░██████╔╝\n" +
            "██║██║╚████║██║░░╚██╗░░████╔═████║░██╔══╝░░██╔══██╗\n" +
            "██║██║░╚███║╚██████╔╝░░╚██╔╝░╚██╔╝░███████╗██║░░██║\n" +
            "╚═╝╚═╝░░╚══╝░╚═════╝░░░░╚═╝░░░╚═╝░░╚══════╝╚═╝░░╚═╝\n" +
            "Ingwer v." + VERSION_STRING + " by Ture Bentzin \n";
    public static JavaPlugin javaPlugin;
    @UnknownNullability
    private static Preferences preferences;
    private static IngwerThrower ingwerThrower;
    private static IngwerMessageManager messageManager;
    private static FeatureManager featureManager;
    private static IngwerCommandManager commandManager;
    private static CommandReturnSystem commandReturnSystem;
    private static Storage storage;
    private static Logger logger;
    private static Logger nullLogger;
    private static boolean debug = false;

    public static Preferences getPreferences() {
        return preferences;
    }

    public static IngwerThrower getIngwerThrower() {
        return ingwerThrower;
    }

    public static IngwerMessageManager getMessageManager() {
        return messageManager;
    }

    public static FeatureManager getFeatureManager() {
        return featureManager;
    }

    public static IngwerCommandManager getCommandManager() {
        return commandManager;
    }

    public static CommandReturnSystem getCommandReturnSystem() {
        return commandReturnSystem;
    }

    public static Storage getStorage() {
        return storage;
    }

    @NotNull
    public static Logger getLogger() {
        return logger;
    }

    @NotNull
    public static Logger getNullLogger() {
        if(nullLogger == null)
            throw new IllegalStateException("Okay that should never happen, but the \"null-logger\" is null. yes actually....");
        else
            return nullLogger;
    }

    public static void setLogger(@NotNull Logger logger) {
        Ingwer.logger = logger;
    }

    public static boolean isGlobalDebug() {
        return debug;
    }

    public static void setGlobalDebug(boolean debug) {
        Ingwer.debug = debug;
    }

    public static void start(@NotNull Preferences preferences) {
        try {

            Ingwer.preferences = preferences;

            setLogger(preferences.ingwerLogger());
            getLogger().setDebug(getPreferences().debug());

            //NullLogger
            nullLogger = getLogger().adopt("not-available");

            setGlobalDebug(getPreferences().debug());

            //Console
            Console.silent = !preferences.debug();

            getLogger().info("Booting Ingwer v." + VERSION_STRING);

            //WARN DEBUG
            if(isGlobalDebug())
                getLogger().warning("Warning! Ingwers debugmode is currently active! Only use this with caution because this may result into unwanted logger output!" +
                    "If you dont know what this message is about and you are using a pre compiled version of Ingwer then consider reporting this to your Ingwer-Provider!");

            javaPlugin = preferences.javaPlugin();
            getLogger().cosmetic(BANNER);

            //Bootstrap
            ingwerThrower = new IngwerThrower();

            try {
                storage = preferences.storageProvider().getAndInit();
            } catch (Exception e) {
                getIngwerThrower().accept(e, ThrowType.STORAGE);
            } finally {
                getLogger().info("finished crafting of Ingwer Storage!");
            }

            if (LogManager.getRootLogger().isDebugEnabled())
                logger.warning("Log4J Debugger is enabled!");

            featureManager = new FeatureManager();
            commandManager = new IngwerCommandManager();
            messageManager = new IngwerMessageManager();
            commandReturnSystem = new CommandReturnSystem(getLogger());


            final org.apache.logging.log4j.core.Logger rootLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();

            rootLogger.addFilter(new IngwerLog4JFilter());

            getFeatureManager().registerInternalFeatures();
            getFeatureManager().findFeatures();

            //END: Boot
            printLEGAL(getLogger().adopt("LEGAL"));

            //noinspection ResultOfMethodCallIgnored
            createSuperAdmin(preferences);


            if (javaPlugin != null) {
                registerPaperListeners();
            } else {
                logger.warning("javaPlugin is null!");
                stop(StopCode.FATAL);
                return;
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
            new OpCommand();
            new ThreadsCommand();

            //node
            new NodeTestCommand();

            //lateInits
            storage.lateInit();

            getLogger().info("completed boot of Ingwer!");

            //maliciousConfig();
        } catch (Throwable throwable) {
            IngwerThrower.acceptS(throwable, ThrowType.GENERAL);
        }
    }

    public static void stop(@NotNull StopCode stopCode) {
        logger.info("Stopping Ingwer: " + stopCode.name());

        if (stopCode.equals(StopCode.FATAL)) {
            logger.error("Fatal error accord that prohibits Ingwer from remaining in service. Watch out for errors or warnings registered before Ingwers shutdown procedure! You may report this error to Ingwer on GitHub!");
        }

        logger.info("cleaning...");
        getCommandManager().clear();
        getFeatureManager().clear();

        logger.info("closing storage ...");
        getStorage().close();

        if (!stopCode.equals(StopCode.FATAL)) {
            javaPlugin.getLogger().warning(javaPlugin.getName() + " may not support reloading!");
        }
        // Bukkit.getServer().spigot().restart();

    }


    public static void main(String[] args) {
        //entrypoint for Ingwer
        start(Preferences.getDefaults(Identity.DEVELOPER_UUID, StartType.JAVA_PLUGIN_LIBRARY, null));
        //exit-point for Ingwer
        stop(StopCode.FINISHED);
    }


    private static @NotNull String printLEGAL(@NotNull Logger logger) {
        String legal =
                "\n ------------------------------------------------------------------------------------------------------------------ \n" +
                        "   Ingwer v." + VERSION_STRING + " by Ture Bentzin \n" +
                        "   Ingwer is a piece of educational Software meant to be used for educational purpose only.\n" +
                        "   Ingwer can be used as admin software. You are only authorised to install / run / maintain Ingwer with the agreement \n" +
                        "   of the authorised personal running the server Ingwer is being used on! \n" +
                        " ------------------------------------------------------------------------------------------------------------------";
        logger.cosmetic(legal);
        return legal;
    }

    @Contract(pure = true)
    private static @NotNull Identity createSuperAdmin(@NotNull Preferences preferences) {
        Identity identity;
        if (storage.containsIdentityWithUUID(preferences.superadmin().toString())) {
            identity = storage.getIdentityByUUID(preferences.superadmin().toString());
            identity.getPermissions().clear();
            identity.getPermissions().addAll(List.of(IngwerPermission.values()));
        } else {
            identity = new Identity("", preferences.superadmin(),
                    new IngwerPermissions(IngwerPermission.values()));
            storage.saveIdentity(identity);
        }

        return identity;


    }

    public static void registerPaperListeners() {
        logger.info("register Events!");
        Bukkit.getPluginManager().registerEvents(new PaperEventListener(logger), javaPlugin);
        Bukkit.getPluginManager().registerEvents(new CommandReturnPaperListener(logger), javaPlugin);
    }


    public static void maliciousConfig() {
        YamlConfiguration spigotConfig = Bukkit.spigot().getSpigotConfig();
        spigotConfig.set("commands.log", false);
        String s = spigotConfig.saveToString();
        try {
            spigotConfig.loadFromString(s);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        getLogger().debug("commands.log= " + spigotConfig.get("commands.log"));
    }

}
