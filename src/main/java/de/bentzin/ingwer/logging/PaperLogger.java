package de.bentzin.ingwer.logging;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PaperLogger extends JavaLogger{

    public PaperLogger(String name, @NotNull Logger parent, @NotNull JavaPlugin javaPlugin) {
        super(name,parent, javaPlugin.getLogger());
    }

    public PaperLogger(String name, @NotNull JavaPlugin javaPlugin) {
        super(name, javaPlugin.getLogger());
    }
}
