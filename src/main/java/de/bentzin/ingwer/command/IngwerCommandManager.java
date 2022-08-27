package de.bentzin.ingwer.command;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.ext.Permissioned;
import de.bentzin.ingwer.features.Feature;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.tools.register.Registerator;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class IngwerCommandManager extends Registerator<IngwerCommand> {

    public static IngwerCommandManager getInstance() {
        return Ingwer.getCommandManager();
    }

    private Logger logger;

    public IngwerCommandManager() {
        this.logger = Ingwer.getLogger().adopt("CMD");
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     *
     * @param newName
     * @return if name is already taken
     */
    public boolean checkName(String newName) {
        for (IngwerCommand command : this) {
            if(command.getName().equalsIgnoreCase(newName)) {return true;}
        }
        return false;
    }

    public void preRunCommand(String input, IngwerCommandSender sender, @NotNull CommandTarget senderType) {
        if(!senderType.isLast()) try {
            throw new IllegalStateException("Unexpected value: " + senderType.name() + ". senderType cant be multi-reference!");
        }catch (IllegalStateException e) { IngwerThrower.acceptS(e,ThrowType.COMMAND);}
        //run
        if(senderType.comesWithPrefix()) {
            if (input.startsWith(Ingwer.getPreferences().prefix() + "")) {
                logger.debug("prefixed message received -> " + input);
                String replaceFirst = input.replaceFirst(Ingwer.getPreferences().prefix() + "", "");
                boolean b = runCommand(replaceFirst, sender, senderType);
                if (!b) {
                    logger.waring("failed to execute command: " + input);
                }
            }
        }else{
            boolean b = runCommand(input, sender, senderType);
            if (!b) {
                logger.waring("failed to execute command: " + input);
            }
        }

    }

    @Contract(pure = true)
    private boolean runCommand(@NotNull String input, IngwerCommandSender sender, CommandTarget senderType) {
        String[] split = input.split(" ");
        if(split.length < 1) {
            if(!senderType.isLast()) try {
                throw new IllegalStateException("Unexpected value: " + input + ". input needs to be splittable!");
            }catch (IllegalStateException e) { IngwerThrower.acceptS(e,ThrowType.COMMAND);}

        }
        String cmd = split[0];
        for (IngwerCommand command : this) {
            if(command.commandTargetCollection().contains(senderType) && command.getName().equalsIgnoreCase(cmd)) {
                boolean b = true;
                Permissioned p = null;
                if(command instanceof Permissioned) {
                    p = (Permissioned) command;
                    b = p.checkPermission(sender);
                }
                if(b) {
                    logger.info(sender.getName() + " executed command: " + input + "@" + command.getName());
                    command.execute(sender,split,senderType);
                    return true;
                }else {
                    logger.info(sender.getName() + " tried to execute command without permissions: " + input);
                    if(p != null) {
                        sender.sendMessage("Lacking permission: " + p.getPermission().name());
                    }
                }

            }
        }
        return false;
    }
}
