package de.bentzin.ingwer.features.integrated.vault;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.command.node.LambdaAgrumentNode;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.command.node.preset.CollectionNode;
import de.bentzin.ingwer.command.node.preset.OnlinePlayersNode;
import de.bentzin.ingwer.command.node.preset.UsageNode;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.message.FramedMessage;
import de.bentzin.ingwer.message.MultilinedMessage;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.bentzin.ingwer.message.builder.C.*;

/*
 * perm - view own                           X
 * perm detail - view own detail             X
 * perm user [user] - view user              X
 * perm user [user] detail - view user detail   X
 * perm group [group] - view group
 * perm user/group [user/group] add [permission] - add [permission] to [user/group]
 * perm user/group [user/group] remove [permission] - remove [permission] to [user/group]
 *
 * perm user [user] join [group]
 * perm user [user] leave [group]
 */

public class PermissionCommand extends IngwerNodeCommand {

    private final VaultFeature vaultFeature;

    public PermissionCommand(VaultFeature vaultFeature) throws NodeTrace.NodeParser.NodeParserException {
        super(new CommandTarget[]{CommandTarget.SAVE}, "perm", "<blue>[VAULT]</blue> view or change permissions or groups",
                (data, nodeTrace) -> {
                    IngwerCommandSender ingwerCommandSender = data.commandSender();
                    if (ingwerCommandSender instanceof Identity id) {
                        Player player = Bukkit.getPlayer(id.getUUID());
                        if (player != null) {
                            generateOverview(player, vaultFeature).send(id);
                            return;
                        }
                    }
                    yourNoPlayer().send(ingwerCommandSender);
                });
        this.vaultFeature = vaultFeature;
        //command

        UsageNode addNode = new UsageNode("add");
        UsageNode removeNode = new UsageNode("add");



        getCommandNode().append(new LambdaAgrumentNode("detail", (data, nodeTrace) -> {
                    IngwerCommandSender ingwerCommandSender = data.commandSender();
                    if (ingwerCommandSender instanceof Identity id) {
                        Player player = Bukkit.getPlayer(id.getUUID());
                        if (player != null) {
                            List<OneLinedMessage> oneLinedMessages = generateDetail(player, vaultFeature);
                            new MultipageMessageKeeper(player.getUniqueId(), oneLinedMessages, 15, true).send();
                            return;
                        }
                    }
                    yourNoPlayer().send(ingwerCommandSender);
                }))
                .append(new UsageNode("user")
                        .append(new OnlinePlayersNode("users") {
                                    @Override
                                    public void execute(CommandData commandData, NodeTrace nodeTrace, Player player) {
                                        if (player != null) {
                                            generateOverview(player, vaultFeature).send(commandData.commandSender());
                                        }
                                    }
                                }.append(new LambdaAgrumentNode("detail", (data, nodeTrace) -> {
                                    Player player = null;
                                    try {
                                        player = nodeTrace.parser(data).parse("users");
                                    } catch (NodeTrace.NodeParser.NodeParserException e) {
                                        throw new RuntimeException(e);
                                    }
                                    List<OneLinedMessage> oneLinedMessages = generateDetail(player, vaultFeature);
                                    new MultipageMessageKeeper(player.getUniqueId(), oneLinedMessages, 15, true).send();
                                }))
                        ))
                .append(new UsageNode("group").append(new CollectionNode<>(
                                "groups", () -> List.of(vaultFeature.getPerms().getGroups()), group -> group) {
                            @Override
                            public void execute(CommandData commandData, NodeTrace nodeTrace, String group) {
                                generateOverview(group, vaultFeature).send(commandData.commandSender());
                            }
                        })
                )
                .finish();
    }

    private static OneLinedMessage yourNoPlayer() {
        return MessageBuilder.prefixed().add(E, "To execute this you need to be an player currently connected to the server!").build();
    }

    @Contract("_, _ -> new")
    private static @NotNull MultilinedMessage generateOverview(@NotNull Player player, @NotNull VaultFeature vaultFeature) {
        List<OneLinedMessage> oneLinedMessageList = new ArrayList<>();
        oneLinedMessageList.add(MessageBuilder.empty().add(C, "Overview of: ").add(A, player.getName()).build());
        oneLinedMessageList.add(MessageBuilder.empty().add(A, "Groups: ").add(C, Arrays.toString(vaultFeature.getPerms().getPlayerGroups(player))).build());
        oneLinedMessageList.add(MessageBuilder.empty().add(A, "Prefix: ").add(C, vaultFeature.getChat().getPlayerPrefix(player)).build());
        oneLinedMessageList.add(MessageBuilder.empty().add(A, "Suffix: ").add(C, vaultFeature.getChat().getPlayerSuffix(player)).build());

        return new FramedMessage(oneLinedMessageList);
    }

    @Contract("_, _ -> new")
    private static @NotNull MultilinedMessage generateOverview(@NotNull String group, @NotNull VaultFeature vaultFeature) {
        List<OneLinedMessage> oneLinedMessageList = new ArrayList<>();
        World world = Bukkit.getWorlds().get(0);
        oneLinedMessageList.add(MessageBuilder.empty().add(C, "Overview of: ").add(A, group).build());
        oneLinedMessageList.add(MessageBuilder.empty().add(C, "Data for world: ").add(A, world.getName()).build());
        oneLinedMessageList.add(MessageBuilder.empty().add(A, "Prefix: ").add(C, vaultFeature.getChat().getGroupPrefix(world, group)).build());
        oneLinedMessageList.add(MessageBuilder.empty().add(A, "Suffix: ").add(C, vaultFeature.getChat().getGroupSuffix(world, group)).build());
        oneLinedMessageList.add(MessageBuilder.empty().add(C, "For advanced managing please try using an ingwer feature designed for you permission system!").build());

        return new FramedMessage(oneLinedMessageList);
    }

    @Contract("_, _ -> new")
    private static List<OneLinedMessage> generateDetail(@NotNull Player player, @NotNull VaultFeature vaultFeature) {
        List<OneLinedMessage> oneLinedMessageList = new ArrayList<>();
        oneLinedMessageList.add(MessageBuilder.empty().add(C, "Permissions of: ").add(A, player.getName()).build());
        // int i = 1;
        for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            oneLinedMessageList.add(MessageBuilder.empty().add(C,/*[" + i + "] : */"\"")
                    .add(A, effectivePermission.getPermission())
                    .add(C, "\" : [" + effectivePermission.getValue() + "]").build());
            // i++;
        }
        return oneLinedMessageList;
    }
}
