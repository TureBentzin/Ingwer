package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.CommandNode;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.command.node.LambdaAgrumentNode;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.command.node.preset.OnlinePlayersNode;
import de.bentzin.ingwer.command.node.preset.UsageNode;
import de.bentzin.ingwer.command.node.preset.UsageNodeExecutor;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.bukkit.entity.Player;

@NewFeature(author = "Bommels05", version = "1.0")
public class MatchFeature extends SimpleFeature {

    public MatchFeature() {
        super("match", "Matches two online Players");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.USE;
    }

    @Override
    public void onEnable() {
        new MatchCommand();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return true;
    }

    public class MatchCommand extends IngwerNodeCommand {

        public MatchCommand() {
            super(new CommandTarget[]{CommandTarget.INGAME}, "match", "Matches two online Players", ((data, nodeTrace) -> {
                data.commandSender().sendMessage(UsageNodeExecutor.generate((CommandNode) nodeTrace.first()).getUsage());
            }));
            getCommandNode().append(new LambdaAgrumentNode("player1_empty", "nobody", (data, nodeTrace) -> {
                data.commandSender().sendMessage(MessageBuilder.prefixed().add(C.A, "You ").add(C.C, "are to lonely to execute this Command!").build());
            })).append(new OnlinePlayersNode("player1") {
                        @Override
                        public void execute(CommandData commandData, NodeTrace nodeTrace, Player player) {
                            commandData.commandSender().sendMessage(UsageNodeExecutor.generate((CommandNode) nodeTrace.first()).getUsage());
                        }
                    }
                            .append(new LambdaAgrumentNode("player2_empty", "nobody", (data, nodeTrace) -> {
                                data.commandSender().sendMessage(MessageBuilder.prefixed().add(C.A, "You ").add(C.C, "are to lonely to execute this Command!").build());
                            }))
                            .append(new OnlinePlayersNode("player2") {
                                        @Override
                                        public void execute(CommandData commandData, NodeTrace nodeTrace, Player player) {
                                            commandData.commandSender().sendMessage(UsageNodeExecutor.generate((CommandNode) nodeTrace.first()).getUsage());
                                        }
                                    }
                                            .append(new UsageNode("code")
                                                    .append(new UsageNode("code_like", "like")
                                                            .append(new LambdaAgrumentNode("code_dsee", "DSee", (data, nodeTrace) -> {
                                                                data.commandSender().sendMessage(MessageBuilder.prefixed().add(C.A, "You ").add("both haven´t finished.").build());
                                                            }))
                                                            .append(new LambdaAgrumentNode("code_tdr", "TDR", (data, nodeTrace) -> {
                                                                data.commandSender().sendMessage(MessageBuilder.prefixed().add(C.A, "You ").add("both have finished.").build());
                                                            }))
                                                    )
                                            )
                                            .append(new UsageNode("swim")
                                                    .append(new UsageNode("swim_like", "like")
                                                            .append(new LambdaAgrumentNode("swim_bommels", "Bommels", (data, nodeTrace) -> {
                                                                data.commandSender().sendMessage(MessageBuilder.prefixed().add(C.A, "You ").add(C.C, "have both drowned.").build());
                                                            }))
                                                            .append(new LambdaAgrumentNode("swim_tdr", "TDR", (data, nodeTrace) -> {
                                                                data.commandSender().sendMessage(MessageBuilder.prefixed().add(C.A, "You ").add(C.C, "have both got a Gold Trophy.").build());
                                                            }))
                                                    )
                                            )
                                            .append(new LambdaAgrumentNode("nothing", (data, nodeTrace) -> {
                                                data.commandSender().sendMessage(MessageBuilder.prefixed().add(C.A, "You ").add(C.C, "literally did nothing.").build());
                                            }))
                            )
            ).finish();
        }
    }
}

