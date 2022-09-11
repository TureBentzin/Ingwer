package de.bentzin.ingwer.message;

public class PrefixedMiniMessageMessage extends MiniMessageMessage {
    public PrefixedMiniMessageMessage(String miniMessage) {
        super(IngwerMessage.INGWER + miniMessage);
    }
}
