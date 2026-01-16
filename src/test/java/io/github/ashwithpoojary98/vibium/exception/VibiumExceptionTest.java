package io.github.ashwithpoojary98.vibium.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link VibiumException}.
 */
class VibiumExceptionTest {

    @Test
    void constructor_withMessage_setsMessage() {
        VibiumException exception = new VibiumException("Test error message");

        assertEquals("Test error message", exception.getMessage());
    }

    @Test
    void constructor_withMessageAndCause_setsBoth() {
        Throwable cause = new RuntimeException("Root cause");
        VibiumException exception = new VibiumException("Wrapper message", cause);

        assertEquals("Wrapper message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void isRuntimeException() {
        VibiumException exception = new VibiumException("error");

        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void canBeThrownAndCaught() {
        VibiumException thrown = assertThrows(VibiumException.class, () -> {
            throw new VibiumException("Test exception");
        });

        assertEquals("Test exception", thrown.getMessage());
    }

    @Test
    void canCatchAsRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new VibiumException("Test");
        });
    }

    @Test
    void causeChainIsPreserved() {
        Exception root = new IllegalArgumentException("root");
        Exception middle = new RuntimeException("middle", root);
        VibiumException top = new VibiumException("top", middle);

        assertSame(middle, top.getCause());
        assertSame(root, top.getCause().getCause());
    }
}
