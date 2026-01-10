package io.github.ashwithpoojary98;

/**
 * Information about an element.
 *
 * @param tag  The HTML tag name (e.g., "div", "button")
 * @param text The text content of the element
 * @param box  The bounding box of the element
 */
public record ElementInfo(String tag, String text, BoundingBox box) {
}
