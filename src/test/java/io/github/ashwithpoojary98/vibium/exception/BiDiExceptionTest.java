package io.github.ashwithpoojary98.vibium.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BiDiException}.
 */
class BiDiExceptionTest {

    @Test
    void constructor_withMessage_setsMessageAndDefaultErrorCode() {
        BiDiException exception = new BiDiException("Command failed");

        assertEquals("Command failed", exception.getMessage());
        assertEquals(-1, exception.getErrorCode());
    }

    @Test
    void constructor_withErrorCodeAndMessage_formatsBoth() {
        BiDiException exception = new BiDiException(404, "Element not found");

        assertTrue(exception.getMessage().contains("404"));
        assertTrue(exception.getMessage().contains("Element not found"));
        assertEquals(404, exception.getErrorCode());
    }

    @Test
    void constructor_withMessageAndCause_setsAll() {
        Throwable cause = new RuntimeException("WebSocket error");
        BiDiException exception = new BiDiException("Connection failed", cause);

        assertEquals("Connection failed", exception.getMessage());
        assertSame(cause, exception.getCause());
        assertEquals(-1, exception.getErrorCode());
    }

    @Test
    void extendsVibiumException() {
        BiDiException exception = new BiDiException("error");

        assertInstanceOf(VibiumException.class, exception);
    }

    @Test
    void extendsRuntimeException() {
        BiDiException exception = new BiDiException("error");

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void errorCode_withZero() {
        BiDiException exception = new BiDiException(0, "Success");

        assertEquals(0, exception.getErrorCode());
    }

    @Test
    void errorCode_withNegative() {
        BiDiException exception = new BiDiException(-1, "Unknown");

        assertEquals(-1, exception.getErrorCode());
    }

    @Test
    void canBeThrownAndCaughtAsBiDiException() {
        BiDiException thrown = assertThrows(BiDiException.class, () -> {
            throw new BiDiException(500, "Internal error");
        });

        assertEquals(500, thrown.getErrorCode());
    }

    @Test
    void canBeCaughtAsVibiumException() {
        assertThrows(VibiumException.class, () -> {
            throw new BiDiException("error");
        });
    }
}
