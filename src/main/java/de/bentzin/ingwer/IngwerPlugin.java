package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.logging.JavaLogger;
import de.bentzin.ingwer.logging.PaperLogger;
import de.bentzin.ingwer.logging.SLF4JLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.utils.StopCode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

public class IngwerPlugin extends JavaPlugin {


    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Ingwer.start(new Preferences(Identity.DEVELOPER_UUID,'+',StartType.JAVA_PLUGIN_STANDALONE,
                null,new SLF4JLogger("Ingwer", LoggerFactory.getILoggerFactory().getLogger("Ingwer")),this));
    }

    @Override
    public void onDisable() {
        Ingwer.stop(StopCode.SHUTDOWN);
    }
}
