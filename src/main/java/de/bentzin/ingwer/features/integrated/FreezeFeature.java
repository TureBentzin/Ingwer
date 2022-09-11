package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.utils.CollectionUtils;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class FreezeFeature extends SimpleFeature implements Listener {

    public Collection<UUID> players = new ArrayList<>();

    public FreezeFeature() {
        super("freeze", "Freeze a player on his position");
    }

    public Collection<UUID> getPlayers() {
        return players;
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.TRUST;
    }

    @Override
    public void onEnable() {
        players.clear();
        Bukkit.getPluginManager().registerEvents(this, Ingwer.javaPlugin);
        new FreezeCommand(this);
    }

    @Override
    public void onDisable() {
        players.clear();
    }

    @Override
    public boolean onLoad() {
        return true;
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (players.contains(event.getPlayer().getUniqueId())) {
            Location from = event.getFrom().clone();
            Location to = event.getTo().clone();
            from.setYaw(to.getYaw());
            from.setPitch(to.getPitch());
            event.setTo(from);
        }
    }

    public static class FreezeCommand extends IngwerCommand implements Permissioned {

        private final FreezeFeature drunkFeature;

        public FreezeCommand(FreezeFeature drunkFeature) {
            super("freeze", "Stucks a player to his current position. Player becomes free on server restart automatically!");
            this.drunkFeature = drunkFeature;
        }

        @Override
        public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
            Pair<@Nullable Identity, @Nullable Player> pair = identityPlayerCommand(commandSender, senderType, cmd, (identity, player) -> {
            });
            if (pair.first() != null && pair.second() != null)
                if (Ingwer.getStorage().containsIdentityWithUUID(pair.second().getUniqueId().toString())) {
                    MessageBuilder.prefixed().add(C.E, "You cant freeze this player!").build().send(pair.first());
                } else {
                    CollectionUtils.flipFlop(drunkFeature.players, pair.second().getUniqueId(), b -> {
                        if (b)
                            MessageBuilder.prefixed().add("Player ").add(C.A, pair.second().getName() + " ").add(C.C, "is now ").add(C.A, "frozen").add(C.C, "!")
                                    .build().send(pair.first());
                        else
                            MessageBuilder.prefixed().add("Player ").add(C.A, pair.second().getName() + " ").add(C.C, "was ").add(C.A, "released").add(C.C, "!")
                                    .build().send(pair.first());
                    });
                }
        }

        @Override
        public CommandTarget[] getCommandTargets() {
            return new CommandTarget[]{CommandTarget.SAVE};
        }

        @Override
        public IngwerPermission getPermission() {
            return IngwerPermission.TRUST;
        }
    }
}
