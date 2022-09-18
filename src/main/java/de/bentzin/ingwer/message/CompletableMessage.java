package de.bentzin.ingwer.message;

import de.bentzin.ingwer.command.IngwerCommand;
import de.bentzin.ingwer.command.IngwerCommandSender;
import de.bentzin.ingwer.utils.Irreversible;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.PrivilegedActionException;
import java.util.Objects;

/**
 * @implNote This Message should be completed before it's been sent
 * @param <T> insert the implementation of this Interface here!
 */
public interface CompletableMessage extends IngwerMessage,Cloneable {
    boolean isCompleted();

    /**
     * @return completed IngwerMessage
     * @throws UncompletedMessageException when this {@link this#isCompleted()} returns false!
     * @implNote It is highly suggested to call {@link this#checkAndThrow()} in first line!
     */
    @NotNull
    IngwerMessage get() throws UncompletedMessageException;

    /**
     * @throws UncompletedMessageException when this {@link CompletableMessage#isCompleted()} returns false!
     */
    default void checkAndThrow() throws UncompletedMessageException {
        if (!isCompleted()) throw new UncompletedMessageException();
    }

    default void checkOriginAndThrow() throws UnsupportedOperationException {
        if(isOrigin()) throw originException();
    }

    /**
     * @implNote origin() sets the message to a state where it can't be completed. To complete it, you need to call clone();
     * If tried to change anyway, then throw this.originException!
     * @see CompletableMessage#checkOriginAndThrow()
     * @see CompletableMessage#originException()
     * @return this
     */
    @Irreversible
    <T extends CompletableMessage> T origin();

    boolean isOrigin();

    @Override
    default void send(@NotNull IngwerCommandSender recipient) {
        get().send(recipient);
    }

    @Override
    default void send(@NotNull CommandSender recipient) {
        get().send(recipient);
    }

    class UncompletedMessageException extends IllegalStateException {
        /**
         * Constructs an UncompletedMessageException with default message.
         * A detail message is a String that describes this particular exception.
         */
        public UncompletedMessageException() {
            super("IngwerMessage is not completed yet!");
        }

        /**
         * Constructs an UncompletedMessageException with the specified detail
         * message.  A detail message is a String that describes this particular
         * exception.
         *
         * @param s the String that contains a detailed message
         */
        public UncompletedMessageException(String s) {
            super(s);
        }

        /**
         * Constructs a new exception with the specified detail message and
         * cause.
         *
         * <p>Note that the detail message associated with {@code cause} is
         * <i>not</i> automatically incorporated in this exception's detail
         * message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link Throwable#getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link Throwable#getCause()} method).  (A {@code null} value
         *                is permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.5
         */
        public UncompletedMessageException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new exception with the specified cause and a detail
         * message of {@code (cause==null ? null : cause.toString())} (which
         * typically contains the class and detail message of {@code cause}).
         * This constructor is useful for exceptions that are little more than
         * wrappers for other throwables (for example, {@link
         * PrivilegedActionException}).
         *
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link Throwable#getCause()} method).  (A {@code null} value is
         *              permitted, and indicates that the cause is nonexistent or
         *              unknown.)
         * @since 1.5
         */
        public UncompletedMessageException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * @implNote every CompleteableMessage that is by default an {@link this#origin()} should be annotated with this, to warn the developer that he need to clone the annotated Message to complete it. Try to avoid annotating an already completed Message with this
     */
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.METHOD,ElementType.FIELD,ElementType.LOCAL_VARIABLE,ElementType.PARAMETER})
    @interface Origin{}

    default UnsupportedOperationException originException() {
        return new UnsupportedOperationException("This CompletableMessage cant be mutated because its defined as an origin! To complete this use clone() instead!");
    }
}
