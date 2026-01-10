package io.github.ashwithpoojary98;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Main browser automation interface.
 *
 * <p>Provides methods to navigate, interact with elements, and take screenshots.
 *
 * <p>Use {@link Browser#launch()} to create a new Vibe instance.
 *
 * <pre>{@code
 * Vibe vibe = Browser.launch().join();
 * vibe.go("https://example.com").join();
 * Element button = vibe.find("button.submit").join();
 * button.click().join();
 * vibe.close();
 * }</pre>
 */
public class Vibe implements AutoCloseable {

    private static final int DEFAULT_TIMEOUT = 30000;

    private final BiDiClient client;
    private final ClickerProcess process;
    private String context;

    Vibe(BiDiClient client, ClickerProcess process) {
        this.client = client;
        this.process = process;
    }

    /**
     * Get the browsing context ID.
     */
    private CompletableFuture<String> getContext() {
        if (context != null) {
            return CompletableFuture.completedFuture(context);
        }

        return client.send("browsingContext.getTree")
                .thenApply(result -> {
                    JsonArray contexts = result.getAsJsonArray("contexts");
                    if (contexts == null || contexts.isEmpty()) {
                        throw new RuntimeException("No browsing context available");
                    }
                    context = contexts.get(0).getAsJsonObject().get("context").getAsString();
                    return context;
                });
    }

    /**
     * Navigate to a URL.
     *
     * @param url The URL to navigate to
     * @return A CompletableFuture that completes when navigation is done
     */
    public CompletableFuture<Void> go(String url) {
        return getContext().thenCompose(ctx ->
                client.send("browsingContext.navigate", Map.of(
                        "context", ctx,
                        "url", url,
                        "wait", "complete"
                ))
        ).thenApply(result -> null);
    }

    /**
     * Capture a screenshot of the viewport.
     *
     * @return A CompletableFuture containing PNG image data as bytes
     */
    public CompletableFuture<byte[]> screenshot() {
        return getContext().thenCompose(ctx ->
                client.send("browsingContext.captureScreenshot", Map.of(
                        "context", ctx
                ))
        ).thenApply(result -> {
            String data = result.get("data").getAsString();
            return Base64.getDecoder().decode(data);
        });
    }

    /**
     * Find an element by CSS selector.
     *
     * <p>Waits for the element to exist before returning.
     *
     * @param selector CSS selector
     * @return A CompletableFuture containing an Element instance
     */
    public CompletableFuture<Element> find(String selector) {
        return find(selector, DEFAULT_TIMEOUT);
    }

    /**
     * Find an element by CSS selector.
     *
     * <p>Waits for the element to exist before returning.
     *
     * @param selector CSS selector
     * @param timeout  Timeout in milliseconds
     * @return A CompletableFuture containing an Element instance
     */
    public CompletableFuture<Element> find(String selector, int timeout) {
        return getContext().thenCompose(ctx -> {
            Map<String, Object> params = new HashMap<>();
            params.put("context", ctx);
            params.put("selector", selector);
            params.put("timeout", timeout);

            return client.send("vibium:find", params)
                    .thenApply(result -> {
                        JsonObject boxData = result.getAsJsonObject("box");
                        BoundingBox box = new BoundingBox(
                                boxData.get("x").getAsDouble(),
                                boxData.get("y").getAsDouble(),
                                boxData.get("width").getAsDouble(),
                                boxData.get("height").getAsDouble()
                        );

                        ElementInfo info = new ElementInfo(
                                result.get("tag").getAsString(),
                                result.get("text").getAsString(),
                                box
                        );

                        return new Element(client, ctx, selector, info);
                    });
        });
    }

    /**
     * Get the WebSocket port the browser is listening on.
     */
    public int getPort() {
        return process.getPort();
    }

    /**
     * Get the underlying BiDi client for advanced operations.
     */
    public BiDiClient getClient() {
        return client;
    }

    /**
     * Check if the browser is still connected.
     */
    public boolean isConnected() {
        return client.isOpen() && process.isAlive();
    }

    /**
     * Send a BiDi command to the browser.
     *
     * @param method The BiDi method name (e.g., "browsingContext.navigate")
     * @return A CompletableFuture containing the result
     */
    public CompletableFuture<JsonObject> send(String method) {
        return client.send(method);
    }

    /**
     * Send a BiDi command to the browser with parameters.
     *
     * @param method The BiDi method name (e.g., "browsingContext.navigate")
     * @param params Parameters for the command
     * @return A CompletableFuture containing the result
     */
    public CompletableFuture<JsonObject> send(String method, Object params) {
        return client.send(method, params);
    }

    /**
     * Close the browser and release resources.
     */
    @Override
    public void close() {
        client.close();
        process.stop();
    }

    /**
     * Close the browser asynchronously.
     *
     * @return A CompletableFuture that completes when the browser has closed
     */
    public CompletableFuture<Void> closeAsync() {
        return client.closeAsync()
                .thenCompose(v -> process.stopAsync());
    }
}
