package de.bentzin.ingwer.utils.cmdreturn;

import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class CommandReturnSystem {

    private final Logger logger;
    private final List<CommandReturn> commandReturnList = new ArrayList<>();

    public CommandReturnSystem(@NotNull Logger parent) {
        this.logger = parent.adopt("CRS");
    }

    @TestOnly
    public static void main(String[] args) {
        CommandReturnSystem system = new CommandReturnSystem(new SystemLogger("SYSTEM"));
        system.getLogger().setDebug(true);
        for (int i = 0; i < 40; i++) {
            system.getLogger().info("Gen: " + i);
            system.generateNewCommand();
        }

    }

    private Logger getLogger() {
        return logger;
    }

    private List<String> commandList() {
        return commandReturnList.stream().map(CommandReturn::command).toList();
    }

    public @NotNull CommandReturn addNewReturn(Runnable action, UUID owner) {
        CommandReturn commandReturn = new CommandReturn(generateNewCommand(), action, owner);
        commandReturnList.add(commandReturn);
        return commandReturn;
    }

    public @NotNull String generateCommand(int length) {
        Random random = new Random();
        int leftLimit = 97; //a
        int rightLimit = 122; //z


        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(() -> new StringBuilder("/"), StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        getLogger().debug("generated: \"" + generatedString + "\"");
        return generatedString;
    }

    public String generateNewCommand() {
        List<String> strings = new ArrayList<>(commandList());
        String s = generateCommand(124);
        int failsafe = 0;
        while (strings.contains(s) && failsafe < 40) {
            s = generateCommand(124);
            failsafe++;
        }
        if (failsafe >= 39) {
            try {
                throw new NoSuchElementException("cant generate new command. Timeout");
            } catch (NoSuchElementException e) {
                e.setMessage("Won in Lotto: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return s;
    }

    public boolean runThrough(String command, UUID uuid) {
        List<String> strings = commandList();
        if (strings.contains(command)) {
            for (CommandReturn commandReturn : commandReturnList) {
                if (commandReturn.command().equals(command)) {
                    if (commandReturn.owner().equals(uuid)) {
                        commandReturn.run();
                        return true;
                    } else {
                        Player player = Bukkit.getPlayer(uuid);
                        getLogger().warning(
                                player != null ? player.getName() : uuid +
                                        " tried to execute foreign returnCommand! >> " + command);
                    }
                }
            }
        }
        return false;
    }

    public boolean check(String command) {
        return commandList().contains(command);
    }


    @SuppressWarnings("unused")
    private static class NoSuchElementException extends java.util.NoSuchElementException {
        private String message;

        /**
         * Constructs a {@code NoSuchElementException} with {@code null}
         * as its error message string.
         */
        public NoSuchElementException() {
        }

        /**
         * Constructs a {@code NoSuchElementException} with the specified detail
         * message and cause.
         *
         * @param s     the detail message, or null
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link #getCause()} method), or null
         * @since 15
         */
        public NoSuchElementException(String s, Throwable cause) {
            super(s, cause);
        }

        /**
         * Constructs a {@code NoSuchElementException} with the specified cause.
         * The detail message is set to {@code (cause == null ? null :
         * cause.toString())} (which typically contains the class and
         * detail message of {@code cause}).
         *
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link #getCause()} method)
         * @since 15
         */
        public NoSuchElementException(Throwable cause) {
            super(cause);
        }

        /**
         * Constructs a {@code NoSuchElementException}, saving a reference
         * to the error message string {@code s} for later retrieval by the
         * {@code getMessage} method.
         *
         * @param s the detail message.
         */
        public NoSuchElementException(String s) {
            super(s);
        }

        /**
         * Returns the detail message string of this throwable.
         *
         * @return the detail message string of this {@code Throwable} instance
         * (which may be {@code null}).
         */
        @Override
        public String getMessage() {
            return message;
        }

        public void setMessage(String s) {
            message = s;
        }

        /**
         * Creates a localized description of this throwable.
         * Subclasses may override this method in order to produce a
         * locale-specific message.  For subclasses that do not override this
         * method, the default implementation returns the same result as
         * {@code getMessage()}.
         *
         * @return The localized description of this throwable.
         * @since 1.1
         */
        @Override
        public String getLocalizedMessage() {
            return message;
        }
    }
}
