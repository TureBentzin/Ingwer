package de.bentzin.ingwer.features.integrated.vault;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.*;
import de.bentzin.ingwer.command.node.preset.CollectionNode;
import de.bentzin.ingwer.command.node.preset.OnlinePlayersNode;
import de.bentzin.ingwer.command.node.preset.UsageNode;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.message.*;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        @CompletableMessage.Origin
        PatternedMiniMessageMessage message = MessageBuilder.prefixed().add(C,"Successfully ").add(A,"{0}")
                .add(C," permission \"").add(A,"{1}").add(C,"\" from ").add(A,"{2}").add(C,"!").toCompletableMessage().origin();

        UsageNode addNode = new UsageNode("add");
        UsageNode removeNode = new UsageNode("remove");
        AnyStringNode permissionNode = new AnyStringNode("permission",null){
            @Override
            public void execute(CommandData commandData, @NotNull NodeTrace nodeTrace, String permission) throws NodeTrace.NodeParser.NodeParserException {

                Case c = null;
                String target = null;
                Optional<Node<Player>> user = nodeTrace.getOptional("user");
                if(user.isPresent()){
                    c = Case.USER;
                    Player p = nodeTrace.parser(commandData).parse("users");
                    target = p.getName();
                } else {
                    Optional<Node<String>> group = nodeTrace.getOptional("group");
                    if (group.isPresent()) {
                        c = Case.GROUP;
                        target = nodeTrace.parser(commandData).parse("groups");
                    }
                }

                if(nodeTrace.contains(addNode)) {
                    //case: add
                    assert c != null;
                    c.add.accept(vaultFeature,target,permission);
                    message.clone().insert(0,"added").insert(1,permission).insert(2,target).send(commandData.commandSender());

                } else if (nodeTrace.contains(removeNode)) {
                    //case: remove
                    assert c != null;
                    c.remove.accept(vaultFeature,target,permission);
                    message.clone().insert(0,"removed").insert(1,permission).insert(2,target).send(commandData.commandSender());
                }else {
                    //ok wtf went wrong here
                    throw new IllegalStateException("node is bricked!");
                }
            }
        };

        addNode.append(permissionNode);
        removeNode.append(permissionNode);

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
                                .append(addNode).append(removeNode)
                        ))
                .append(new UsageNode("group").append(new CollectionNode<>(
                                "groups", () -> List.of(vaultFeature.getPerms().getGroups()), group -> group) {
                            @Override
                            public void execute(CommandData commandData, NodeTrace nodeTrace, String group) {
                                generateOverview(group, vaultFeature).send(commandData.commandSender());
                            }
                        }.append(addNode).append(removeNode))
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
    private static @NotNull List<OneLinedMessage> generateDetail(@NotNull Player player, @NotNull VaultFeature vaultFeature) {
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


    protected void removePermission(String target, String permission) {

    }

    enum Case {
        GROUP((vault, group, permission) -> {
            //remove
            for (World world : Bukkit.getWorlds()) {
                vault.getPerms().groupRemove(world,group,permission);
            }

        }, (vault, group, permission) ->  {
            //add
            for (World world : Bukkit.getWorlds()) {
                vault.getPerms().groupAdd(world,group,permission);
            }
        }),
        USER((vault, user, permission) -> {
            //remove
            Player player = Bukkit.getPlayer(user);
            vault.getPerms().playerRemove(player,permission);
        }, (vault, user, permission) ->  {
            //add
            Player player = Bukkit.getPlayer(user);
            vault.getPerms().playerAdd(player,permission);
        });
        private final TriConsumer<VaultFeature, String, String> remove;
        private final TriConsumer<VaultFeature, String, String> add;

        Case(TriConsumer<VaultFeature, String, String> remove, TriConsumer<VaultFeature, String, String> add){
            this.remove = remove;
            this.add = add;
        }
    }

}
