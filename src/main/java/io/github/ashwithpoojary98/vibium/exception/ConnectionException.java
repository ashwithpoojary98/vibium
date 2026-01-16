package io.github.ashwithpoojary98.vibium.exception;

/**
 * Exception thrown when connection to the browser fails.
 */
public class ConnectionException extends VibiumException {

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
