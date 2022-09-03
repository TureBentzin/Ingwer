package de.bentzin.ingwer.utils.cmdreturn;

import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.*;

public class CommandReturnSystem {

    private final Logger logger;

    protected Logger getLogger() {
        return logger;
    }

    private List<CommandReturn> commandReturnList = new ArrayList<>();

    public CommandReturnSystem(@NotNull Logger parent) {
        this.logger = parent.adopt("CRS");
    }

    private List<String> commandList() {
        return commandReturnList.stream().map(CommandReturn::command).toList();
    }

    public CommandReturn addNewReturn(Runnable action, UUID owner) {
        CommandReturn commandReturn = new CommandReturn(generateNewCommand(),action,owner);
        commandReturnList.add(commandReturn);
        return commandReturn;
    }


    public String generateCommand(int length) {
        Random random = new Random();
        int leftLimit = 97; //a
        int rightLimit = 122; //z


        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(() -> new StringBuilder("/"), StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        getLogger().debug("generated: \"" + generatedString +"\"");
        return generatedString;
    }

    public String generateNewCommand() {
        List<String> strings = new ArrayList<>(commandList());
        String s = generateCommand(124);
        int failsave = 0;
        while (strings.contains(s) && failsave < 40){
            s = generateCommand(124);
            failsave++;
        }
        if(failsave >= 39) {
            try {
                throw new NoSuchElementException("cant generate new command. Timeout");
            }catch (NoSuchElementException e) {
                e.setMessage("Won in Lotto: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return s;
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

    public boolean runThrough(String command, UUID uuid) {
        List<String> strings = commandList();
        if(strings.contains(command)) {
            for (CommandReturn commandReturn : commandReturnList) {
                if(commandReturn.command().equals(command)) {
                    if(commandReturn.owner().equals(uuid)) {
                        commandReturn.run();
                        return true;
                    }else {
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

}
