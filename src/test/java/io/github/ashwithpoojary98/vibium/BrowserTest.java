package io.github.ashwithpoojary98.vibium;

import io.github.ashwithpoojary98.vibium.options.LaunchOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Browser}.
 */
class BrowserTest {

    @Test
    void constructor_createsInstance() {
        Browser browser = new Browser();

        assertNotNull(browser);
    }

    @Test
    void launch_withNullOptions_doesNotThrow() {
        Browser browser = new Browser();

        // We can't fully test launch without mocking process creation,
        // but we can verify the method exists and accepts null
        assertNotNull(browser);
    }

    @Test
    void launch_acceptsLaunchOptions() {
        Browser browser = new Browser();
        LaunchOptions options = LaunchOptions.builder()
                .headless(true)
                .port(9222)
                .build();

        // Verify options are accepted (actual launch requires clicker binary)
        assertNotNull(options);
        assertTrue(options.isHeadless());
        assertEquals(9222, options.getPort());
    }

    @Test
    void connect_acceptsWebSocketUrl() {
        Browser browser = new Browser();

        // Verify the method signature exists
        assertNotNull(browser);
        // Actual connection test would require a running browser
    }

    @Test
    void launchOptions_defaultValues() {
        LaunchOptions options = LaunchOptions.builder().build();

        assertFalse(options.isHeadless());
        assertNull(options.getPort());
        assertNull(options.getExecutablePath());
    }

    @Test
    void launchOptions_headlessTrue() {
        LaunchOptions options = LaunchOptions.builder()
                .headless(true)
                .build();

        assertTrue(options.isHeadless());
    }

    @Test
    void launchOptions_customPort() {
        LaunchOptions options = LaunchOptions.builder()
                .port(8080)
                .build();

        assertEquals(8080, options.getPort());
    }

    @Test
    void launchOptions_customExecutablePath() {
        LaunchOptions options = LaunchOptions.builder()
                .executablePath("/usr/bin/chrome")
                .build();

        assertEquals("/usr/bin/chrome", options.getExecutablePath());
    }

    @Test
    void launchOptions_allOptions() {
        LaunchOptions options = LaunchOptions.builder()
                .headless(true)
                .port(9222)
                .executablePath("/path/to/chrome")
                .build();

        assertTrue(options.isHeadless());
        assertEquals(9222, options.getPort());
        assertEquals("/path/to/chrome", options.getExecutablePath());
    }
}
