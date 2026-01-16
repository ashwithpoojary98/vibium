package io.github.ashwithpoojary98.vibium.exception;

/**
 * Base exception for all Vibium-related errors.
 */
public class VibiumException extends RuntimeException {

    public VibiumException(String message) {
        super(message);
    }

    public VibiumException(String message, Throwable cause) {
        super(message, cause);
    }
}
