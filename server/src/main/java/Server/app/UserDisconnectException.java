package Server.app;

public class UserDisconnectException extends Exception {

    public UserDisconnectException() {
        super();
    }

    public UserDisconnectException(String message) {
        super(message);
    }

    public UserDisconnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDisconnectException(Throwable cause) {
        super(cause);
    }

    protected UserDisconnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
