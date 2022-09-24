package de.bentzin.ingwer.features.integrated.vault;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultFeature extends SimpleFeature {

    private Economy econ = null;
    private Permission perms = null;
    private Chat chat = null;

    public VaultFeature() {
        super("Vault", "Permissions and more");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.TRUST;
    }

    @Override
    public void onEnable() {

        getLogger().info("setting up services for vault...");
        if (setupChat()) getLogger().warning("failed to setup chat!");
        if (setupEconomy()) getLogger().warning("failed to setup economy!");
        if (setupPermissions()) getLogger().warning("failed to setup permissions!");
        getLogger().info("setting up commands for Ingwer");

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        getLogger().info("checking for Vault...");
        if (isVault()) {
            Plugin vault = getVault();
            getLogger().info("found: " + vault.getName() + " v. " + vault.getDescription().getVersion());
            getVault().getLogger().info("Ingwer was injected successfully! (; ");
            return true;
        } else {
            getLogger().warning("vault could no be found!");
            return false;
        }
    }

    private boolean setupEconomy() {
        if (Ingwer.javaPlugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Ingwer.javaPlugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        if (Ingwer.javaPlugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Chat> rsp = Ingwer.javaPlugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
            return false;
        }
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        if (Ingwer.javaPlugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = Ingwer.javaPlugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean isVault() {
        return getVault() != null;
    }

    public Plugin getVault() {
        return Ingwer.javaPlugin.getServer().getPluginManager().getPlugin("Vault");
    }

    @NotNull
    public Chat getChat() throws UnsupportedOperationException {
        if (chat == null) {
            throw new UnsupportedOperationException("chat is not available!");
        }
        return chat;
    }

    @NotNull
    public Economy getEcon() throws UnsupportedOperationException {
        if (econ == null) {
            throw new UnsupportedOperationException("economy is not available!");
        }
        return econ;
    }

    @NotNull
    public Permission getPerms() throws UnsupportedOperationException {
        if (perms == null) {
            throw new UnsupportedOperationException("permissions is not available!");
        }
        return perms;
    }

}
