package de.bentzin.ingwer.thrower;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.utils.StopCode;
import de.bentzin.tools.register.Registerator;

import java.sql.SQLException;
import java.util.function.Consumer;


public final class IngwerThrower {

    public static final boolean SQL_DEBUGMODE = true;
    private final Logger logger;

    /**
     * @apiNote The FatalActions will be executed on a reported fatal. They should be as safe as possible and mostly independent of Ingwers initialization.
     * Please check for everything here! Do not assume things be correctly initialized because a fatal can mean that exact this initialization failed!
     */
    private final Registerator<Consumer<Throwable>> fatalActions = new Registerator<>();

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

    /**
     * @apiNote The FatalActions will be executed on a reported fatal. They should be as safe as possible and mostly independent of Ingwers initialization.
     * Please check for everything here! Do not assume things be correctly initialized because a fatal can mean that exact this initialization failed!
     * @see Registerator
     */
    public Registerator<Consumer<Throwable>> getFatalActions() {
        return fatalActions;
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
            if (type != ThrowType.FATAL)
                throw new IngwerException(throwable, type);
            else {
                logger.error("FATAL: Ingwer will be stopped after Exception occurred in critical Workflows!!");
                throwable.printStackTrace();
                //run FatalActions
                logger.warning("FATAL: It may be possible that Ingwer will lose settings or data because of the automatic fixing routine!");
                fatalActions.forEach(action -> action.accept(throwable));
                Ingwer.stop(StopCode.FATAL);
            }

        }

    }
}
