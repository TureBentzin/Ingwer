package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.MiniMessageMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class WelcomeFeature extends SimpleFeature implements Listener {

    public static String[] WELCOME_MESSAGES = new String[]{
            "Welcome to Ingwer, Captain!",
            "Welcome to Ingwer, Boss!",
            "Welcome to Ingwer, Admin!",
            "Welcome back to Ingwer, Sir!",
            "Whats up for now? Ingwer is running!",
            "Sir, Ingwer is here for you to serve!",
            "Ingwer is running, Captain!",
            "Ready for the Ingwer-Show, Boss?",
            "Nice to see you, Ingwer is here for you!",
            "Welcome to Ingwer!"
    };


    public WelcomeFeature() {
        super("welcome", "Welcomes every ingwer user!");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.TRUST;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, Ingwer.javaPlugin);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return true;
    }

    @EventHandler
    public void onAdminJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Identity identity = Ingwer.getStorage().getIdentityByUUID(player.getUniqueId().toString());

        if (identity != null) {
            getLogger().debug("Allied join: " + identity.getName());
            if (identity.isEnabled()) {
                identity.sendMessage(getRandomMessage());
            }
        }
    }

    public IngwerMessage getRandomMessage() {
        Random random = new Random();
        int i = random.nextInt(WELCOME_MESSAGES.length);
        return new MiniMessageMessage("<gold>" + WELCOME_MESSAGES[i]);
    }
}
