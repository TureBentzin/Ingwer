package de.tdrstudios.ingwer;

import com.sun.javafx.css.parser.StopConverter;
import de.tdrstudios.ingwer.enums.StartType;
import de.tdrstudios.ingwer.identity.AccessType;
import de.tdrstudios.ingwer.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.Permission;


public class Ingwer extends JavaPlugin {
    private static  Preferences preferences;

    public static Preferences getPreferences() {
        return preferences;
    }

    protected static void setPreferences(Preferences preferences) {
       Ingwer.preferences = preferences;
    }

    //Ingwer as JavaPlugin
    @Override
    /**
     * Start Ingwer as a JavaPlugin
     */
    public void onEnable() {
        // Plugin startup logic


    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * @apiNote Here is the Name "TDR_Minecraft" Hardcode but it will change soon!
     */
    @Override
    public void onLoad() {
        Preferences preferences = new Preferences(new Identity(Bukkit.getOfflinePlayer("TDR_Minecraft") , AccessType.ADMIN) , StartType.JAVA_PLUGIN);
        super.onLoad();
    }

    //Ingwer as Lib
    // STOPSHIP: 05.01.21

    public static void start(Preferences preferences) {
        Identity.setAdminIdentity(preferences.getAdminIdentity());
        setPreferences(preferences);
        
    }

    /**
     * @param stopcode
     * Codes:
     * 0: Normal
     * 1: Error
     * 2: Panic
     * 3: Java shutdown
     */
    public static void stop(int stopcode){
        
    }
    @Deprecated
    public static void panic() {
         stop(2);
    }


    /**
     *
     * @param args - this will be set by JavaVM
     * @apiNote This is only for Debug - You cant use it in Ingwer!
     */
    public static void main(String[] args) {
        // STOPSHIP: 05.01.21
        Identity identity = new Identity(Bukkit.getOfflinePlayer("TDR_Minecraft") , AccessType.ADMIN);
        // STOPSHIP: 06.01.21  
        Preferences preferences = new Preferences();
        preferences.setStartType(StartType.LIBARY);
        preferences.setAdminIdentity(identity);
        Ingwer.start(preferences);
    }

}
