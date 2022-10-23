package de.bentzin.ingwer.message;

import com.google.errorprone.annotations.ForOverride;
import org.jetbrains.annotations.Nullable;

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

    /**
     * @return description to be shown if {@link MultilinedMessage#multilineLog(String)} is called.
     */
    @Nullable
    @ForOverride
    default String logDescription(){
        return null;
    }

    default void multilineLog(String recipientName) {
        if(logDescription() == null)
            get().forEachRemaining(oneLinedMessage -> log(recipientName,IngwerMessage.deserializePlain(oneLinedMessage.getOneLinedComponent()),
                    true));
        else
        {
            log(recipientName,"ML:" + logDescription());
        }
    }
}
