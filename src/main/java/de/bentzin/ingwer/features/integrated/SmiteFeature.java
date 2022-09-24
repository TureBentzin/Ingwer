package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.ext.Described;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.command.node.Node;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@NewFeature(author = "LOL", version = "0.0.1-LOL")
public class SmiteFeature extends SimpleFeature {
    public SmiteFeature() {
        super("smite", "Strike the world with lightning");
    }

    @Override
    public boolean onLoad() {
        return true;
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.USE;
    }

    @Override
    public void onEnable() {
        new SmiteCommand();
    }

    @Override
    public void onDisable() {

    }

    private static final IngwerMessage LOOK_ON_GROUND = MessageBuilder.prefixed().add(C.E, "You need to look at the ground to strike lightning!").build();
    private static final IngwerMessage WORLD_STRUCK = MessageBuilder.prefixed().add(C.C, "The world was struck by lightning!").build();

    public static class SmiteCommand extends IngwerNodeCommand {
        private static Player getPlayer(IngwerCommandSender sender) {
            return Bukkit.getPlayer(Identity.getUUIDOrNull(sender));
        }
        public SmiteCommand() {
            super(new CommandTarget[]{CommandTarget.INGAME}, "smite", "Strikes the world", new Node.NodeExecutor() {
                @Override
                public void accept(CommandData data, NodeTrace nodeTrace) {
                    Block targetBlock = getPlayer(data.commandSender()).getTargetBlock(50);
                    if (targetBlock == null) {
                        LOOK_ON_GROUND.send(data.commandSender());
                        return;
                    }
                    Bukkit.getScheduler().runTask(Ingwer.javaPlugin, new Runnable() {
                        @Override
                        public void run() {
                            targetBlock.getWorld().strikeLightning(targetBlock.getLocation());
                        }
                    });
                    WORLD_STRUCK.send(data.commandSender());
                }
            });
            getCommandNode().finish();
        }
    }
}
