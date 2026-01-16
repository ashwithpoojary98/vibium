package io.github.ashwithpoojary98.vibium.exception;

/**
 * Exception thrown when a BiDi/CDP command fails.
 */
public class BiDiException extends VibiumException {

    private final int errorCode;

    public BiDiException(String message) {
        super(message);
        this.errorCode = -1;
    }

    public BiDiException(int errorCode, String message) {
        super("BiDi error (" + errorCode + "): " + message);
        this.errorCode = errorCode;
    }

    public BiDiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = -1;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
