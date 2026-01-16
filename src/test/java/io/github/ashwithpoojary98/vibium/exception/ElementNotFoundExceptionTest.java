package io.github.ashwithpoojary98.vibium.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ElementNotFoundException}.
 */
class ElementNotFoundExceptionTest {

    @Test
    void constructor_withSelector_setsMessageAndSelector() {
        ElementNotFoundException exception = new ElementNotFoundException("#submit-button");

        assertEquals("#submit-button", exception.getSelector());
        assertTrue(exception.getMessage().contains("#submit-button"));
        assertTrue(exception.getMessage().contains("Element not found"));
    }

    @Test
    void constructor_withSelectorAndCause_setsBoth() {
        Throwable cause = new RuntimeException("Timeout");
        ElementNotFoundException exception = new ElementNotFoundException(".search-input", cause);

        assertEquals(".search-input", exception.getSelector());
        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains(".search-input"));
    }

    @Test
    void extendsVibiumException() {
        ElementNotFoundException exception = new ElementNotFoundException("div");

        assertInstanceOf(VibiumException.class, exception);
    }

    @Test
    void selector_withComplexCssSelector() {
        String selector = "div.container > ul.list > li:nth-child(2) > a[href*='example']";
        ElementNotFoundException exception = new ElementNotFoundException(selector);

        assertEquals(selector, exception.getSelector());
        assertTrue(exception.getMessage().contains(selector));
    }

    @Test
    void selector_withIdSelector() {
        ElementNotFoundException exception = new ElementNotFoundException("#my-element");

        assertEquals("#my-element", exception.getSelector());
    }

    @Test
    void selector_withClassSelector() {
        ElementNotFoundException exception = new ElementNotFoundException(".my-class");

        assertEquals(".my-class", exception.getSelector());
    }

    @Test
    void selector_withAttributeSelector() {
        ElementNotFoundException exception = new ElementNotFoundException("[data-testid='login']");

        assertEquals("[data-testid='login']", exception.getSelector());
    }

    @Test
    void canBeThrownAndCaught() {
        ElementNotFoundException thrown = assertThrows(ElementNotFoundException.class, () -> {
            throw new ElementNotFoundException("button.submit");
        });

        assertEquals("button.submit", thrown.getSelector());
    }
}
