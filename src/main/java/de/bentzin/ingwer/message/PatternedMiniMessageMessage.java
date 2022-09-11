package de.bentzin.ingwer.message;


import de.bentzin.ingwer.message.builder.MessageBuilder;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.ingwer.utils.Irreversible;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.PrivilegedActionException;
import java.util.HashSet;
import java.util.Set;

/**
 * Pattern definition:
 * {0} {1} ... {patternCount - 1}
 *
 * @implNote In this: "pattern" & "query" have the same meaning!
 */
public class PatternedMiniMessageMessage implements CompletableMessage {

    private final int patternCount;
    final Set<String> patternQueries;
    private String miniMessage;


    public PatternedMiniMessageMessage(String miniMessage, int patternCount) {
        this.miniMessage = miniMessage;
        this.patternCount = patternCount;

        patternQueries = generatePatternQueries(this.patternCount);
    }

    /**
     * @implNote only supports miniMessages with up to 50 queries - all above will be ignored!!
     */
    @Contract("_ -> new")
    public static @NotNull PatternedMiniMessageMessage fromMiniMessage(String mm) {
        int i = 0;
        for (String patternQuery : generatePatternQueries(50)) {
            if (mm.contains(patternQuery)) i++;
        }
        return new PatternedMiniMessageMessage(mm, i);
    }

    /**
     * @implNote only supports miniMessages with up to 50 queries - all above will be ignored!!
     * @implNote Never call from {@link MessageBuilder#toCompletableMessage()}!
     */
    @Contract("_ -> new")
    public static @NotNull PatternedMiniMessageMessage fromMessageBuilder(@NotNull MessageBuilder messageBuilder) {
        return messageBuilder.toCompletableMessage();
    }

    public static @NotNull Set<String> generatePatternQueries(int count) {
        Set<String> queries = new HashSet<>();
        for (int i = 0; i < count; i++) {
            queries.add(generateQuery(i));
        }
        return queries;
    }

    @Contract(pure = true)
    public static @NotNull String generateQuery(int i) {
        return "{" + i + "}";
    }

    public static int resolveQuery(@NotNull String query) throws ResolutionException {
        int i;
        String s = query.replace("\\{", "");
        s = s.replace("\\}", "");
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException numberFormatException) {
            throw new ResolutionException("cant resolve query: \"" + query + "\"", numberFormatException);
        }
        return i;
    }

    @Override
    public boolean isCompleted() {
        return countRemainingPatterns() == 0;
    }

    @Override
    public @NotNull IngwerMessage get() throws UncompletedMessageException {
        checkAndThrow();
        return new MiniMessageMessage(miniMessage);
    }

    public int getPatternCount() {
        return patternCount;
    }

    public int countRemainingPatterns() {
        //patternQueries.forEach(s -> StringUtils.countMatches(miniMessage,s));
        int i = 0;
        for (String patternQuery : patternQueries) {
            i = i + StringUtils.countMatches(miniMessage, patternQuery);
        }
        return i;
    }

    public Set<String> getRemainingPatterns() {
        Set<String> remaining = new HashSet<>();
        for (String patternQuery : patternQueries) {
            if (miniMessage.contains(patternQuery)) remaining.add(patternQuery);
        }
        return remaining;
    }

    @Irreversible
    public void insert(int query, String insertion) {
        miniMessage = miniMessage.replace(generateQuery(query), insertion);
    }

    @Irreversible
    public void deleteRemainingQueries() {
        getRemainingPatterns().forEach(s -> {
            try {
                insert(resolveQuery(s), "");
            } catch (ResolutionException e) {
                IngwerThrower.acceptS(e, ThrowType.MESSAGE);
            }
        });
    }

    public static class ResolutionException extends Exception {
        /**
         * Constructs a new exception with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         */
        public ResolutionException() {
        }

        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public ResolutionException(String message) {
            super(message);
        }

        /**
         * Constructs a new exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A {@code null} value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public ResolutionException(String message, Throwable cause) {
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
         *              {@link #getCause()} method).  (A {@code null} value is
         *              permitted, and indicates that the cause is nonexistent or
         *              unknown.)
         * @since 1.4
         */
        public ResolutionException(Throwable cause) {
            super(cause);
        }

        /**
         * Constructs a new exception with the specified detail message,
         * cause, suppression enabled or disabled, and writable stack
         * trace enabled or disabled.
         *
         * @param message            the detail message.
         * @param cause              the cause.  (A {@code null} value is permitted,
         *                           and indicates that the cause is nonexistent or unknown.)
         * @param enableSuppression  whether or not suppression is enabled
         *                           or disabled
         * @param writableStackTrace whether or not the stack trace should
         *                           be writable
         * @since 1.7
         */
        public ResolutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
