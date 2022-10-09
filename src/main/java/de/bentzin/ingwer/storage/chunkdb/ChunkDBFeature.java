package de.bentzin.ingwer.storage.chunkdb;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.command.node.LambdaAgrumentNode;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;

/**
 * @author Ture Bentzin
 * 09.10.2022
 */
class ChunkDBFeature extends SimpleFeature {
    private final ChunkDB chunkDB;

    public ChunkDBFeature(ChunkDB chunkDB) {
        super("chunkDB","Store all of Ingwers data secure in the chunks! No one will ever find them here, i promise!");
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

    public static class ChunkDBCommand extends IngwerNodeCommand{
        public ChunkDBCommand(ChunkDB chunkDB) {
            super(new CommandTarget[]{CommandTarget.SAVE}, "chunkDB", "manage chunkDB", (data, nodeTrace) -> {
                chunkDB.getStatusMessage().send(data.commandSender());
            });

            getCommandNode().append(new LambdaAgrumentNode("clear",(data, nodeTrace) -> {
                chunkDB.clean();
                MessageBuilder.prefixed().add(C.C,"ChunkDB wurde gelöscht!").build().send(data.commandSender());
            })).finish();

        }
    }
}
