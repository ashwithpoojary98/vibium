package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

/**
 * Represents a bounding box with position and dimensions.
 */
@Getter
public final class Box {

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public Box(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the center X coordinate.
     */
    public double getCenterX() {
        return x + width / 2;
    }

    /**
     * Get the center Y coordinate.
     */
    public double getCenterY() {
        return y + height / 2;
    }

    @Override
    public String toString() {
        return String.format("Box[x=%.1f, y=%.1f, width=%.1f, height=%.1f]", x, y, width, height);
    }
}
