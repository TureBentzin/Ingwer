package de.bentzin.ingwer.command;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.message.CompletableMessage;
import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.PatternedMiniMessageMessage;
import de.bentzin.ingwer.message.builder.C;
import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import de.bentzin.tools.register.Registerator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class IngwerCommandManager extends Registerator<IngwerCommand> {

    private final Logger logger;

    public IngwerCommandManager() {
        this.logger = Ingwer.getLogger().adopt("CMD");
    }

    public static IngwerCommandManager getInstance() {
        return Ingwer.getCommandManager();
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * @param newName the name to check
     * @return if name is already taken
     */
    public boolean checkName(String newName) {
        for (IngwerCommand command : this) {
            if (command.getName().equalsIgnoreCase(newName)) {
                return true;
            }
        }
        return false;
    }

    @ApiStatus.Internal
    @Nullable IngwerCommand getByName(String name) {
        IngwerCommand ingwerCommand = null;
        for (IngwerCommand command : this) {
            if (command.getName().equalsIgnoreCase(name)) {
                ingwerCommand = command;
                break;
            }
        }
        return ingwerCommand;
    }

    private static PatternedMiniMessageMessage commandNotFound() {
        return MessageBuilder.prefixed()
                .add(C.E, "Failed to execute ").add(C.A, "<click:suggest_command:'" + "{0}" + "'>{0}!</click>")
                .add(C.E, " Type ").add(C.A, "<click:suggest_command:'" +
                        Ingwer.getPreferences().prefix() + "help" + "'>" + Ingwer.getPreferences().prefix() + "help" + "</click>")
                .add(C.E, " to get a list of available commands!").toCompletableMessage().clone();
    }

    public void preRunCommand(String input, IngwerCommandSender sender, @NotNull CommandTarget senderType) {
        if (!senderType.isLast()) try {
            throw new IllegalStateException("Unexpected value: " + senderType.name() + ". senderType cant be multi-reference!");
        } catch (IllegalStateException e) {
            IngwerThrower.acceptS(e, ThrowType.COMMAND);
        }
        //run
        if (senderType.comesWithPrefix()) {
            if (input.startsWith(Ingwer.getPreferences().prefix() + "")) {
                String replaceFirst = saveRemoveFirst(input, Ingwer.getPreferences().prefix());
                boolean b = runCommand(replaceFirst, sender, senderType);
                if (!b) {
                    commandNotFound().insert(0,input).send(sender);
                    logger.warning("failed to execute command: " + input);
                }
            }
        } else {
            boolean b = runCommand(input, sender, senderType);
            if (!b) {
                commandNotFound().insert(0,input).send(sender);
                logger.warning("failed to execute command: " + input);
            }
        }

    }


    @NotNull
    private String saveRemoveFirst(@NotNull String s, char query) {
        StringBuilder builder = new StringBuilder();
        char[] chars = s.toCharArray();
        boolean first = true;
        for (char aChar : chars) {
            if (aChar == query && first) {
                first = false;
            } else {
                builder.append(aChar);
            }
        }
        return builder.toString();
    }

    @Contract(pure = true)
    private boolean runCommand(@NotNull String input, IngwerCommandSender sender, CommandTarget senderType) {
        String[] split = input.split(" ");
        if (split.length < 1) {
            if (!senderType.isLast()) try {
                throw new IllegalStateException("Unexpected value: " + input + ". input needs to be splittable!");
            } catch (IllegalStateException e) {
                IngwerThrower.acceptS(e, ThrowType.COMMAND);
            }

        }
        String cmd = split[0];
        for (IngwerCommand command : this) {
            if (command.commandTargetCollection().contains(senderType) && command.getName().equalsIgnoreCase(cmd)) {
                boolean b = true;
                Permissioned p = null;
                if (command instanceof Permissioned) { //check for permission
                    p = (Permissioned) command;
                    b = p.checkPermission(sender);
                }
                if (b) {
                    command.execute(sender, split, senderType);
                    logger.info(sender.getName() + " executed command: " + input + "@" + command.getName());
                    if (!sender.getPermissions().contains(IngwerPermission.ADMIN))
                        IngwerMessage.inform(IngwerPermission.ADMIN, MessageBuilder.informMessageBuilder().add(C.A, sender.getName())
                                .add(C.C, " executed command: \"").add(C.A, input).add(C.C, "\"!")
                                .build());
                    return true;
                } else {
                    logger.info(sender.getName() + " tried to execute command without permissions: " + input);
                    if (p != null) {
                        Permissioned.lacking(sender, p.getPermission());
                    }
                }

            }
        }
        return false;
    }
}
