package io.github.ashwithpoojary98;

import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Integration tests for browser automation.
 *
 * These tests require the clicker binary to be installed.
 * They will be skipped if the binary is not found.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrowserIntegrationTest {

    private static boolean clickerAvailable = false;

    @BeforeAll
    static void checkClickerAvailable() {
        try {
            Clicker.findClicker();
            clickerAvailable = true;
        } catch (ClickerNotFoundException e) {
            clickerAvailable = false;
            System.out.println("Clicker not found, skipping integration tests: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    void browserLaunch_async_succeeds() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        CompletableFuture<Vibe> future = Browser.launch(true); // headless

        assertDoesNotThrow(() -> {
            Vibe vibe = future.get(60, TimeUnit.SECONDS);
            assertNotNull(vibe);
            assertTrue(vibe.isConnected());
            assertTrue(vibe.getPort() > 0);
            vibe.close();
        });
    }

    @Test
    @Order(2)
    void browserLaunch_sync_succeeds() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true); // headless
            assertNotNull(vibe);
            assertTrue(vibe.isConnected());
            assertTrue(vibe.getPort() > 0);
            vibe.quit();
        });
    }

    @Test
    @Order(3)
    void navigation_works() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true);
            try {
                vibe.go("data:text/html,<h1>Hello World</h1>");
                // If we get here without exception, navigation worked
            } finally {
                vibe.quit();
            }
        });
    }

    @Test
    @Order(4)
    void screenshot_returnsBytes() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true);
            try {
                vibe.go("data:text/html,<h1>Screenshot Test</h1>");
                byte[] screenshot = vibe.screenshot();

                assertNotNull(screenshot);
                assertTrue(screenshot.length > 0, "Screenshot should not be empty");

                // PNG files start with specific bytes
                assertEquals((byte) 0x89, screenshot[0], "Should be PNG format");
                assertEquals((byte) 0x50, screenshot[1], "Should be PNG format");
                assertEquals((byte) 0x4E, screenshot[2], "Should be PNG format");
                assertEquals((byte) 0x47, screenshot[3], "Should be PNG format");
            } finally {
                vibe.quit();
            }
        });
    }

    @Test
    @Order(5)
    void findElement_returnsElement() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true);
            try {
                vibe.go("data:text/html,<button id='btn'>Click Me</button>");
                ElementSync button = vibe.find("#btn", 5000);

                assertNotNull(button);
                assertEquals("button", button.getTag().toLowerCase());
            } finally {
                vibe.quit();
            }
        });
    }

    @Test
    @Order(6)
    void elementText_returnsCorrectText() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true);
            try {
                vibe.go("data:text/html,<p id='text'>Hello World</p>");
                ElementSync element = vibe.find("#text", 5000);

                String text = element.text();
                assertEquals("Hello World", text);
            } finally {
                vibe.quit();
            }
        });
    }

    @Test
    @Order(7)
    void elementGetAttribute_returnsValue() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true);
            try {
                vibe.go("data:text/html,<a id='link' href='https://example.com'>Link</a>");
                ElementSync link = vibe.find("#link", 5000);

                String href = link.getAttribute("href");
                assertEquals("https://example.com", href);
            } finally {
                vibe.quit();
            }
        });
    }

    @Test
    @Order(8)
    void elementIsVisible_returnsTrue() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true);
            try {
                vibe.go("data:text/html,<div id='visible'>Visible</div>");
                ElementSync element = vibe.find("#visible", 5000);

                assertTrue(element.isVisible());
            } finally {
                vibe.quit();
            }
        });
    }

    @Test
    @Order(9)
    void elementIsVisible_hiddenElement_returnsFalse() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            VibeSync vibe = BrowserSync.launch(true);
            try {
                vibe.go("data:text/html,<div id='hidden' style='display:none'>Hidden</div>");
                ElementSync element = vibe.find("#hidden", 5000);

                assertFalse(element.isVisible());
            } finally {
                vibe.quit();
            }
        });
    }

    @Test
    @Order(10)
    void tryWithResources_closesAutomatically() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            try (VibeSync vibe = BrowserSync.launch(true)) {
                vibe.go("data:text/html,<h1>Auto Close Test</h1>");
            }
            // If we get here without exception, auto-close worked
        });
    }

    @Test
    @Order(11)
    void asyncApi_chainsCorrectly() {
        assumeTrue(clickerAvailable, "Clicker binary not available");

        assertDoesNotThrow(() -> {
            Browser.launch(true)
                .thenCompose(vibe ->
                    vibe.go("data:text/html,<h1>Async Test</h1>")
                        .thenApply(v -> vibe)
                )
                .thenCompose(vibe ->
                    vibe.screenshot()
                        .thenApply(bytes -> {
                            assertTrue(bytes.length > 0);
                            return vibe;
                        })
                )
                .thenAccept(Vibe::close)
                .get(60, TimeUnit.SECONDS);
        });
    }
}
