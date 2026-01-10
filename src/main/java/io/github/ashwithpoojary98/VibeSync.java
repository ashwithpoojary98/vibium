package io.github.ashwithpoojary98;

import com.google.gson.JsonObject;

/**
 * Synchronous browser automation interface.
 *
 * <p>This is a convenience wrapper around {@link Vibe} that blocks until
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
 * @see Vibe for the async API
 */
public class VibeSync implements AutoCloseable {

    private final Vibe vibe;

    VibeSync(Vibe vibe) {
        this.vibe = vibe;
    }

    /**
     * Get the underlying async Vibe instance.
     */
    public Vibe async() {
        return vibe;
    }

    /**
     * Navigate to a URL.
     *
     * @param url The URL to navigate to
     */
    public void go(String url) {
        vibe.go(url).join();
    }

    /**
     * Capture a screenshot of the viewport.
     *
     * @return PNG image data as bytes
     */
    public byte[] screenshot() {
        return vibe.screenshot().join();
    }

    /**
     * Find an element by CSS selector.
     *
     * <p>Waits for the element to exist before returning.
     *
     * @param selector CSS selector
     * @return An ElementSync instance
     */
    public ElementSync find(String selector) {
        Element element = vibe.find(selector).join();
        return new ElementSync(element);
    }

    /**
     * Find an element by CSS selector.
     *
     * <p>Waits for the element to exist before returning.
     *
     * @param selector CSS selector
     * @param timeout  Timeout in milliseconds
     * @return An ElementSync instance
     */
    public ElementSync find(String selector, int timeout) {
        Element element = vibe.find(selector, timeout).join();
        return new ElementSync(element);
    }

    /**
     * Get the WebSocket port the browser is listening on.
     */
    public int getPort() {
        return vibe.getPort();
    }

    /**
     * Check if the browser is still connected.
     */
    public boolean isConnected() {
        return vibe.isConnected();
    }

    /**
     * Send a BiDi command to the browser.
     *
     * @param method The BiDi method name (e.g., "browsingContext.navigate")
     * @return The result as JsonObject
     */
    public JsonObject send(String method) {
        return vibe.send(method).join();
    }

    /**
     * Send a BiDi command to the browser with parameters.
     *
     * @param method The BiDi method name (e.g., "browsingContext.navigate")
     * @param params Parameters for the command
     * @return The result as JsonObject
     */
    public JsonObject send(String method, Object params) {
        return vibe.send(method, params).join();
    }

    /**
     * Close the browser and clean up resources.
     */
    public void quit() {
        vibe.close();
    }

    /**
     * Close the browser and clean up resources.
     */
    @Override
    public void close() {
        quit();
    }
}
