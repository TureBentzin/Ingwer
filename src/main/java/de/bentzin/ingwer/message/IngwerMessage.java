package de.bentzin.ingwer.message;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.IngwerCommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

     MiniMessage miniMessage = MiniMessage.miniMessage();
     TextColor COLOR = NamedTextColor.GRAY;
     TextColor ACCENT = NamedTextColor.BLUE;
     Component INGWER = mm("<gray>[<light_purple>Ingwer<gray>]");

     @Contract(pure = true)
     static @NotNull Component addPrefix(Component message) {
          Component component = INGWER;
          component = component.append(message);
          return component;
     }


     static @NotNull Component mm(String s) {
          return miniMessage.deserialize(s);
     }

     static @NotNull Component colorize(String plain) {
          return Component.text(plain).color(COLOR);
     }

     static @NotNull Component easyFormat(String plain) {
          return addPrefix(colorize(plain));
     }

     static @NotNull String deserialize(Component component) {
          return LegacyComponentSerializer.legacyAmpersand().serialize(component);
     }

    //message


     void send(CommandSender recipient);
     void send(IngwerCommandSender recipient);


     /**
      * @implNote can be implemented or custom called to log message sends!
      * @param recipientName name of the recipient
      * @param simplifiedMessage simplifiedMessage can contain the message itself (without decoration and prefix) or a description of the message send to the recipient
      */
     default void log(String recipientName, String simplifiedMessage) {
          manager().getLogger().info(recipientName + " << " + simplifiedMessage);
     }

     default IngwerMessageManager manager() {
          return Ingwer.getMessageManager();
     }
}
