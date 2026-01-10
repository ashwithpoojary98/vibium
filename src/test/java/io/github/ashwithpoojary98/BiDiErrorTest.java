package io.github.ashwithpoojary98;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BiDiError exception.
 */
class BiDiErrorTest {

    @Test
    void constructor_setsErrorAndMessage() {
        BiDiError error = new BiDiError("invalid argument", "Selector not found");

        assertEquals("invalid argument", error.getError());
        assertEquals("Selector not found", error.getErrorMessage());
    }

    @Test
    void getMessage_combinesErrorAndMessage() {
        BiDiError error = new BiDiError("timeout", "Operation timed out");

        assertEquals("timeout: Operation timed out", error.getMessage());
    }

    @Test
    void isRuntimeException() {
        BiDiError error = new BiDiError("error", "message");

        assertInstanceOf(RuntimeException.class, error);
    }

    @Test
    void canBeThrownAndCaught() {
        BiDiError thrown = assertThrows(BiDiError.class, () -> {
            throw new BiDiError("no such element", "Element not found: #missing");
        });

        assertEquals("no such element", thrown.getError());
        assertEquals("Element not found: #missing", thrown.getErrorMessage());
    }
}
