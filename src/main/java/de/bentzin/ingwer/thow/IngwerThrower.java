package de.bentzin.ingwer.thow;

public class IngwerThrower {

    public static void accept(Throwable throwable) throws IngwerException {
        accept(throwable,ThrowType.GENERAL);
    }

    public static void accept(Throwable throwable, ThrowType type) throws IngwerException {
        //TODO
        throw new IngwerException(throwable, type);
    }
}
