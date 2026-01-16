package io.github.ashwithpoojary98.vibium;

import io.github.ashwithpoojary98.vibium.exception.VibiumException;
import io.github.ashwithpoojary98.vibium.internal.BiDiClient;
import io.github.ashwithpoojary98.vibium.options.FindOptions;

import java.time.Duration;
import java.util.concurrent.CompletionException;

/**
 * Main browser automation interface with synchronous (blocking) API.
 *
 * <p>For non-blocking async operations, use {@link VibeAsync} instead.
 *
 * <p>Example usage:
 * <pre>{@code
 * Vibe vibe = new Browser().launch();
 * vibe.go("https://example.com");
 * Element button = vibe.find("button.submit");
 * button.click();
 * vibe.quit();
 * }</pre>
 *
 * @see VibeAsync
 * @see Browser
 */
public final class Vibe implements AutoCloseable {

    private final VibeAsync async;

    /**
     * Create a new Vibe instance wrapping an async instance.
     *
     * @param async the async instance to wrap
     */
    public Vibe(VibeAsync async) {
        this.async = async;
    }

    /**
     * Get the underlying async instance for advanced operations.
     *
     * @return the VibeAsync instance
     */
    public VibeAsync async() {
        return async;
    }

    /**
     * Navigate to a URL.
     *
     * @param url the URL to navigate to
     * @throws VibiumException if navigation fails
     */
    public void go(String url) {
        try {
            async.go(url).join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Capture a screenshot of the viewport.
     *
     * @return PNG image data as bytes
     * @throws VibiumException if screenshot fails
     */
    public byte[] screenshot() {
        try {
            return async.screenshot().join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Find an element by CSS selector or XPath.
     *
     * <p>Waits for the element to exist before returning (default 30 second timeout).
     * Automatically detects if the selector is XPath (starts with "/" or "(") or CSS.
     *
     * <p>Examples:
     * <pre>{@code
     * // CSS selector
     * vibe.find("button.submit");
     * vibe.find("a[href='/roadmap']");
     *
     * // XPath selector
     * vibe.find("//a[text()='Roadmap']");
     * vibe.find("//button[@class='submit']");
     * }</pre>
     *
     * @param selector CSS selector or XPath expression
     * @return an Element instance
     * @throws VibiumException if element not found or operation fails
     */
    public Element find(String selector) {
        try {
            return new Element(async.find(selector).join());
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Find an element by CSS selector or XPath with custom timeout.
     *
     * @param selector CSS selector or XPath expression
     * @param timeout  maximum time to wait for the element
     * @return an Element instance
     * @throws VibiumException if element not found or operation fails
     */
    public Element find(String selector, Duration timeout) {
        try {
            return new Element(async.find(selector, timeout).join());
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Find an element using FindOptions.
     *
     * @param selector CSS selector
     * @param options  find options
     * @return an Element instance
     * @throws VibiumException if element not found or operation fails
     */
    public Element find(String selector, FindOptions options) {
        try {
            return new Element(async.find(selector, options).join());
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Evaluate JavaScript in the page context.
     *
     * @param script the JavaScript code to execute
     * @param clazz  the expected return type
     * @param <T>    the return type
     * @return the result
     * @throws VibiumException if script execution fails
     */
    public <T> T evaluate(String script, Class<T> clazz) {
        try {
            return async.evaluate(script, clazz).join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Evaluate JavaScript in the page context.
     *
     * @param script the JavaScript code to execute
     * @return the result as Object
     * @throws VibiumException if script execution fails
     */
    public Object evaluate(String script) {
        try {
            return async.evaluate(script).join();
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Get the underlying BiDi client for advanced operations.
     *
     * @return the BiDiClient instance
     */
    public BiDiClient getClient() {
        return async.getClient();
    }

    /**
     * Check if the browser is still connected.
     *
     * @return true if connected
     */
    public boolean isConnected() {
        return async.isConnected();
    }

    /**
     * Close the browser and release resources.
     */
    public void quit() {
        async.quit();
    }

    /**
     * Close the browser (same as {@link #quit()}).
     */
    @Override
    public void close() {
        quit();
    }

    /**
     * Unwrap CompletionException to get the actual cause.
     */
    private RuntimeException unwrap(CompletionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new VibiumException(cause.getMessage(), cause);
    }
}
