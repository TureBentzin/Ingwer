package de.bentzin.ingwer.storage.chunkdb;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.ext.NonFinalPermissiond;
import de.bentzin.ingwer.command.node.*;
import de.bentzin.ingwer.command.node.preset.UsageNode;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.utils.CompletableOptional;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ture Bentzin
 * 09.10.2022
 */
class ChunkDBFeature extends SimpleFeature {
    private final ChunkDB chunkDB;

    public ChunkDBFeature(ChunkDB chunkDB) {
        super("chunkDB", "Store all of Ingwers data secure in the chunks! No one will ever find them here, i promise!");
        this.chunkDB = chunkDB;
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.ADMIN;
    }

    @Override
    public void onEnable() {
        new ChunkDBCommand(chunkDB);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return Ingwer.getStorage() instanceof ChunkDB;
    }


    public static class ChunkDBCommand extends IngwerNodeCommand {


        public ChunkDBCommand(ChunkDB chunkDB) {
            super(new CommandTarget[]{CommandTarget.SAVE}, "chunkDB", "manage chunkDB", (data, nodeTrace) -> {
                chunkDB.getStatusMessage().send(data.commandSender());
            });

            getCommandNode().append(new LambdaAgrumentNode("clear", (data, nodeTrace) -> {
                        chunkDB.clean();
                        MessageBuilder.prefixed().add(C.E, "<bold>ChunkDB wurde gelöscht!</bold>").build().send(data.commandSender());
                    })).append(new LambdaAgrumentNode("list", (data, nodeTrace) -> {

                        List<OneLinedMessage> oneLinedMessages = new ArrayList<>();
                        for (NamespacedKey currentIngwerKey : chunkDB.dbManager().getCurrentIngwerKeys()) {
                            oneLinedMessages.add(MessageBuilder.prefixed().add(C.C, currentIngwerKey.getKey()).add(C.A, " : ")
                                    .add(C.C, chunkDB.dbManager().get(currentIngwerKey.getKey())).build());
                        }

                        if(data.commandSender() instanceof Identity identity) {
                            MultipageMessageKeeper multipageMessageKeeper
                                    = new MultipageMessageKeeper(identity.getUUID(), oneLinedMessages, 8,true);
                            multipageMessageKeeper.send();
                        }
                    })).append(new UsageNode("get").append(new WildNode<NamespacedKey>("key",(s) -> true) {

                        @Override
                        public @NotNull NamespacedKey parse(@NotNull String input, @NotNull NodeTrace nodeTrace)
                                throws InvalidParameterException {
                                return new NamespacedKey(ChunkDBManager.NAMESPACE, input);
                        }

                        @Override
                        public void execute(CommandData commandData, NodeTrace nodeTrace, NamespacedKey key) {
                            IngwerCommandSender ingwerCommandSender = commandData.commandSender();
                            String s = "null";
                            try {
                                s = chunkDB.dbManager().get(key);
                            }catch (NullPointerException ignored){}

                            MessageBuilder.prefixed().add(C.C, key.getKey()).add(C.A," -> ").add(C.C, s).build().send(ingwerCommandSender);
                        }
                    }))
                    .finish();

        }
    }
}
