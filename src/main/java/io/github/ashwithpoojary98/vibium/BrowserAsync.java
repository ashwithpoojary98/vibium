package io.github.ashwithpoojary98.vibium;

import io.github.ashwithpoojary98.vibium.internal.BiDiClient;
import io.github.ashwithpoojary98.vibium.internal.ClickerProcess;
import io.github.ashwithpoojary98.vibium.options.LaunchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Async factory for launching browser instances.
 *
 * <p>For a simpler blocking API, use {@link Browser} instead.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Launch with default options
 * VibeAsync vibe = new BrowserAsync().launch().join();
 *
 * // Launch with custom options
 * LaunchOptions options = LaunchOptions.builder()
 *     .headless(true)
 *     .port(9222)
 *     .build();
 * VibeAsync vibe = new BrowserAsync().launch(options).join();
 *
 * // Always close when done
 * vibe.quit();
 * }</pre>
 *
 * @see Browser
 */
public final class BrowserAsync {

    private static final Logger log = LoggerFactory.getLogger(BrowserAsync.class);

    /**
     * Launch a browser with default options.
     *
     * @return CompletableFuture containing the VibeAsync instance
     */
    public CompletableFuture<VibeAsync> launch() {
        return launch(null);
    }

    /**
     * Launch a browser with custom options.
     *
     * @param options launch options (may be null for defaults)
     * @return CompletableFuture containing the VibeAsync instance
     */
    public CompletableFuture<VibeAsync> launch(LaunchOptions options) {
        boolean headless = options != null && options.isHeadless();
        Integer port = options != null ? options.getPort() : null;
        String executablePath = options != null ? options.getExecutablePath() : null;

        log.info("Launching browser (headless={}, port={})", headless, port);

        return ClickerProcess.start(headless, port, executablePath)
                .thenCompose(process -> {
                    String wsUrl = "ws://localhost:" + process.getPort();
                    log.debug("Connecting to browser at {}", wsUrl);

                    return BiDiClient.connect(wsUrl)
                            .thenApply(client -> new VibeAsync(client, process));
                });
    }

    /**
     * Connect to an existing browser at the specified WebSocket URL.
     *
     * @param wsUrl the WebSocket URL (e.g., "ws://localhost:9222")
     * @return CompletableFuture containing the VibeAsync instance
     */
    public CompletableFuture<VibeAsync> connect(String wsUrl) {
        log.info("Connecting to existing browser at {}", wsUrl);

        return BiDiClient.connect(wsUrl)
                .thenApply(client -> new VibeAsync(client, null));
    }
}
