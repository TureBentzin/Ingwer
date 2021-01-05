package de.tdrstudios.ingwer;

import de.tdrstudios.ingwer.identity.Identity;
import org.bukkit.plugin.java.JavaPlugin;

public class Ingwer extends JavaPlugin {


    //Ingwer as JavaPlugin
    @Override
    /**
     * Start Inwer as a JavaPlugin
     */
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    //Ingwer as Lib

    private Identity admin = Identity.getAdminIdentity();

}
