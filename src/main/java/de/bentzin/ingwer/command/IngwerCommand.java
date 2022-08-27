package de.bentzin.ingwer.command;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.tools.register.Registerator;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class IngwerCommand {

    private final Logger logger;

    protected boolean valid;

    @NotNull
    private final String name;

    @Nullable
    private final String description;

    public IngwerCommand(@NotNull String name, @Nullable String description) {
       // this.logger = IngwerCommandManager.getInstance().getLogger().adopt(name);
        this.name = name;
        this.description = description;
        if(IngwerCommandManager.getInstance().checkName(name)) {
            logger = Ingwer.getCommandManager().getLogger().adopt(name);
        }else {
            Ingwer.getCommandManager().getLogger().error("ambiguous naming of: " + name + "!");

            //final logger
            logger = Ingwer.getCommandManager().getLogger().adopt("err");

            valid = false;
            return;
        }
        logger.info("finished creating of: " + name);
        valid = true;

        try {
            Ingwer.getCommandManager().register(this);
        } catch (Registerator.DuplicateEntryException e) {
            Ingwer.getIngwerThrower().accept(e, ThrowType.COMMAND);
        }
    }

    public @NotNull String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        if(description == null) return "";
        else return description;
    }

    public @NotNull Logger getLogger() {
        return logger;
    }

    public Collection<CommandTarget> commandTargetCollection() { return List.of(getCommandTargets());}


    public void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType){};

    abstract CommandTarget[] getCommandTargets();


}
