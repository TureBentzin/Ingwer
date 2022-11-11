package de.bentzin.ingwer.logging.dynamic;

import de.bentzin.ingwer.logging.Logger;
import org.jetbrains.annotations.NotNull;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;

/**
 * @author Ture Bentzin
 * 11.11.2022
 */
public class DynamicLoggerContainer extends Logger {

    public DynamicLoggerContainer(@NotNull Logger logger) {
        super("DYN//" + logger.getLastName());
        heart = logger;
    }

    private DynamicLoggerContainer(@NotNull Logger logger, DynamicLoggerContainer parent) {
        super("DYN//" + logger.getLastName(),parent);
        heart = logger;
    }

    private ArrayList<DynamicLoggerContainer> children = new ArrayList<>();
    private Logger heart;

    public Logger getHeart() {
        return heart;
    }

    public void update(Logger heart) {
        this.heart = heart;
    }

    /**
     * @param message  message to handle
     * @param logLevel level associated with the message
     * @implNote WARNING: Please check is {@link Logger#isDebugEnabled()} before handling debug messages (logLevel == {@link LogLevel#DEBUG})
     */
    @Override
    public void log(String message, @NotNull LogLevel logLevel) {
        heart.log(message,logLevel);
    }

    @Override
    public DynamicLoggerContainer adopt(String name) {
        return new DynamicLoggerContainer(heart.adopt(name),this);
    }

    @Override
    public void notifyParent(Logger child) {
        if(child instanceof DynamicLoggerContainer dynamicLoggerContainer) {
            children.add(dynamicLoggerContainer);
        }else {
            try {
                throw new OperationNotSupportedException("this container does not allow non container children!");
            } catch (OperationNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
