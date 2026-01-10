package io.github.ashwithpoojary98;

/**
 * Synchronous browser launcher for Vibium automation.
 *
 * <p>This is a convenience wrapper around {@link Browser} that blocks until
 * operations complete. Use this for simple scripts or when async is not needed.
 *
 * <pre>{@code
 * VibeSync vibe = BrowserSync.launch();
 * vibe.go("https://example.com");
 *
 * ElementSync link = vibe.find("a");
 * System.out.println(link.text());
 * link.click();
 *
 * vibe.quit();
 * }</pre>
 *
 * @see Browser for the async API
 */
public class BrowserSync {

    private BrowserSync() {
        // Utility class
    }

    /**
     * Launch a new browser instance.
     *
     * @param headless       Run browser in headless mode (default: visible)
     * @param port           WebSocket port (null for auto-assigned)
     * @param executablePath Path to clicker binary (null for auto-detect)
     * @return A VibeSync instance for browser automation
     */
    public static VibeSync launch(boolean headless, Integer port, String executablePath) {
        Vibe vibe = Browser.launch(headless, port, executablePath).join();
        return new VibeSync(vibe);
    }

    /**
     * Launch a new browser instance with default settings.
     *
     * @return A VibeSync instance for browser automation
     */
    public static VibeSync launch() {
        return launch(false, null, null);
    }

    /**
     * Launch a new browser instance.
     *
     * @param headless Run browser in headless mode
     * @return A VibeSync instance for browser automation
     */
    public static VibeSync launch(boolean headless) {
        return launch(headless, null, null);
    }

    /**
     * Launch a new browser instance.
     *
     * @param headless Run browser in headless mode
     * @param port     WebSocket port (null for auto-assigned)
     * @return A VibeSync instance for browser automation
     */
    public static VibeSync launch(boolean headless, Integer port) {
        return launch(headless, port, null);
    }
}
