package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DemoteCommand extends IngwerCommand implements Permissioned, CommandUtils {

    private final OneLinedMessage specify_online_player = MessageBuilder.prefixed().add(C.E, "Please specify an online Player!").build();

    private final OneLinedMessage is_superadmin = MessageBuilder.prefixed().add(C.E, "You cant change permissions of this Identity.").build();

    private final OneLinedMessage no_IngwerUser = MessageBuilder.prefixed().add(C.E, "The specified Player isn't an Ingwer user at the moment.").build();


    public DemoteCommand() {
        super("demote", "Demotes a user within the Ingwer hierarchy");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, @NotNull CommandTarget senderType) {
        if (senderType.equals(CommandTarget.INGAME)) {
            if (commandSender instanceof Identity) {
                Identity identity = (Identity) commandSender;
                if (cmd.length == 1) {
                    specify_online_player.send(identity);
                } else if (cmd.length > 2) {
                    specify_online_player.send(identity);
                } else if (cmd.length == 2) {
                    String s = cmd[1];
                    Player player = Bukkit.getPlayer(s);
                    if (player != null) {
                        Identity target = getOrCreateIdentity(player);
                        if (target.isSuperAdmin()) {
                            is_superadmin.send(identity);
                            MessageBuilder.prefixed().add(C.A, identity.getName()).add(C.E, " tried to change your permissions!").build().send(target);
                        } else {
                            //target is normal user
                            if (target.getPermissions().contains(IngwerPermission.ADMIN)) {
                                target.getPermissions().remove(IngwerPermission.ADMIN);
                                target.getPermissions().add(IngwerPermission.TRUST); //failsafe
                                MessageBuilder.prefixed().add(C.A, target.getName()).add(C.C, " was demoted to: ").add(C.A, "TRUST").build().send(identity);
                                MessageBuilder.prefixed().add(C.A, identity.getName()).add(C.C, " demoted you to: ").add(C.A, "TRUST").build().send(target);

                            } else if (target.getPermissions().contains(IngwerPermission.TRUST)) {
                                target.getPermissions().remove(IngwerPermission.TRUST);
                                MessageBuilder.prefixed().add(C.A, target.getName()).add(C.C, " was demoted to: ").add(C.A, "USER").build().send(identity);
                                MessageBuilder.prefixed().add(C.A, identity.getName()).add(C.C, " demoted you to: ").add(C.A, "USER").build().send(target);
                                Ingwer.getStorage().removeIdentity(target);
                                MessageBuilder.prefixed().add("Your authority to use Ingwer was revoked by ")
                                        .add(C.A, identity.getName())
                                        .add(C.C, ". You are not able to use commands or other Ingwer features for now. If you send a message starting with \""
                                                + Ingwer.getPreferences().prefix() + "\" the message will be send like a normal message. Thank you for using: ")
                                        .add(C.A, "Ingwer v." + Ingwer.VERSION_STRING).add(C.C, "!").build().send(target);

                                return;
                            } else {
                                no_IngwerUser.send(identity);
                            }
                            //update identity
                            Ingwer.getStorage().updateOrSaveIdentity(target, target.getName(), target.getUUID(), target.getPermissions());
                        }

                    } else {
                        specify_online_player.send(identity);
                    }
                }
            }
        }
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.INGAME};
    }

    @Override
    public IngwerPermission getPermission() {
        return IngwerPermission.SUPERADMIN;
    }
}
