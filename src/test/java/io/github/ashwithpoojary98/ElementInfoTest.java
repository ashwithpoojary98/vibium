package io.github.ashwithpoojary98;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ElementInfo record.
 */
class ElementInfoTest {

    @Test
    void constructor_setsAllFields() {
        BoundingBox box = new BoundingBox(0, 0, 100, 50);
        ElementInfo info = new ElementInfo("button", "Click me", box);

        assertEquals("button", info.tag());
        assertEquals("Click me", info.text());
        assertEquals(box, info.box());
    }

    @Test
    void equals_withSameValues_returnsTrue() {
        BoundingBox box = new BoundingBox(0, 0, 100, 50);
        ElementInfo info1 = new ElementInfo("div", "Hello", box);
        ElementInfo info2 = new ElementInfo("div", "Hello", box);

        assertEquals(info1, info2);
    }

    @Test
    void equals_withDifferentTag_returnsFalse() {
        BoundingBox box = new BoundingBox(0, 0, 100, 50);
        ElementInfo info1 = new ElementInfo("div", "Hello", box);
        ElementInfo info2 = new ElementInfo("span", "Hello", box);

        assertNotEquals(info1, info2);
    }

    @Test
    void equals_withDifferentText_returnsFalse() {
        BoundingBox box = new BoundingBox(0, 0, 100, 50);
        ElementInfo info1 = new ElementInfo("div", "Hello", box);
        ElementInfo info2 = new ElementInfo("div", "World", box);

        assertNotEquals(info1, info2);
    }

    @Test
    void toString_containsTagAndText() {
        BoundingBox box = new BoundingBox(0, 0, 100, 50);
        ElementInfo info = new ElementInfo("button", "Submit", box);
        String str = info.toString();

        assertTrue(str.contains("button"));
        assertTrue(str.contains("Submit"));
    }

    @Test
    void withEmptyText_works() {
        BoundingBox box = new BoundingBox(0, 0, 100, 50);
        ElementInfo info = new ElementInfo("input", "", box);

        assertEquals("", info.text());
    }
}
