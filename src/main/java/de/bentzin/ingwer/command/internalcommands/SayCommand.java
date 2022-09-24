package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.command.paper.PaperEventListener;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class SayCommand extends IngwerCommand implements Permissioned, CommandUtils {
    public SayCommand() {
        super("say", "Say things starting with + into the chat");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, @NotNull CommandTarget senderType) {
        if (senderType.equals(CommandTarget.INGAME)) {
            if (commandSender instanceof Identity identity) {
                if (identity.getUUID() != null) {
                    Player player = Bukkit.getPlayer(identity.getUUID());
                    String gen = gen(cmd);
                    PaperEventListener.AUTHORIZED.add(gen);
                    Bukkit.getScheduler().runTask(Ingwer.javaPlugin, () -> player.chat(gen));
                }
            }
        }
    }


    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.INGAME};
    }

    @Override
    public @NotNull IngwerPermission getPermission() {
        return IngwerPermission.TRUST;
    }
}
