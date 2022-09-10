package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.logging.ApacheLogger;
import de.bentzin.ingwer.logging.JavaLogger;
import de.bentzin.ingwer.logging.PaperLogger;
import de.bentzin.ingwer.logging.SLF4JLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.utils.StopCode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.internal.LogManagerStatus;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class IngwerPlugin extends JavaPlugin {


    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Filter filter = new Filter() {
            /**
             * Check if a given log record should be published.
             *
             * @param record a LogRecord
             * @return true if the log record should be published.
             */
            @Override
            public boolean isLoggable(@NotNull LogRecord record) {
                getLogger().info("*" +record.getMessage());
                if(record.getMessage().contains("issued server command")) {
                    getLogger().warning("suppressed: " + record.getLoggerName() + " -> " + record.getMessage());
                    return false;
                }
                return true;
            }
        };

        /*
        Logger.getGlobal().setFilter(filter);
        Logger.getAnonymousLogger().setFilter(filter);
        Bukkit.getLogger().setFilter(filter);
        LogBuilder logBuilder = LogManager.getRootLogger4J().atInfo();
        org.apache.logging.log4j.Logger rootLogger1 = LogManager.getRootLogger4J();
        org.slf4j.Logger rootLogger = (org.slf4j.Logger) LogManager.getRootLogger4J()
         */

        //setProp
       // System.getProperties().setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level",""); //enable log4J Debug


        Ingwer.start(new Preferences(Identity.DEVELOPER_UUID,'+',StartType.JAVA_PLUGIN_STANDALONE,
                null,new ApacheLogger("Ingwer",LogManager.getRootLogger()),this, true));
    }

    @Override
    public void onDisable() {
        Ingwer.stop(StopCode.SHUTDOWN);
    }
}
