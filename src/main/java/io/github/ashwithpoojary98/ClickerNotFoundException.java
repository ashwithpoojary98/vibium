package io.github.ashwithpoojary98;

/**
 * Thrown when the clicker binary cannot be found.
 */
public class ClickerNotFoundException extends RuntimeException {

    public ClickerNotFoundException(String message) {
        super(message);
    }

    public ClickerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
