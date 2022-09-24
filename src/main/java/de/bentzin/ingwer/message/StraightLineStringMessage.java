package de.bentzin.ingwer.message;

import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StraightLineStringMessage implements OneLinedMessage {

    @NotNull
    private final String message;
    private final Component component;

    public StraightLineStringMessage(@NotNull String message) {
        this.message = message.replace("\n", "");
        component = IngwerMessage.easyFormat(message);
    }

    @Override
    public void send(@NotNull CommandSender recipient) {
        recipient.sendMessage(component);
        log(recipient.getName(), IngwerMessage.deserializePlain(component));
    }

    @Override
    public void send(@NotNull IngwerCommandSender recipient) {
        if (recipient instanceof Identity) {
            recipient.sendOneLinedMessage(this);
        }
        log(recipient.getName(), IngwerMessage.deserializePlain(component));
    }


    @Override
    public String getOneLinedString() {
        return message;
    }

    @Override
    public Component getOneLinedComponent() {
        return component;
    }
}
