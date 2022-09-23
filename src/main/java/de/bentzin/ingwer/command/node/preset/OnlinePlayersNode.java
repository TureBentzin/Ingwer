package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.NodeTrace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OnlinePlayersNode extends CollectionNode<Player>{


    public OnlinePlayersNode(String name, Predicate<Player> causality) {
        super(name, () -> (Collection<Player>) Bukkit.getOnlinePlayers().stream().takeWhile(causality).toList(), Player::getName);
    }

    public OnlinePlayersNode(String name) {
        super(name,() -> (Collection<Player>) Bukkit.getOnlinePlayers(), Player::getName);
    }

    @ApiStatus.Internal
    protected OnlinePlayersNode(String name, Supplier<Collection<Player>> supplier) {
        super(name, supplier, Player::getName);
    }

    /**
     * @param commandData commandData
     * @param nodeTrace   trace to this (last)
     * @implNote execute is getting called if this is the last node in the trace
     */
    @Override
    public void execute(CommandData commandData, NodeTrace nodeTrace, Player player) {
    }
}
