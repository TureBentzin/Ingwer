package de.bentzin.ingwer.message;

import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultipageMessageKeeper {

    private final UUID recipient;
    private final ArrayList<FramedMessage> messages;

    public MultipageMessageKeeper(UUID recipient, ArrayList<FramedMessage> messages) {
        this.recipient = recipient;
        this.messages = messages;
    }

    public MultipageMessageKeeper(UUID recipient, List<OneLinedMessage> content, int pageLength) {
        this.recipient = recipient;
        this.messages = new FramedMultipageMessageGenerator(content).generate(pageLength, recipient, this::send);
    }

    protected boolean isPopulated() {
        return messages != null && messages.size() != 0;
    }

    public void send(Integer page) {
        if (isPopulated()) {

            validatePage(page);
            Player player = Bukkit.getPlayer(recipient);
            if (player != null) {
                messages.get(page -1).send(player); //execute!
            } else {
                try {
                    throw new IllegalArgumentException("uuid is not associated with a player! >> " + recipient);
                } catch (CommandLine.InitializationException e) {
                    IngwerThrower.acceptS(e, ThrowType.MESSAGE);
                }
            }

        } else {
            try {
                throw new CommandLine.InitializationException("messages is not ready yet!");
            } catch (CommandLine.InitializationException e) {
                IngwerThrower.acceptS(e, ThrowType.MESSAGE);
            }
        }
    }

    @Nullable
    public MultilinedMessage getPage(Integer page) {
        if (isPopulated()) {

            validatePage(page);
            Player player = Bukkit.getPlayer(recipient);

            if (player != null) {
                return messages.get(page); //execute!
            } else {
                try {
                    throw new IllegalArgumentException("uuid is not associated with a player! >> " + recipient);
                } catch (CommandLine.InitializationException e) {
                    IngwerThrower.acceptS(e, ThrowType.MESSAGE);
                }
            }

        } else {
            try {
                throw new CommandLine.InitializationException("messages is not ready yet!");
            } catch (CommandLine.InitializationException e) {
                IngwerThrower.acceptS(e, ThrowType.MESSAGE);
            }
        }
        return null;
    }

    private void validatePage(Integer page) {
        if (page > messages.size() || page <= 0) {
            try {
                throw new IllegalArgumentException("invalid page >> " + page + "/" + messages.size());
            } catch (CommandLine.InitializationException e) {
                IngwerThrower.acceptS(e, ThrowType.MESSAGE);
            }
        }


    }
}
