package de.bentzin.ingwer.message;

import java.util.Comparator;
import java.util.Iterator;

public interface MultilinedMessage extends IngwerMessage {
    Iterator<OneLinedMessage> get();

    String[] getLabel();

    String[] getPlainLabel();

    int getDepth();

    default void sort(Comparator<OneLinedMessage> sorter) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This operation is not supported for this message");
    }
}
