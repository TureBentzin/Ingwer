package de.tdrstudios.ingwer.command;

import de.tdrstudios.ingwer.player.IngwerPlayer;

public abstract interface IngwerCommandExecutorInterface {
    public abstract boolean onIngwerCommand(IngwerPlayer player , IngwerCommand ingwerCommand, String[] args);
}
