package de.bentzin.ingwer.command.internalcommands;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.MultipageMessageKeeper;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.utils.cmdreturn.CommandReturn;
import de.bentzin.tools.DevTools;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ThreadsCommand extends IngwerCommand implements Permissioned {
    public ThreadsCommand() {
        super("threads", "list all threads");
    }


    @Contract("_ -> new")
    private @NotNull MultipageMessageKeeper threadMessage(@NotNull Identity identity) {
        return new MultipageMessageKeeper(Objects.requireNonNull(identity.getUUID()),
                generate(Objects.requireNonNull(identity)), 8);
    }

    private @NotNull List<OneLinedMessage> generate(@NotNull Identity identity) {
        List<OneLinedMessage> oneLinedMessages = new ArrayList<>();

        for (Thread thread : DevTools.getSilkWormController().getThreads()) {
            oneLinedMessages.add(generateMessage(thread, identity.getUUID()));
        }


        return oneLinedMessages;
    }

    //cmdName   : cmdDesc
    // <click:suggest_command:'+say'><hover:show_text:'<gray>Full Description'><gray>+say   : Say things stating with +</click></hover>
    @NotNull
    private OneLinedMessage generateMessage(@NotNull Thread thread, UUID uuid) {
        char prefix = Ingwer.getPreferences().prefix();

        CommandReturn kill = Ingwer.getCommandReturnSystem().addNewReturn(thread::interrupt, uuid);

        return new MiniMessageMessage("<gray>[" + (thread.isAlive() ? "alive" : "dead") + "] " + "<gold>" + thread.getName() + "<hover:show_text:'<gray>" + "prio: " + thread.getPriority() + " group: " + thread.getThreadGroup().getName() + " id: " + thread.getId() + "'>" + "<dark_gray> » <gray>" + thread.getState().name()
                + "<dark_red><click:run_command:'" + kill.command() + "'> [KILL]</click>");
    }

    @Override
    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType) {
        identityCommand(commandSender, senderType, identity -> threadMessage(identity).send(1));
    }

    @Override
    public CommandTarget[] getCommandTargets() {
        return new CommandTarget[]{CommandTarget.INGAME};
    }

    @Override
    public @NotNull IngwerPermission getPermission() {
        return IngwerPermission.SUPERADMIN;
    }
}
