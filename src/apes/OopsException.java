package apes;

public class OopsException extends Exception{

    public OopsException(String message) {
        super(message);
    }

    public OopsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OopsException(Throwable cause) {
        super(cause);
    }
}
