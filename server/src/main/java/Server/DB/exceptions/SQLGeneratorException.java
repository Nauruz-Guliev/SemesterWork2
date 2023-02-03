package Server.DB.exceptions;

public class SQLGeneratorException extends Exception{
    public SQLGeneratorException() {
        super();
    }

    public SQLGeneratorException(String message) {
        super(message);
    }

    public SQLGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLGeneratorException(Throwable cause) {
        super(cause);
    }

    protected SQLGeneratorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
