package de.bentzin.ingwer.command.ext;

import de.bentzin.ingwer.command.CommandTarget;
import de.bentzin.ingwer.command.IngwerCommandSender;
import org.jetbrains.annotations.NotNull;

public record CommandData(@NotNull IngwerCommandSender commandSender, @NotNull String[] cmd,
                          @NotNull CommandTarget senderType) {
}
