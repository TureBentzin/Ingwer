package de.bentzin.ingwer.message;

import de.bentzin.ingwer.command.IngwerCommandSender;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class StraightLineStringMessage implements IngwerMessage{

    private String message;

    public StraightLineStringMessage(String message) {
        this.message = message;
    }

    @Override
    public void send(CommandSender sender) {
        Component component = IngwerMessage.easyFormat(message);

    }

    @Override
    public void send(IngwerCommandSender sender) {

    }
}
