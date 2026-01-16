package io.github.ashwithpoojary98.vibium.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Box}.
 */
class BoxTest {

    @Test
    void constructor_setsAllFields() {
        Box box = new Box(10.0, 20.0, 100.0, 50.0);

        assertEquals(10.0, box.getX());
        assertEquals(20.0, box.getY());
        assertEquals(100.0, box.getWidth());
        assertEquals(50.0, box.getHeight());
    }

    @Test
    void getCenterX_calculatesCorrectly() {
        Box box = new Box(10.0, 20.0, 100.0, 50.0);

        // centerX = x + width/2 = 10 + 100/2 = 60
        assertEquals(60.0, box.getCenterX());
    }

    @Test
    void getCenterY_calculatesCorrectly() {
        Box box = new Box(10.0, 20.0, 100.0, 50.0);

        // centerY = y + height/2 = 20 + 50/2 = 45
        assertEquals(45.0, box.getCenterY());
    }

    @Test
    void getCenterX_withZeroOrigin() {
        Box box = new Box(0.0, 0.0, 200.0, 100.0);

        assertEquals(100.0, box.getCenterX());
    }

    @Test
    void getCenterY_withZeroOrigin() {
        Box box = new Box(0.0, 0.0, 200.0, 100.0);

        assertEquals(50.0, box.getCenterY());
    }

    @Test
    void getCenterX_withNegativeOrigin() {
        Box box = new Box(-50.0, -25.0, 100.0, 50.0);

        // centerX = -50 + 100/2 = 0
        assertEquals(0.0, box.getCenterX());
    }

    @Test
    void getCenterY_withNegativeOrigin() {
        Box box = new Box(-50.0, -25.0, 100.0, 50.0);

        // centerY = -25 + 50/2 = 0
        assertEquals(0.0, box.getCenterY());
    }

    @Test
    void toString_containsAllValues() {
        Box box = new Box(10.0, 20.0, 100.0, 50.0);
        String str = box.toString();

        assertTrue(str.contains("10.0"));
        assertTrue(str.contains("20.0"));
        assertTrue(str.contains("100.0"));
        assertTrue(str.contains("50.0"));
        assertTrue(str.startsWith("Box["));
    }

    @Test
    void zeroSizeBox_centersAtOrigin() {
        Box box = new Box(50.0, 50.0, 0.0, 0.0);

        assertEquals(50.0, box.getCenterX());
        assertEquals(50.0, box.getCenterY());
    }
}
