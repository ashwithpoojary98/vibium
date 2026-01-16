package io.github.ashwithpoojary98.vibium.exception;

/**
 * Exception thrown when an element cannot be found within the timeout period.
 */
public class ElementNotFoundException extends VibiumException {

    private final String selector;

    public ElementNotFoundException(String selector) {
        super("Element not found: " + selector);
        this.selector = selector;
    }

    public ElementNotFoundException(String selector, Throwable cause) {
        super("Element not found: " + selector, cause);
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }
}
