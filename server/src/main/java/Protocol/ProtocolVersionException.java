package Protocol;

public class ProtocolVersionException extends Exception {

    public ProtocolVersionException() {
        super();
    }

    public ProtocolVersionException(String message) {
        super(message);
    }

    public ProtocolVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolVersionException(Throwable cause) {
        super(cause);
    }

    protected ProtocolVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
