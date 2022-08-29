package de.bentzin.ingwer.message;

import java.util.Iterator;

public interface MultilinedMessage extends IngwerMessage {
    Iterator<OneLinedMessage> get();
    String[] getLabel();
    String[] getPlainLabel();
    int getDepth();
}
