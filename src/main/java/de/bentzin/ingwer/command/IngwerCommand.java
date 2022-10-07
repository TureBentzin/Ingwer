package de.bentzin.ingwer.command;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.message.StraightLineStringMessage;
import de.bentzin.ingwer.thrower.ThrowType;
import de.bentzin.tools.register.Registerator;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class IngwerCommand {

    protected final boolean valid;
    private final Logger logger;
    @NotNull
    private final String name;
    @Nullable
    private final String description;

    public IngwerCommand(@NotNull String name, @Nullable String description) {
        // this.logger = IngwerCommandManager.getInstance().getLogger().adopt(name);
        this.name = name;
        this.description = description;

        if (!IngwerCommandManager.getInstance().checkName(name)) {
            logger = Ingwer.getCommandManager().getLogger().adopt(name);
        } else {
            IngwerCommand already = Ingwer.getCommandManager().getByName(name);
            Ingwer.getCommandManager().getLogger().error("ambiguous naming of: " + name + "!" +
                    (already != null ? " Name is already in use by: " + already.getClass().getName() : ""));

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

    public final @NotNull String getName() {
        return name;
    }

    @NotNull
    public final String getDescription() {
        if (description == null) return "";
        else return description;
    }

    public final @NotNull Logger getLogger() {
        return logger;
    }

    public Collection<CommandTarget> commandTargetCollection() {
        List<CommandTarget> commandTargets = new ArrayList<>();
        for (CommandTarget commandTarget : getCommandTargets()) {
            commandTargets.addAll(List.of(commandTarget.fullfill()));
        }
        return commandTargets;
    }


    public abstract void execute(IngwerCommandSender commandSender, String[] cmd, CommandTarget senderType);

    public abstract CommandTarget[] getCommandTargets();


    @ApiStatus.Experimental
    @Nullable
    public final <T extends IngwerCommand> Identity identityCommand(IngwerCommandSender commandSender, @NotNull CommandTarget senderType, Consumer<Identity> action) {
        if (senderType.equals(CommandTarget.INGAME)) {
            if (commandSender instanceof Identity identity) {
                action.accept(identity);
                return identity;
            }
        }
        return null;
    }


    @ApiStatus.Experimental
    @NotNull
    public final <T extends IngwerCommand> Pair<@Nullable Identity, @Nullable Player> identityPlayerCommand(IngwerCommandSender commandSender, @NotNull CommandTarget senderType, String[] cmd, BiConsumer<Identity, Player> action) {
        StraightLineStringMessage specify_online_player = new StraightLineStringMessage("Please specify an online Player!");
        if (senderType.equals(CommandTarget.INGAME)) {
            if (commandSender instanceof Identity identity) {
                if (cmd.length == 1) {
                    specify_online_player.send(identity);
                } else if (cmd.length > 2) {
                    specify_online_player.send(identity);
                } else if (cmd.length == 2) {
                    String s = cmd[1];
                    Player player = Bukkit.getPlayer(s);
                    if (player != null) {
                        action.accept(identity, player);
                        return Pair.of(identity, player);
                    } else {
                        specify_online_player.send(identity);
                    }
                }
                return Pair.of(identity, null);
            }
        }
        return Pair.of(null, null);
    }

    @ApiStatus.Experimental
    @NotNull
    public final <T extends IngwerCommand> Pair<@Nullable Identity, @Nullable Identity> identityIdentityCommand(IngwerCommandSender commandSender, @NotNull CommandTarget senderType, String[] cmd, BiConsumer<Identity, Identity> action) {
        StraightLineStringMessage specify_user_name = new StraightLineStringMessage("Please specify a valid user_name!");
        if (senderType.equals(CommandTarget.INGAME)) {
            if (commandSender instanceof Identity identity) {
                if (cmd.length == 1) {
                    specify_user_name.send(identity);
                } else if (cmd.length > 2) {
                    specify_user_name.send(identity);
                } else if (cmd.length == 2) {
                    String s = cmd[1];
                    Identity target = Ingwer.getStorage().getIdentityByName(s);
                    if (target != null) {
                        action.accept(identity, target);
                        return Pair.of(identity, target);
                    } else {
                        specify_user_name.send(identity);
                    }
                }
                return Pair.of(identity, null);
            }
        }
        return Pair.of(null, null);
    }


    /**
     * @param action player and identity of commandSender
     * @return player and identity of commandSender
     * @implNote not depending on length or existence of arguments!!
     */
    @ApiStatus.Experimental
    public final Pair<Player, Identity> playerCommand(IngwerCommandSender commandSender, @NotNull CommandTarget senderType, BiConsumer<Player, Identity> action) {
        AtomicReference<Player> player = new AtomicReference<>();
        Identity identity = identityCommand(commandSender, senderType, (id) -> {
            player.set(Bukkit.getPlayer(id.getUUID()));
        });
        if (identity == null || player.get() == null) return null;
        action.accept(player.get(), identity);
        return Pair.of(player.get(), identity);
    }

}
