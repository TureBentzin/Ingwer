package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.CommandNode;
import de.bentzin.ingwer.command.node.Node;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.command.node.NodeTraceBuilder;
import de.bentzin.ingwer.message.FramedMessage;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.tools.pair.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * when executed this prints a generated usage to the player
 */
public class UsageNodeExecutor implements Node.NodeExecutor {

    /*
       Usage: <prefix>command [arg1,arg2,arg3]
     */

    private final IngwerMessage usage;


    public UsageNodeExecutor(IngwerMessage usage) {
        this.usage = usage;
    }

    @Contract("_ -> new")
    public static @NotNull UsageNodeExecutor generate(@NotNull CommandNode root) {
        List<Map.Entry<Integer, Node<?>>> nodes = getTree(0, root);
        List<OneLinedMessage> messages = new ArrayList<>();
        for (Map.Entry<Integer, Node<?>> entry : nodes) {
            if (entry.getKey() == 0) {
                continue;
            }
            if (entry.getKey() == 1) {
                MessageBuilder builder = MessageBuilder.empty();
                addContent(entry.getValue(), builder);
                messages.add(builder.build());
            }
            else {
                MessageBuilder builder = MessageBuilder.empty();
                int i = entry.getKey();
                while (i > 2) {
                    builder.add(C.C, "  ");
                    i--;
                }
                builder.add(C.C, "|").add(C.C, "-");
                addContent(entry.getValue(), builder);
                messages.add(builder.build());
            }
        }
        FramedMessage message = new FramedMessage(messages);
        return new UsageNodeExecutor(message);
    }

    private static List<Map.Entry<Integer, Node<?>>> getTree(int depth, Node<?> node) {
        System.out.println(node);
        List<Map.Entry<Integer, Node<?>>> nodes = new ArrayList<>();
        nodes.add(new HashMap.SimpleEntry(depth, node));
        for (Node node1 : node.getNodes()) {
            nodes.addAll(getTree(depth + 1, node1));
        }
        return nodes;
    }

    private static void addContent(Node<?> node, MessageBuilder builder) {
        StringJoiner joiner = new StringJoiner(IngwerMessage.COLOR_MM + ", " + IngwerMessage.COLOR_MM_C);
        for (String s : node.values()) {
            joiner.add(s);
        }
        builder.add(C.C, "[").add(C.A, node.getName()).add(C.C, "]").add(C.A, "<dark_gray> » </dark_gray>" + joiner);
    }

    /**
     * Performs this operation on the given arguments.
     *
     * @param data      the commandData
     * @param nodeTrace the nodeTrace to this point
     */
    @Override
    public void accept(@NotNull CommandData data, NodeTrace nodeTrace) {
        usage.send(data.commandSender());
    }

    public IngwerMessage getUsage() {
        return usage;
    }


}
