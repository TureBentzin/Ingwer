package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.identity.permissions.IngwerPermissions;
import de.bentzin.ingwer.message.IM;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.StraightLineStringMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PromoteCommand extends IngwerCommand {

    private StraightLineStringMessage specify_online_player  = new StraightLineStringMessage("Please specify an online Player!");

    private StraightLineStringMessage is_superadmin  = new StraightLineStringMessage("You cant change permissions of this Identity!");

    private StraightLineStringMessage already_admin  = new StraightLineStringMessage("The specified player is already admin");



    public PromoteCommand() {
        super("promote","Promote the user ingame");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, @NotNull CommandTarget senderType) {
        if(senderType.equals(CommandTarget.INGAME)) {
            if(commandSender instanceof Identity) {
                Identity identity = (Identity) commandSender;
                if(cmd.length == 1) {
                    specify_online_player.send(identity);
                }else if(cmd.length > 2) {
                    specify_online_player.send(identity);
                }else if(cmd.length == 2) {
                    String s = cmd[1];
                    Player player = Bukkit.getPlayer(s);
                    if(player != null) {
                        Identity target;
                        if(Ingwer.getStorage().containsIdentityWithUUID(String.valueOf(player.getUniqueId()))) {
                            target = Ingwer.getStorage().getIdentityByUUID(String.valueOf(player.getUniqueId()));
                        }else {
                            target = new Identity(player.getName(), player.getUniqueId(), new IngwerPermissions());
                        }
                            if(target.isSuperAdmin()) {
                                is_superadmin.send(identity);
                                //new MiniMessageMessage(IM.ACCENT_MM + identity.getName() + IM.ACCENT_MM_C +  IM.COLOR_MM +" tried to change your permissions!" + IM.COLOR_MM_C).send(target);
                                MessageBuilder.prefixed().add(C.A, identity.getName()).add(C.C, " tried to change your permissions!").build().send(target);
                            }else {
                                //target is normal user
                                if(target.getPermissions().contains(IngwerPermission.ADMIN)) {
                                    already_admin.send(identity);
                                } else if (target.getPermissions().contains(IngwerPermission.TRUST)) {
                                    target.getPermissions().add(IngwerPermission.ADMIN);
                                    new StraightLineStringMessage(IM.ACCENT_MM + identity.getName() + IM.COLOR_MM +" tried to change your permissions!").send(target);

                                }
                            }

                    }else{
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
}
