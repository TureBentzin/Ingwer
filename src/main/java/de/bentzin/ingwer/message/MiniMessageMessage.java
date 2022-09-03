package de.bentzin.ingwer.message;

public class MiniMessageMessage extends ComponentMessage{
    public MiniMessageMessage(String miniMessage) {
        super(IngwerMessage.mm(miniMessage));
    }
}
