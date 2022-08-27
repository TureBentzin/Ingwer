package de.bentzin.ingwer.thow;

import de.bentzin.ingwer.Ingwer;

public class IngwerException extends RuntimeException{

    private final ThrowType type;

    public IngwerException(ThrowType type) {
        super();
        this.type = type;
    }

    public IngwerException(String message, ThrowType type) {
        super(message);
        this.type = type;
    }

    public IngwerException(String message, Throwable cause, ThrowType type) {
        super(message, cause);
        this.type = type;
    }

    public IngwerException(Throwable cause, ThrowType type) {
        super("Error executing Ingwer v. "+ Ingwer.VERSION_STRING,cause);
        this.type = type;
    }


    public ThrowType getType() {
        return type;
    }


}
