package io.github.ashwithpoojary98.vibium;

import io.github.ashwithpoojary98.vibium.exception.VibiumException;
import io.github.ashwithpoojary98.vibium.options.LaunchOptions;

import java.util.concurrent.CompletionException;

/**
 * Factory for launching browser instances with synchronous (blocking) API.
 *
 * <p>For non-blocking async operations, use {@link BrowserAsync} instead.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Launch with default options
 * Vibe vibe = new Browser().launch();
 *
 * // Launch with custom options
 * LaunchOptions options = LaunchOptions.builder()
 *     .headless(true)
 *     .port(9222)
 *     .build();
 * Vibe vibe = new Browser().launch(options);
 *
 * // Always close when done
 * vibe.quit();
 * }</pre>
 *
 * @see BrowserAsync
 */
public final class Browser {

    private final BrowserAsync async = new BrowserAsync();

    /**
     * Get the underlying async factory for advanced operations.
     *
     * @return the BrowserAsync instance
     */
    public BrowserAsync async() {
        return async;
    }

    /**
     * Launch a browser with default options.
     *
     * @return the Vibe instance
     * @throws VibiumException if launch fails
     */
    public Vibe launch() {
        return launch(null);
    }

    /**
     * Launch a browser with custom options.
     *
     * @param options launch options (may be null for defaults)
     * @return the Vibe instance
     * @throws VibiumException if launch fails
     */
    public Vibe launch(LaunchOptions options) {
        try {
            return new Vibe(async.launch(options).join());
        } catch (CompletionException e) {
            throw unwrap(e);
        }
    }

    /**
     * Connect to an existing browser at the specified WebSocket URL.
     *
     * @param wsUrl the WebSocket URL (e.g., "ws://localhost:9222")
     * @return the Vibe instance
     * @throws VibiumException if connection fails
     */
    public Vibe connect(String wsUrl) {
        try {
            return new Vibe(async.connect(wsUrl).join());
        } catch (CompletionException e) {
            throw unwrap(e);
        }
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
