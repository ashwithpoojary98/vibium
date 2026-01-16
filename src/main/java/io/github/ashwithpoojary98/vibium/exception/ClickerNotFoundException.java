package io.github.ashwithpoojary98.vibium.exception;

/**
 * Exception thrown when the Clicker binary cannot be found.
 */
public class ClickerNotFoundException extends VibiumException {

    public ClickerNotFoundException(String message) {
        super(message);
    }

    public ClickerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
