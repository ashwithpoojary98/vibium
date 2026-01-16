package io.github.ashwithpoojary98.vibium;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.ashwithpoojary98.vibium.exception.VibiumException;
import io.github.ashwithpoojary98.vibium.internal.BiDiClient;
import io.github.ashwithpoojary98.vibium.internal.ClickerProcess;
import io.github.ashwithpoojary98.vibium.model.Box;
import io.github.ashwithpoojary98.vibium.model.BrowsingContextTree;
import io.github.ashwithpoojary98.vibium.model.ElementInfo;
import io.github.ashwithpoojary98.vibium.options.FindOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

/**
 * Async browser automation interface using CompletableFuture.
 *
 * <p>For a simpler blocking API, use {@link Vibe} instead.
 *
 * <p>Example usage:
 * <pre>{@code
 * VibeAsync vibe = new BrowserAsync().launch().join();
 * vibe.go("https://example.com")
 *     .thenCompose(v -> vibe.find("button.submit"))
 *     .thenCompose(ElementAsync::click)
 *     .join();
 * vibe.quit();
 * }</pre>
 *
 * @see Vibe
 * @see BrowserAsync
 */
public final class VibeAsync implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(VibeAsync.class);
    private static final Duration DEFAULT_FIND_TIMEOUT = Duration.ofSeconds(30);

    private final BiDiClient client;
    private final ClickerProcess process;
    private final Gson gson = new Gson();

    private volatile String context;

    /**
     * Create a new VibeAsync instance.
     *
     * @param client  the BiDi client for browser communication
     * @param process the browser process (may be null if connecting to existing browser)
     */
    public VibeAsync(BiDiClient client, ClickerProcess process) {
        this.client = client;
        this.process = process;
    }

    /**
     * Create a new VibeAsync instance with a specific browsing context.
     *
     * @param client  the BiDi client
     * @param process the browser process
     * @param context the browsing context ID
     */
    public VibeAsync(BiDiClient client, ClickerProcess process, String context) {
        this.client = client;
        this.process = process;
        this.context = context;
    }

    /**
     * Get or detect the browsing context ID.
     */
    String getContext() {
        if (context != null) {
            return context;
        }

        log.debug("Fetching browsing context tree");
        JsonObject result = client.sendCommand("browsingContext.getTree", null).join();
        BrowsingContextTree tree = gson.fromJson(result, BrowsingContextTree.class);

        if (tree.getContexts() == null || tree.getContexts().isEmpty()) {
            throw new VibiumException("No browsing context available");
        }

        context = tree.getContexts().get(0).getContext();
        log.debug("Using browsing context: {}", context);
        return context;
    }

    /**
     * Navigate to a URL.
     *
     * @param url the URL to navigate to
     * @return CompletableFuture that completes when navigation is done
     */
    public CompletableFuture<Void> go(String url) {
        String contextId = getContext();
        log.debug("Navigating to: {}", url);

        JsonObject params = new JsonObject();
        params.addProperty("context", contextId);
        params.addProperty("url", url);
        params.addProperty("wait", "complete");

        return client.sendCommand("browsingContext.navigate", params)
                .thenApply(result -> null);
    }

    /**
     * Capture a screenshot of the viewport.
     *
     * @return CompletableFuture containing PNG image data as bytes
     */
    public CompletableFuture<byte[]> screenshot() {
        String contextId = getContext();
        log.debug("Capturing screenshot");

        JsonObject params = new JsonObject();
        params.addProperty("context", contextId);

        return client.sendCommand("browsingContext.captureScreenshot", params)
                .thenApply(result -> {
                    String data = result.get("data").getAsString();
                    return Base64.getDecoder().decode(data);
                });
    }

    /**
     * Find an element by CSS selector or XPath.
     *
     * <p>Waits for the element to exist before returning (default 30 second timeout).
     * Automatically detects if the selector is XPath (starts with "/" or "(") or CSS.
     *
     * @param selector CSS selector or XPath expression
     * @return CompletableFuture containing an ElementAsync instance
     */
    public CompletableFuture<ElementAsync> find(String selector) {
        return find(selector, DEFAULT_FIND_TIMEOUT);
    }

    /**
     * Find an element by CSS selector or XPath with custom timeout.
     *
     * @param selector CSS selector or XPath expression
     * @param timeout  maximum time to wait for the element
     * @return CompletableFuture containing an ElementAsync instance
     */
    public CompletableFuture<ElementAsync> find(String selector, Duration timeout) {
        String contextId = getContext();
        log.debug("Finding element: {} (timeout={}ms)", selector, timeout.toMillis());

        JsonObject params = new JsonObject();
        params.addProperty("context", contextId);
        params.addProperty("selector", selector);
        params.addProperty("timeout", timeout.toMillis());

        return client.sendCommand("vibium:find", params)
                .thenApply(result -> {
                    log.trace("vibium:find result: {}", result);

                    JsonObject boxData = result.getAsJsonObject("box");
                    Box box = new Box(
                            boxData.get("x").getAsDouble(),
                            boxData.get("y").getAsDouble(),
                            boxData.get("width").getAsDouble(),
                            boxData.get("height").getAsDouble()
                    );

                    ElementInfo info = ElementInfo.builder()
                            .tagName(result.get("tag").getAsString())
                            .textContent(result.get("text").getAsString())
                            .box(box)
                            .build();

                    return new ElementAsync(client, contextId, selector, info);
                });
    }

    /**
     * Find an element using FindOptions.
     *
     * @param selector CSS selector
     * @param options  find options
     * @return CompletableFuture containing an ElementAsync instance
     */
    public CompletableFuture<ElementAsync> find(String selector, FindOptions options) {
        return find(selector, options.getTimeout());
    }

    /**
     * Evaluate JavaScript in the page context.
     *
     * @param script the JavaScript code to execute
     * @param clazz  the expected return type
     * @param <T>    the return type
     * @return CompletableFuture containing the result
     */
    public <T> CompletableFuture<T> evaluate(String script, Class<T> clazz) {
        String contextId = getContext();
        log.debug("Evaluating script: {}", script.substring(0, Math.min(50, script.length())));

        JsonObject params = new JsonObject();
        params.addProperty("functionDeclaration", "() => { " + script + " }");

        JsonObject target = new JsonObject();
        target.addProperty("context", contextId);
        params.add("target", target);

        params.add("arguments", new JsonArray());
        params.addProperty("awaitPromise", true);
        params.addProperty("resultOwnership", "root");

        return client.sendCommand("script.callFunction", params)
                .thenApply(result -> {
                    JsonObject res = result
                            .getAsJsonObject("result")
                            .getAsJsonObject("result");
                    return gson.fromJson(res.get("value"), clazz);
                });
    }

    /**
     * Evaluate JavaScript in the page context.
     *
     * @param script the JavaScript code to execute
     * @return CompletableFuture containing the result as Object
     */
    public CompletableFuture<Object> evaluate(String script) {
        String contextId = getContext();

        JsonObject params = new JsonObject();
        params.addProperty("functionDeclaration", "() => { " + script + " }");

        JsonObject target = new JsonObject();
        target.addProperty("context", contextId);
        params.add("target", target);

        params.add("arguments", new JsonArray());
        params.addProperty("awaitPromise", true);

        return client.sendCommand("script.callFunction", params)
                .thenApply(result -> result
                        .getAsJsonObject("result")
                        .getAsJsonObject("result")
                        .get("value"));
    }

    /**
     * Get the underlying BiDi client for advanced operations.
     *
     * @return the BiDiClient instance
     */
    public BiDiClient getClient() {
        return client;
    }

    /**
     * Get the underlying process (if managed).
     *
     * @return the ClickerProcess or null
     */
    ClickerProcess getProcess() {
        return process;
    }

    /**
     * Check if the browser is still connected.
     *
     * @return true if connected
     */
    public boolean isConnected() {
        return client.isConnected() && (process == null || process.isAlive());
    }

    /**
     * Close the browser and release resources.
     */
    public void quit() {
        log.debug("Quitting browser");

        // Send browser.close command to properly terminate the browser
        if (client.isConnected()) {
            try {
                client.sendCommand("browser.close", null).join();
                log.debug("Browser close command sent successfully");
            } catch (Exception e) {
                log.debug("Error sending browser.close command: {}", e.getMessage());
            }
        }

        client.close();
        if (process != null) {
            process.stop();
        }
    }

    /**
     * Close the browser (same as {@link #quit()}).
     */
    @Override
    public void close() {
        quit();
    }
}
