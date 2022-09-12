package de.bentzin.ingwer.message;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.identity.Identity;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
    TextColor ACCENT = NamedTextColor.GOLD;
    TextColor ERROR = NamedTextColor.RED;
    String COLOR_MM = "<gray>";
    String ACCENT_MM = "<gold>";
    String ERROR_MM = "<red>";
    String COLOR_MM_C = COLOR_MM.replaceFirst("<", "</");
    String ACCENT_MM_C = ACCENT_MM.replaceFirst("<", "</");
    String ERROR_MM_C = ERROR_MM.replaceFirst("<", "</");

    String INGWER_MM = "<gray>[<gradient:light_purple:blue>Ingwer</gradient><gray>]: ";
    String INGWER_HEAD_MM = "<gray>[<gradient:light_purple:blue>Ingwer</gradient><gray>]";

    Component INGWER = mm(INGWER_MM);
    Component INGWER_HEAD = mm(INGWER_HEAD_MM);


    MiniMessageMessage EMPTY_LINE = new MiniMessageMessage(" ");

    @Contract(pure = true)
    static @NotNull Component addPrefix(Component message) {
        Component component = INGWER;
        component = component.append(message);
        return component;
    }

    static void easySend(CommandSender sender, String message) {
        new ComponentMessage(easyFormat(message)).send(sender);
    }


    /**
     * converts (mm) string to component
     *
     * @param miniMessage the message to convert
     * @return component
     */
    static @NotNull Component mm(String miniMessage) {
        return IngwerMessage.miniMessage.deserialize(miniMessage);
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

    static @NotNull String deserializePlain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    static void inform(@NotNull IngwerPermission ingwerPermission, @NotNull IngwerMessage ingwerMessage) {
        inform(ingwerPermission, ingwerMessage, new Identity[0]);
    }

    static void inform(@NotNull IngwerPermission ingwerPermission, @NotNull IngwerMessage ingwerMessage, @NotNull Identity... excluded) {
        List<String> excludedList = Stream.of(excluded).map(Identity::getName).toList();
        Objects.requireNonNull(Ingwer.getStorage().getAllIdentities()).forEach(identity -> {
            if (!excludedList.contains(identity.getName()))
                if (identity.isReachable() && identity.getPermissions().contains(ingwerPermission)) {
                    ingwerMessage.send(identity);
                }
        });
    }

    /**
     * @see IngwerMessage#inform(IngwerPermission, IngwerMessage, Identity...)
     * @deprecated used ONLY if no identity is available!!
     */
    @ApiStatus.Experimental
    @Deprecated
    static void inform_legacy(@NotNull IngwerPermission ingwerPermission, @NotNull IngwerMessage ingwerMessage, @NotNull IngwerCommandSender... excluded) {
        List<String> excludedList = Stream.of(excluded).map(IngwerCommandSender::getName).toList();
        Objects.requireNonNull(Ingwer.getStorage().getAllIdentities()).forEach(identity -> {
            if (!excludedList.contains(identity.getName()))
                if (identity.isReachable() && identity.getPermissions().contains(ingwerPermission)) {
                    ingwerMessage.send(identity);
                }
        });
    }


    //message


    void send(CommandSender recipient);

    void send(IngwerCommandSender recipient);


    /**
     * @param recipientName     name of the recipient
     * @param simplifiedMessage simplifiedMessage can contain the message itself (without decoration and prefix) or a description of the message send to the recipient
     * @implNote can be implemented or custom called to log message sends!
     */
    default void log(String recipientName, String simplifiedMessage) {
        manager().getLogger().info(recipientName + " << " + simplifiedMessage);
    }

    default IngwerMessageManager manager() {
        return Ingwer.getMessageManager();
    }
}
