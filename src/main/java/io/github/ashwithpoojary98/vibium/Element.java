package io.github.ashwithpoojary98.vibium;

import io.github.ashwithpoojary98.vibium.exception.VibiumException;
import io.github.ashwithpoojary98.vibium.model.Box;
import io.github.ashwithpoojary98.vibium.model.ElementInfo;

import java.util.concurrent.CompletionException;

/**
 * Represents a DOM element in the browser with synchronous (blocking) API.
 *
 * <p>For non-blocking async operations, use {@link ElementAsync} instead.
 *
 * <p>Provides methods to interact with the element such as clicking,
 * typing, and retrieving properties.
 *
 * @see ElementAsync
 */
public final class Element {

    private final ElementAsync async;

    /**
     * Create a new Element instance wrapping an async instance.
     *
     * @param async the async instance to wrap
     */
    public Element(ElementAsync async) {
        this.async = async;
    }

    /**
     * Get the underlying async instance for advanced operations.
     *
     * @return the ElementAsync instance
     */
    public ElementAsync async() {
        return async;
    }

    /**
     * Get the element's tag name.
     *
     * @return the tag name (e.g., "button", "div")
     */
    public String getTagName() {
        return async.getTagName();
    }

    /**
     * Get the element's text content.
     *
     * @return the text content
     */
    public String getText() {
        return async.getText();
    }

    /**
     * Get the element's bounding box.
     *
     * @return the bounding box
     */
    public Box getBox() {
        return async.getBox();
    }

    /**
     * Get the element info.
     *
     * @return the element info
     */
    public ElementInfo getInfo() {
        return async.getInfo();
    }

    /**
     * Click the element at its center point.
     *
     * @throws VibiumException if click fails
     */
    public void click() {
        try {
            async.click().join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Type text into the element.
     *
     * @param text the text to type
     * @throws VibiumException if typing fails
     */
    public void type(String text) {
        try {
            async.type(text).join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Clear the element's value (for input fields).
     *
     * @throws VibiumException if clearing fails
     */
    public void clear() {
        try {
            async.clear().join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Get an attribute value from the element.
     *
     * @param name the attribute name
     * @return the attribute value or null
     * @throws VibiumException if operation fails
     */
    public String getAttribute(String name) {
        try {
            return async.getAttribute(name).join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Check if the element is visible.
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return async.isVisible().join();
    }

    @Override
    public String toString() {
        return String.format("Element[%s selector='%s']", getTagName(), async.getSelector());
    }

    /**
     * Unwrap CompletionException to get the actual cause.
     */
    private RuntimeException unwrap(CompletionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new VibiumException(cause.getMessage(), cause);
    }
}
