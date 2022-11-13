package de.bentzin.ingwer.logging.dynamic;

import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.thrower.IngwerThrower;
import de.bentzin.ingwer.thrower.ThrowType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * The {@link DynamicLoggerContainer} presents a way to use IngwerLoggers Dynamically. This would allow to change the {@link Logger} on the
 * flight and redirect all Log-Outputs to another kind of Logger. This is not 100% save and may not be supported.
 * It is not used by Ingwer by default but if you like there is nothing stopping you from do so.
 * @implNote This may be unchecked or untested - do not assume that a dynamicLogger works as intended or as expected
 *
 * @author Ture Bentzin
 * 11.11.2022
 */
@ApiStatus.Experimental
public class DynamicLoggerContainer extends Logger {

    public final String name;

    public DynamicLoggerContainer(@NotNull Logger logger) {
        super("DYN//" + logger.getLastName());
        name = logger.getLastName();
        heart = logger;
    }

    private DynamicLoggerContainer(@NotNull Logger logger, DynamicLoggerContainer parent) {
        super("DYN//" + logger.getLastName(),parent);
        name = logger.getLastName();
        heart = logger;
    }

    private final ArrayList<DynamicLoggerContainer> children = new ArrayList<>();
    private Logger heart;

    public Logger getHeart() {
        return heart;
    }

    public String getContainerName() {
        return name;
    }

    /**
     * This will update all hearts below this logger (this logger included)
     * @param superHeart
     */
    public void update(@NotNull Logger superHeart) {
        this.heart = superHeart.adopt(heart.getLastName());
        children.forEach(container -> container.update(heart));
    }

    /**
     * Sets this Containers Heart to newHeart and updates all below
     * @param newHeart
     */
    public void setHeart(@NotNull Logger newHeart) {
        this.heart = newHeart;
        children.forEach(children -> children.update(heart));
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
                throw new OperationNotSupportedException("{" + getContainerName() + "} :: this container does not allow non container children!");
            } catch (OperationNotSupportedException e) {
                IngwerThrower.acceptS(e, ThrowType.LOGGING);
            }
        }

    }

    protected ArrayList<DynamicLoggerContainer> getChildren() {
        return children;
    }

    /**
     * @return an {@link java.util.Collections.UnmodifiableList} with the children
     * @apiNote this is intended for use outside this Object - if you want to change stuff on the children there is {@link DynamicLoggerContainer#getChildren()}
     */
    public List<DynamicLoggerContainer> getChildrenList() {
        return children.stream().toList();
    }
}
