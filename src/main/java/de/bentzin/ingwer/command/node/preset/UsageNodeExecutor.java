package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.CommandNode;
import de.bentzin.ingwer.command.node.Node;
import de.bentzin.ingwer.command.node.NodeTrace;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        /* AtomicReference<StringBuilder> atomicReference = new AtomicReference<>(new StringBuilder());
        root.forest(node -> {
            StringBuilder stringBuilder = atomicReference.get();
            stringBuilder.append("[");
            AtomicBoolean first = new AtomicBoolean(true);
            Objects.requireNonNull(node.getNodes()).
                    forEach(first.get() ? o -> {stringBuilder.append(o);
                        first.set(false);
                    } : o -> stringBuilder.append(",").append(o));
            stringBuilder.append("] ");
        });
         */
        //TODO Usage
        return new UsageNodeExecutor(MessageBuilder.prefixed().add(C.E, "Please care about the usage!").build());
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
