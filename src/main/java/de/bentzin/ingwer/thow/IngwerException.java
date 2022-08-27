package de.bentzin.ingwer.thow;

import de.bentzin.ingwer.Ingwer;

public class IngwerException extends RuntimeException{

    public IngwerException() {
        super();
    }

    public IngwerException(String message) {
        super(message);
    }

    public IngwerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IngwerException(Throwable cause) {
        super("Error executing Ingwer v. "+ Ingwer.VERSION_STRING,cause);
    }


}
