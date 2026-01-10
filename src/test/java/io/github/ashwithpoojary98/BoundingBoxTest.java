package io.github.ashwithpoojary98;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BoundingBox record.
 */
class BoundingBoxTest {

    @Test
    void constructor_setsAllFields() {
        BoundingBox box = new BoundingBox(10.0, 20.0, 100.0, 50.0);

        assertEquals(10.0, box.x());
        assertEquals(20.0, box.y());
        assertEquals(100.0, box.width());
        assertEquals(50.0, box.height());
    }

    @Test
    void centerX_calculatesCorrectly() {
        BoundingBox box = new BoundingBox(10.0, 20.0, 100.0, 50.0);

        // centerX = x + width/2 = 10 + 100/2 = 60
        assertEquals(60.0, box.centerX());
    }

    @Test
    void centerY_calculatesCorrectly() {
        BoundingBox box = new BoundingBox(10.0, 20.0, 100.0, 50.0);

        // centerY = y + height/2 = 20 + 50/2 = 45
        assertEquals(45.0, box.centerY());
    }

    @Test
    void centerX_withZeroOrigin() {
        BoundingBox box = new BoundingBox(0.0, 0.0, 200.0, 100.0);

        assertEquals(100.0, box.centerX());
    }

    @Test
    void centerY_withZeroOrigin() {
        BoundingBox box = new BoundingBox(0.0, 0.0, 200.0, 100.0);

        assertEquals(50.0, box.centerY());
    }

    @Test
    void equals_withSameValues_returnsTrue() {
        BoundingBox box1 = new BoundingBox(10.0, 20.0, 100.0, 50.0);
        BoundingBox box2 = new BoundingBox(10.0, 20.0, 100.0, 50.0);

        assertEquals(box1, box2);
    }

    @Test
    void equals_withDifferentValues_returnsFalse() {
        BoundingBox box1 = new BoundingBox(10.0, 20.0, 100.0, 50.0);
        BoundingBox box2 = new BoundingBox(10.0, 20.0, 100.0, 60.0);

        assertNotEquals(box1, box2);
    }

    @Test
    void hashCode_sameForEqualObjects() {
        BoundingBox box1 = new BoundingBox(10.0, 20.0, 100.0, 50.0);
        BoundingBox box2 = new BoundingBox(10.0, 20.0, 100.0, 50.0);

        assertEquals(box1.hashCode(), box2.hashCode());
    }

    @Test
    void toString_containsAllValues() {
        BoundingBox box = new BoundingBox(10.0, 20.0, 100.0, 50.0);
        String str = box.toString();

        assertTrue(str.contains("10.0"));
        assertTrue(str.contains("20.0"));
        assertTrue(str.contains("100.0"));
        assertTrue(str.contains("50.0"));
    }
}
