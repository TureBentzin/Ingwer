package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NewFeature(author = "Ture Bentzin",version = "1.0")
public class FreezeFeature extends SimpleFeature {

    public Collection<UUID> players = new ArrayList<>();

    public Collection<UUID> getPlayers() {
        return players;
    }

    public FreezeFeature() {
        super("freeze", "Freeze a player on his position");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.TRUST;
    }

    @Override
    public void onEnable() {
            players.clear();
            new FreezeCommand(this);
    }

    @Override
    public void onDisable() {
        players.clear();
    }

    @Override
    public boolean onLoad() {
        return false;
    }

    public static class FreezeCommand extends IngwerCommand {

        private final FreezeFeature freezeFeature;

        public FreezeCommand(FreezeFeature freezeFeature) {
            super("freeze","Stucks a player to his current position. Player becomes free on server restart automatically!");
            this.freezeFeature = freezeFeature;
        }

        @Override
        public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
            identityPlayerCommand(commandSender,senderType,cmd,
                    (identity, player) -> CollectionUtils.flipFlop(freezeFeature.players,player.getUniqueId()));
        }

        @Override
        public CommandTarget[] getCommandTargets() {
            return new CommandTarget[]{CommandTarget.SAVE};
        }
    }
}
