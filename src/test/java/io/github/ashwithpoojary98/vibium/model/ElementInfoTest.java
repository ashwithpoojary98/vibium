package io.github.ashwithpoojary98.vibium.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ElementInfo}.
 */
class ElementInfoTest {

    @Test
    void builder_createsElementInfoWithAllFields() {
        Box box = new Box(0, 0, 100, 50);

        ElementInfo info = ElementInfo.builder()
                .tagName("button")
                .textContent("Click me")
                .box(box)
                .build();

        assertEquals("button", info.getTagName());
        assertEquals("Click me", info.getTextContent());
        assertEquals(box, info.getBox());
    }

    @Test
    void builder_allowsNullTextContent() {
        Box box = new Box(0, 0, 100, 50);

        ElementInfo info = ElementInfo.builder()
                .tagName("input")
                .textContent(null)
                .box(box)
                .build();

        assertNull(info.getTextContent());
    }

    @Test
    void builder_allowsEmptyTextContent() {
        Box box = new Box(0, 0, 100, 50);

        ElementInfo info = ElementInfo.builder()
                .tagName("input")
                .textContent("")
                .box(box)
                .build();

        assertEquals("", info.getTextContent());
    }

    @Test
    void builder_allowsNullBox() {
        ElementInfo info = ElementInfo.builder()
                .tagName("span")
                .textContent("text")
                .box(null)
                .build();

        assertNull(info.getBox());
    }

    @Test
    void builder_staticFactoryMethodExists() {
        assertNotNull(ElementInfo.builder());
    }

    @Test
    void builder_chainMethodsReturnBuilder() {
        ElementInfo.Builder builder = ElementInfo.builder();

        assertSame(builder, builder.tagName("div"));
        assertSame(builder, builder.textContent("content"));
        assertSame(builder, builder.box(new Box(0, 0, 10, 10)));
    }

    @Test
    void builder_withDifferentTagNames() {
        String[] tags = {"div", "span", "button", "input", "a", "form", "table", "tr", "td"};

        for (String tag : tags) {
            ElementInfo info = ElementInfo.builder()
                    .tagName(tag)
                    .build();

            assertEquals(tag, info.getTagName());
        }
    }

    @Test
    void elementInfo_isImmutable() {
        Box box = new Box(10, 20, 30, 40);
        ElementInfo info = ElementInfo.builder()
                .tagName("div")
                .textContent("original")
                .box(box)
                .build();

        // Cannot modify after creation - getters return the same values
        assertEquals("div", info.getTagName());
        assertEquals("original", info.getTextContent());
        assertSame(box, info.getBox());
    }
}
