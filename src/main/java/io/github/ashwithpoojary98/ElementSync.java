package io.github.ashwithpoojary98;

/**
 * Synchronous DOM element wrapper.
 *
 * <p>This is a convenience wrapper around {@link Element} that blocks until
 * operations complete. Use this for simple scripts or when async is not needed.
 *
 * @see Element for the async API
 */
public class ElementSync {

    private final Element element;

    ElementSync(Element element) {
        this.element = element;
    }

    /**
     * Get the underlying async Element instance.
     */
    public Element async() {
        return element;
    }

    /**
     * Get the element info (cached from find).
     */
    public ElementInfo getInfo() {
        return element.getInfo();
    }

    /**
     * Get the HTML tag name.
     */
    public String getTag() {
        return element.getTag();
    }

    /**
     * Get the bounding box.
     */
    public BoundingBox getBox() {
        return element.getBox();
    }

    /**
     * Click the element.
     *
     * <p>Waits for element to be visible, stable, receive events, and enabled.
     */
    public void click() {
        element.click().join();
    }

    /**
     * Click the element.
     *
     * <p>Waits for element to be visible, stable, receive events, and enabled.
     *
     * @param timeout Timeout in milliseconds
     */
    public void click(int timeout) {
        element.click(timeout).join();
    }

    /**
     * Type text into the element.
     *
     * <p>Waits for element to be visible, stable, receive events, enabled, and editable.
     *
     * @param text The text to type
     */
    public void type(String text) {
        element.type(text).join();
    }

    /**
     * Type text into the element.
     *
     * <p>Waits for element to be visible, stable, receive events, enabled, and editable.
     *
     * @param text    The text to type
     * @param timeout Timeout in milliseconds
     */
    public void type(String text, int timeout) {
        element.type(text, timeout).join();
    }

    /**
     * Clear the element's value.
     */
    public void clear() {
        element.clear().join();
    }

    /**
     * Fill the element with text (clears first, then types).
     *
     * @param text The text to fill
     */
    public void fill(String text) {
        element.fill(text).join();
    }

    /**
     * Get the text content of the element.
     *
     * @return The trimmed text content
     */
    public String text() {
        return element.text().join();
    }

    /**
     * Get an attribute value from the element.
     *
     * @param name The attribute name
     * @return The attribute value, or null if not present
     */
    public String getAttribute(String name) {
        return element.getAttribute(name).join();
    }

    /**
     * Check if the element is visible.
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return element.isVisible().join();
    }

    /**
     * Check if the element is enabled.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return element.isEnabled().join();
    }

    /**
     * Hover over the element.
     */
    public void hover() {
        element.hover().join();
    }

    /**
     * Focus the element.
     */
    public void focus() {
        element.focus().join();
    }

    /**
     * Scroll the element into view.
     */
    public void scrollIntoView() {
        element.scrollIntoView().join();
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
