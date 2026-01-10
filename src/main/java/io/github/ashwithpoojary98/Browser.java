package io.github.ashwithpoojary98;

import java.util.concurrent.CompletableFuture;

/**
 * Browser launcher for Vibium automation.
 */
public class Browser {

    private Browser() {
        // Utility class
    }

    /**
     * Launch a new browser instance.
     *
     * @param headless       Run browser in headless mode (default: visible)
     * @param port           WebSocket port (null for auto-assigned)
     * @param executablePath Path to clicker binary (null for auto-detect)
     * @return A CompletableFuture containing a Vibe instance for browser automation
     */
    public static CompletableFuture<Vibe> launch(
            boolean headless,
            Integer port,
            String executablePath) {

        return ClickerProcess.start(headless, port, executablePath)
                .thenCompose(process ->
                    BiDiClient.connect("ws://localhost:" + process.getPort())
                        .thenApply(client -> new Vibe(client, process))
                );
    }

    /**
     * Launch a new browser instance with default settings.
     *
     * @return A CompletableFuture containing a Vibe instance for browser automation
     */
    public static CompletableFuture<Vibe> launch() {
        return launch(false, null, null);
    }

    /**
     * Launch a new browser instance.
     *
     * @param headless Run browser in headless mode
     * @return A CompletableFuture containing a Vibe instance for browser automation
     */
    public static CompletableFuture<Vibe> launch(boolean headless) {
        return launch(headless, null, null);
    }

    /**
     * Launch a new browser instance.
     *
     * @param headless Run browser in headless mode
     * @param port     WebSocket port (null for auto-assigned)
     * @return A CompletableFuture containing a Vibe instance for browser automation
     */
    public static CompletableFuture<Vibe> launch(boolean headless, Integer port) {
        return launch(headless, port, null);
    }
}
