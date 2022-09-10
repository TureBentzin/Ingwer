package de.bentzin.ingwer.message.builder;

import de.bentzin.ingwer.message.IngwerMessage;
import de.bentzin.ingwer.message.MiniMessageMessage;
import de.bentzin.ingwer.message.OneLinedMessage;
import de.bentzin.ingwer.message.PatternedMiniMessageMessage;
import de.bentzin.ingwer.utils.Hardcode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Hardcode that makes your life easy when dealing with simple messages
 */
@Hardcode
public class MessageBuilder implements Cloneable{

    @Contract(" -> new")
    public static @NotNull MessageBuilder empty() {return new MessageBuilder("");}
    @Contract(" -> new")
    public static @NotNull MessageBuilder prefixed(){return new MessageBuilder(IngwerMessage.INGWER_MM);}

    @Deprecated
    public static @NotNull MessageBuilder custom(String initial){return new MessageBuilder(initial);}

    public static MessageBuilder informMessageBuilder() {
        return MessageBuilder.prefixed().add("[<gradient:blue:aqua>INFORM</gradient>]:  ").c();
    }

    private final String defaultMiniMessage;
    private StringBuilder miniMessageBuilder;

    protected MessageBuilder(String defaultMiniMessage) {
        this.defaultMiniMessage = Objects.requireNonNull(defaultMiniMessage);
        reset();
    }

    /**
     * @param s miniMessage
     * @return this
     */
    public MessageBuilder add(String s) {
        miniMessageBuilder.append(s);
        return this;
    }

    /**
     * @param c color
     * @param s miniMessage
     * @return this
     */
    public MessageBuilder add(@NotNull C c, String s) {
        miniMessageBuilder.append(c.insert(s));
        return this;
    }


    public MessageBuilder reset() {
        miniMessageBuilder = new StringBuilder(defaultMiniMessage);
        return this;
    }

    /**
     *
     * @return new OneLinedMessage with deserialized minimessage inside
     */
    public OneLinedMessage build() {
        return new MiniMessageMessage(miniMessageBuilder.toString());
    }

    public PatternedMiniMessageMessage toCompletableMessage() {
        return PatternedMiniMessageMessage.fromMiniMessage(miniMessageBuilder.toString());
    }

    public String getMiniMessage() {
        return miniMessageBuilder.toString();
    }

    @Override
    public String toString() {
        String s = IngwerMessage.deserializePlain(build().getOneLinedComponent());
        if(s == null || s.equals("")) {
            return "Empty MessageBuilder#" + hashCode();
        }
        return s;
    }

    public MessageBuilder c() {
        miniMessageBuilder.append(IngwerMessage.COLOR_MM);
        return this;
    }

    public MessageBuilder a() {
        miniMessageBuilder.append(IngwerMessage.ACCENT_MM);
        return this;
    }

    public MessageBuilder e() {
        miniMessageBuilder.append(IngwerMessage.ERROR_MM);
        return this;
    }

    public String getDefault() {
        return defaultMiniMessage;
    }

    /**
     *
     * @return new StringBuilder containing the "current" String.
     */
    public StringBuilder exportBuilder() {
        return new StringBuilder(miniMessageBuilder.toString());
    }

    @Override
    public MessageBuilder clone(){
        return MessageBuilder.empty().add(getMiniMessage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageBuilder that)) return false;
        return miniMessageBuilder.toString().equals(that.miniMessageBuilder.toString());
    }

    @Override
    public int hashCode() {
        return miniMessageBuilder.hashCode();
    }
}
