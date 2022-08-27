package de.bentzin.ingwer;

import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.preferences.StartType;
import de.bentzin.ingwer.utils.StopCode;
import org.bukkit.plugin.java.JavaPlugin;

public class IngwerPlugin extends JavaPlugin {


    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Ingwer.start(Preferences.getDefaults(Identity.DEVELOPER_UUID, StartType.JAVA_PLUGIN_STANDALONE));
    }

    @Override
    public void onDisable() {
        Ingwer.stop(StopCode.SHUTDOWN);
    }
}
