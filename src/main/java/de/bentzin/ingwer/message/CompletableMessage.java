package de.bentzin.ingwer.message;

import org.jetbrains.annotations.NotNull;

import java.security.PrivilegedActionException;

/**
 * @implNote This Message should be completed before it's been sent
 */
public interface CompletableMessage{
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
        if(!isCompleted()) throw new UncompletedMessageException();
    }

    class UncompletedMessageException extends IllegalStateException{
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
}
