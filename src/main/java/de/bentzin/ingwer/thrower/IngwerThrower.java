package de.bentzin.ingwer.thrower;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;

import java.sql.SQLException;


public final class IngwerThrower {

    public static final boolean SQL_DEBUGMODE = true;
    private final Logger logger;

    public IngwerThrower() {
        logger = Ingwer.getLogger().adopt("Thrower");
    }

    public static IngwerThrower getInstance() {
        return Ingwer.getIngwerThrower();
    }

    public static void acceptS(Throwable throwable) throws IngwerException {
        Ingwer.getIngwerThrower().accept(throwable);
    }

    public static void acceptS(Throwable throwable, ThrowType type) throws IngwerException {
        Ingwer.getIngwerThrower().accept(throwable, type);
    }

    public void accept(Throwable throwable) throws IngwerException {
        accept(throwable, ThrowType.GENERAL);
    }

    public void accept(Throwable throwable, ThrowType type) throws IngwerException {
        //TODO
        if (throwable instanceof SQLException sqlException && !SQL_DEBUGMODE) {
            logger.error(sqlException.getSQLState() + " : " + sqlException.getMessage() + " [" + sqlException.getErrorCode() + "]!");
            StackTraceElement element = sqlException.getStackTrace()[sqlException.getStackTrace().length - 1];
            logger.error(element.toString());
        } else {

            throw new IngwerException(throwable, type);
        }

    }
}
