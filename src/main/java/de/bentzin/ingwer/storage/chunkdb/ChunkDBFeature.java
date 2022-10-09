package de.bentzin.ingwer.storage.chunkdb;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.command.node.LambdaAgrumentNode;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.bukkit.NamespacedKey;

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
                    }))
                    .finish();

        }
    }
}
