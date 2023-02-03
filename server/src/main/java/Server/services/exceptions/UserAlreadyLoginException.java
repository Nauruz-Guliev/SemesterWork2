package Server.services.exceptions;

public class UserAlreadyLoginException extends Exception{
    public UserAlreadyLoginException() {
        super();
    }

    public UserAlreadyLoginException(String message) {
        super(message);
    }

    public UserAlreadyLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyLoginException(Throwable cause) {
        super(cause);
    }

    protected UserAlreadyLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
