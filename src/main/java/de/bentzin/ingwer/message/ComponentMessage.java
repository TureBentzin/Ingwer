package de.bentzin.ingwer.message;

import de.bentzin.ingwer.command.IngwerCommandSender;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ComponentMessage implements OneLinedMessage {

    private final Component message;

    public ComponentMessage(Component message) {
        this.message = message;
    }

    @Override
    public void send(@NotNull CommandSender recipient) {
        recipient.sendMessage(message);
        log(recipient.getName(), IngwerMessage.deserializePlain(getOneLinedComponent()));
    }

    @Override
    public void send(@NotNull IngwerCommandSender recipient) {
        recipient.sendOneLinedMessage(this);
        log(recipient.getName(), IngwerMessage.deserializePlain(getOneLinedComponent()));
    }

    @Override
    public String getOneLinedString() {
        return IngwerMessage.deserialize(message);
    }

    @Override
    public Component getOneLinedComponent() {
        return message;
    }

    public Component getMessage() {
        return message;
    }
}
