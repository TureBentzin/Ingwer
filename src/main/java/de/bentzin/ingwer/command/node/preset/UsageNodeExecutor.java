package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.ext.Described;
import de.bentzin.ingwer.command.node.CommandNode;
import de.bentzin.ingwer.command.node.Node;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.message.*;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.utils.cmdreturn.CommandReturn;
import de.bentzin.ingwer.utils.cmdreturn.CommandReturnSystem;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * when executed this prints a generated usage to the player
 */
public class UsageNodeExecutor implements Node.NodeExecutor {

    /*
       Usage: <prefix>command [arg1,arg2,arg3]
     */

    private final Node node;
    private final IngwerMessage message;

    public UsageNodeExecutor(Node node) {
        this.node = node;
        message = generateMessage(null);
    }

    @Contract("_ -> new")
    public static @NotNull UsageNodeExecutor generate(@NotNull CommandNode node) {
        return new UsageNodeExecutor(node);
    }

    @Contract("_ -> new")
    public static @NotNull UsageNodeExecutor generate(@NotNull Node node) {
        return new UsageNodeExecutor(node);
    }

    private static String generateNodeList(List<Node> nodes, @Nullable UUID uuid) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (Node n : nodes) {
            if (!isFirst) {
                builder.append(" ");
            } else {
                isFirst = false;
            }

            builder.append(wrap(n, generateNode(n), uuid));
        }
        return builder.toString();
    }

    private static String generateNode(Node node) {
        Collection<String> values = node.values();
        if (values.size() == 1) {
            return values.iterator().next();
        }
        StringJoiner joiner = new StringJoiner("|", "<", ">");
        for (String value : values) {
            joiner.add(value);
        }
        return joiner.toString();
    }

    private static String wrap(Node node, String s, @Nullable UUID uuid) {
        return wrapClick(node, wrapHover(node, s), uuid);
    }

    private static String wrapClick(Node node, String s, @Nullable UUID uuid) {
        if (uuid == null) {
            return s;
        }
        CommandReturn commandReturn = Ingwer.getCommandReturnSystem().addNewReturn(new Runnable() {
            @Override
            public void run() {
                UsageNodeExecutor.generate(node).generateMessage(uuid).send(Bukkit.getPlayer(uuid));
            }
        }, uuid);
        return "<click:run_command:"+commandReturn.command()+">"+s+"</click>";
    }

    private static String wrapHover(Node node, String s) {
        if (node instanceof Described described) {
            return "<hover:"+described.getDescription().getOneLinedString()+">"+s+"</hover>";
        } else {
            return s;
        }

    }

    record EntryTrace(List<Node> parents, List<Node> full) {

    }

    private static void forest(Node node, @NotNull BiConsumer<EntryTrace, Node> function) {
        forest(new EntryTrace(Collections.EMPTY_LIST, List.of(node)), node, function);
    }

    private static void forest(EntryTrace trace, Node node, @NotNull BiConsumer<EntryTrace, Node> function) {
        function.accept(trace, node);
        if (node.hasNodes()) {
            @Nullable Collection<Node> nodes = node.getNodes();
            Objects.requireNonNull(nodes);
            List<Node> parents = trace.full;
            for (Node n : nodes) {
                forest(new EntryTrace(parents, addToList(parents, n)), n, function);
            }
        }
    }

    private static <T> List<T> addToList(List<T> list, T value) {
        List<T> l = new ArrayList<>(list);
        l.add(value);
        return Collections.unmodifiableList(l);
    }

    public IngwerMessage generateMessage(@Nullable UUID uuid) {
        List<OneLinedMessage> messages = new ArrayList<>();

        forest(node, (entryTrace, n) -> {
            String s = generateNodeList(entryTrace.full, uuid);
            MiniMessageMessage msg = new MiniMessageMessage(IngwerMessage.COLOR_MM+" "+s);
            messages.add(msg);
        });

        FramedMessage framed = new FramedMessage(messages);
        return framed;
    }


    /**
     * Performs this operation on the given arguments.
     *
     * @param data      the commandData
     * @param nodeTrace the nodeTrace to this point
     */
    @Override
    public void accept(@NotNull CommandData data, NodeTrace nodeTrace) {
        generateMessage(Identity.getUUIDOrNull(data.commandSender())).send(data.commandSender());
    }

    public IngwerMessage getUsage() {
        return message;
    }


}
