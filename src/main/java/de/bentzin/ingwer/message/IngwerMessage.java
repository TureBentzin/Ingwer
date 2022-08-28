package de.bentzin.ingwer.message;

import de.bentzin.ingwer.command.IngwerCommandSender;
import org.bukkit.command.CommandSender;

public interface IngwerMessage {

    //stylesheet
    /*
    <gray>[<light_purple>Ingwer<gray>]:<gray> This is a text with <blue>accent <gray>color!
    <gray>--------------- [<light_purple>Ingwer<gray>]<gray> ---------------
    <gray>+cmd        Description...
    <gray>+say        Description...

    <gray>               [<-] <blue>Page 2 of 4 <gray>[->]
    <gray>--------------- [<light_purple>Ingwer<gray>]<gray> ---------------
     */


    //message


     void send(CommandSender sender);
     void send(IngwerCommandSender sender);

}
