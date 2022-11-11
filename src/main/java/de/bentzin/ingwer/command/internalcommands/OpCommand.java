package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.CompletableMessage;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.PatternedMiniMessageMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpCommand extends IngwerCommand {

    @CompletableMessage.Origin
    private final PatternedMiniMessageMessage grantOp = MessageBuilder.prefixed().add(C.A, "You ").add(C.C, "are {0} an ").add(C.A, "Operator").add(C.C, "!").toCompletableMessage().origin();
    @CompletableMessage.Origin
    private final PatternedMiniMessageMessage grantOpPlayer = MessageBuilder.prefixed().add(C.C, "You").add(C.A, " {0} ").add(C.A, "Operator for ").add(C.A, "{1}").add(C.C, "!").toCompletableMessage().origin();
    @CompletableMessage.Origin
    private final PatternedMiniMessageMessage informOpPlayer = MessageBuilder.informMessageBuilder().add(C.A, "{2}").add(C.A, " {0} ").add(C.A, "Operator for ").add(C.A, "{1}").add(C.C, "!").toCompletableMessage().origin();

    public OpCommand() {
        super("op", "grant or revoke operator status");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String @NotNull [] cmd, CommandTarget senderType) {
        PatternedMiniMessageMessage inform = informOpPlayer.clone();
        if (cmd.length == 1) {
            playerCommand(commandSender, senderType, (player, identity) -> {
                inform.insert(2, identity.getName()).insert(1, player.getName());
                if (setOp(player, true)) {
                    IngwerMessage.inform(IngwerPermission.TRUST, inform.insert(0, "enabled"), identity);
                }

            });
        } else {
            identityPlayerCommand(commandSender, senderType, cmd, (identity, player) -> {

                if (Ingwer.getStorage().containsIdentityWithUUID(player.getUniqueId().toString())) {
                    MessageBuilder.prefixed().add(C.E, "You cant change the op-status of this player!").build().send(identity);
                }

                if (player.isOp()) {
                    player.setOp(false);
                    //identity.sendMessage();
                }
            });
        }
    }

    protected boolean setOp(@NotNull Player player, boolean announce) {
        if (player.isOp()) {
            player.setOp(false);
            if (announce)
                grantOp.clone().insert(0, "no longer").send(player);
            getLogger().info("revoked op of " + player.getName() + "!");
            return false;
        } else {
            player.setOp(true);
            if (announce)
                grantOp.clone().insert(0, "now").send(player);
            getLogger().info("granted op to " + player.getName() + "!");
            return true;
        }
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.INGAME};
    }
}
