package io.github.ashwithpoojary98;

/**
 * Represents the bounding box of an element.
 *
 * @param x      The x-coordinate of the top-left corner
 * @param y      The y-coordinate of the top-left corner
 * @param width  The width of the bounding box
 * @param height The height of the bounding box
 */
public record BoundingBox(double x, double y, double width, double height) {

    /**
     * Get the center x-coordinate.
     */
    public double centerX() {
        return x + width / 2;
    }

    /**
     * Get the center y-coordinate.
     */
    public double centerY() {
        return y + height / 2;
    }
}
