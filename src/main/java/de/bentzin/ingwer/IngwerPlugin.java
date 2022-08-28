package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.logging.JavaLogger;
import de.bentzin.ingwer.logging.PaperLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.utils.StopCode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class IngwerPlugin extends JavaPlugin {


    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Ingwer.start(new Preferences(Identity.DEVELOPER_UUID,'+',StartType.JAVA_PLUGIN_STANDALONE,
                null,new JavaLogger("Ingwer", Bukkit.getServer().getLogger()),this));
    }

    @Override
    public void onDisable() {
        Ingwer.stop(StopCode.SHUTDOWN);
    }
}
