package de.bentzin.ingwer.thow;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;

import java.sql.SQLException;


public final class IngwerThrower {

    public static boolean SQL_DEBUGMODE = false;

    public static IngwerThrower getInstance() {
        return Ingwer.getIngwerThrower();
    }

    private Logger logger;
            
    public IngwerThrower() {
        logger = Ingwer.getLogger().adopt("Thrower");
    }
    
    

    public  void accept(Throwable throwable) throws IngwerException {
        accept(throwable,ThrowType.GENERAL);
    }

    public  void accept(Throwable throwable, ThrowType type) throws IngwerException {
        //TODO
        if(throwable instanceof SQLException && SQL_DEBUGMODE == false) {
            SQLException sqlException = (SQLException) throwable;
            logger.error(sqlException.getSQLState() + " : " + sqlException.getMessage() + " [" + sqlException.getErrorCode() +"]!");
            StackTraceElement element = sqlException.getStackTrace()[sqlException.getStackTrace().length - 1];
            logger.error(element.toString());
        }else {

            throw new IngwerException(throwable, type);
        }

    }


    public static void acceptS(Throwable throwable) throws IngwerException {
        Ingwer.getIngwerThrower().accept(throwable);
    }

    public static void acceptS(Throwable throwable, ThrowType type) throws IngwerException {
        Ingwer.getIngwerThrower().accept(throwable,type);
    }
}
