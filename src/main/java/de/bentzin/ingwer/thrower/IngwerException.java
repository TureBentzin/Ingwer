package de.bentzin.ingwer.thrower;

public class IngwerException extends RuntimeException {

    private final ThrowType type;

    private final String customMessage;

    public IngwerException(ThrowType type) {
        super();
        customMessage = null;
        this.type = type;
    }

    public IngwerException(String message, ThrowType type) {
        super(message);
        customMessage = message;
        this.type = type;
    }

    public IngwerException(String message, Throwable cause, ThrowType type) {
        super(message, cause);
        customMessage = message;
        this.type = type;
    }

    public IngwerException(Throwable cause, ThrowType type) {
        super(null, cause);
        customMessage = null;
        this.type = type;
    }


    public ThrowType getType() {
        return type;
    }


    @Override
    public String getMessage() {
        if (customMessage != null) {
            return type.getMessage() + " :: " + customMessage;
        }
        return type.getMessage();
    }
}
