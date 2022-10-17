package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.ArgumentNode;
import de.bentzin.ingwer.command.node.IngwerNodeCommand;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.command.node.NodeTrace.NodeParser.NodeParserException;
import de.bentzin.ingwer.message.StraightLineStringMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;

public class NodeTestCommand extends IngwerNodeCommand {

    public NodeTestCommand() {
        super(new CommandTarget[]{CommandTarget.INGAME},
                "node-test", "test if the nodesystem works!", ((data, nodeTrace) -> {
                    data.commandSender().sendMessage(MessageBuilder.prefixed().add("Test!").build());
                }));
        getCommandNode(
        ).append(new ArgumentNode("test1") {
                     @Override
                     public void execute(CommandData commandData, NodeTrace nodeTrace, String s) {
                         new StraightLineStringMessage("test 1: " + nodeTrace).send(commandData.commandSender());
                     }
                 }
        ).append(new ArgumentNode("text") {
            @Override
            public void execute(CommandData commandData, NodeTrace nodeTrace, String s) {
                new StraightLineStringMessage("Michael!").send(commandData.commandSender());
            }
        }.append(new ArgumentNode("inner") {
                     @Override
                     public void execute(CommandData commandData, NodeTrace nodeTrace, String s) {
                         new StraightLineStringMessage("LOL").send(commandData.commandSender());
                     }
                 }
        ).append(new ArgumentNode("rec") {
                     @Override
                     public void execute(CommandData commandData, NodeTrace nodeTrace, String s) throws NodeParserException {
                         //new:
                         String string = nodeTrace.parser(commandData).parse("text");
                         MessageBuilder.prefixed().add("Last node was: ").add(C.A, string).build().send(commandData.commandSender());
                     }
                 }
        )).finish();
    }
}
