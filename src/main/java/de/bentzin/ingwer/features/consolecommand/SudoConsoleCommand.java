package de.bentzin.ingwer.features.consolecommand;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.features.Feature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class
SudoConsoleCommand extends IngwerCommand implements Permissioned {
    private final Feature feature;

    public SudoConsoleCommand(Feature feature) {
        super("sudo-cons", "sudo commands in the console!");
        this.feature = feature;
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String @NotNull [] cmd, CommandTarget senderType) {
        if (cmd.length > 1) {

            StringBuilder s = new StringBuilder();
            for (int i = 0; i < cmd.length; i++) {
                if (i == 0) {
                    continue;
                }
                s.append(cmd[i]);
            }
            commandSender.sendMessage("run command as console: " + s);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.toString());
        } else {
            commandSender.sendMessage("you need to enter a command");
        }
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.INGAME};
    }

    @Override
    public @NotNull IngwerPermission getPermission() {
        return feature.generalUsePermission();
    }
}
