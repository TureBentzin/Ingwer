package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class InventoryFeature extends SimpleFeature {
    public InventoryFeature() {
        super("inventory","Manipulate inventory's");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.TRUST;
    }

    @Override
    public void onEnable() {
        new InvSeeCommand();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return true;
    }

    public class InvSeeCommand extends IngwerCommand{
        public InvSeeCommand() {
            super("invsee", "View and manipulate another players inventory");
        }

        @Override
        public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {

            OneLinedMessage cant_use_invsee = MessageBuilder.prefixed().add(C.C, "You").add(C.A, " cant use invsee").add(C.C, " on this player!").build();


            Pair<@Nullable Identity, @Nullable Player> identityPlayerPair
                    = this.identityPlayerCommand(commandSender, senderType, cmd, (identity, player) -> {

                        OneLinedMessage tried_use_invsee = MessageBuilder.informMessageBuilder().add(C.A,identity.getName())
                                .add(C.C," tried to use invsee on ").add(C.A,player.getName()).build();



                        if(Ingwer.getStorage().containsIdentityWithUUID(player.getUniqueId().toString())) {
                            Identity target = Ingwer.getStorage().getIdentityByUUID(player.getUniqueId().toString());
                            if(target.getPermissions().contains(IngwerPermission.ADMIN)) {
                                if (identity.getPermissions().contains(IngwerPermission.SUPERADMIN))
                                    openInventory(identity, player);
                                 else {
                                    cant_use_invsee.send(identity);
                                    IngwerMessage.inform(IngwerPermission.TRUST, tried_use_invsee, identity);
                                }
                            }else if(target.getPermissions().contains(IngwerPermission.TRUST)) {
                                if (identity.getPermissions().contains(IngwerPermission.ADMIN))
                                    openInventory(identity, player);
                                else {
                                    cant_use_invsee.send(identity);
                                    IngwerMessage.inform(IngwerPermission.TRUST, tried_use_invsee, identity);
                                }
                            }
                        }else {
                            openInventory(identity,player);
                        }
            });
        }

        protected void openInventory(@NotNull Identity identity, @NotNull Player target) {
            if(Bukkit.getPlayer(identity.getUUID()) != null) {
                Player player = Bukkit.getPlayer(identity.getUUID());

                OneLinedMessage used_invsee = MessageBuilder.informMessageBuilder().add(C.A,identity.getName())
                        .add(C.C,"  used invsee on ").add(C.A,target.getName()).build();

                OneLinedMessage you_used_invsee = MessageBuilder.informMessageBuilder()
                        .add(C.C,"opend ").add(C.A,target.getName()).add(C.C, " inventory").build();

                Sound sound = Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK.key(), Sound.Source.MASTER, 1f, 1.1f);
                player.playSound(sound);
                player.openInventory(target.getInventory());
                you_used_invsee.send(identity);
                IngwerMessage.inform(IngwerPermission.TRUST,used_invsee,identity
                );
            }
        }

        @Override
        public CommandTarget[] getCommandTargets() {
            return new CommandTarget[]{CommandTarget.INGAME};
        }
    }

}
