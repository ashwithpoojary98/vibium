package io.github.ashwithpoojary98;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a DOM element that can be interacted with.
 */
public class Element {

    private static final int DEFAULT_TIMEOUT = 30000;

    private final BiDiClient client;
    private final String context;
    private final String selector;
    private final ElementInfo info;

    Element(BiDiClient client, String context, String selector, ElementInfo info) {
        this.client = client;
        this.context = context;
        this.selector = selector;
        this.info = info;
    }

    /**
     * Get the element info (cached from find).
     */
    public ElementInfo getInfo() {
        return info;
    }

    /**
     * Get the HTML tag name.
     */
    public String getTag() {
        return info.tag();
    }

    /**
     * Get the bounding box.
     */
    public BoundingBox getBox() {
        return info.box();
    }

    /**
     * Click the element.
     *
     * <p>Waits for element to be visible, stable, receive events, and enabled.
     *
     * @return A CompletableFuture that completes when the click is done
     */
    public CompletableFuture<Void> click() {
        return click(DEFAULT_TIMEOUT);
    }

    /**
     * Click the element.
     *
     * <p>Waits for element to be visible, stable, receive events, and enabled.
     *
     * @param timeout Timeout in milliseconds
     * @return A CompletableFuture that completes when the click is done
     */
    public CompletableFuture<Void> click(int timeout) {
        Map<String, Object> params = new HashMap<>();
        params.put("context", context);
        params.put("selector", selector);
        params.put("timeout", timeout);

        return client.send("vibium:click", params).thenApply(result -> null);
    }

    /**
     * Type text into the element.
     *
     * <p>Waits for element to be visible, stable, receive events, enabled, and editable.
     *
     * @param text The text to type
     * @return A CompletableFuture that completes when typing is done
     */
    public CompletableFuture<Void> type(String text) {
        return type(text, DEFAULT_TIMEOUT);
    }

    /**
     * Type text into the element.
     *
     * <p>Waits for element to be visible, stable, receive events, enabled, and editable.
     *
     * @param text    The text to type
     * @param timeout Timeout in milliseconds
     * @return A CompletableFuture that completes when typing is done
     */
    public CompletableFuture<Void> type(String text, int timeout) {
        Map<String, Object> params = new HashMap<>();
        params.put("context", context);
        params.put("selector", selector);
        params.put("text", text);
        params.put("timeout", timeout);

        return client.send("vibium:type", params).thenApply(result -> null);
    }

    /**
     * Clear the element's value.
     *
     * @return A CompletableFuture that completes when clearing is done
     */
    public CompletableFuture<Void> clear() {
        return client.send("vibium:clear", Map.of(
                "context", context,
                "selector", selector
        )).thenApply(result -> null);
    }

    /**
     * Fill the element with text (clears first, then types).
     *
     * @param text The text to fill
     * @return A CompletableFuture that completes when filling is done
     */
    public CompletableFuture<Void> fill(String text) {
        return clear().thenCompose(v -> type(text));
    }

    /**
     * Get the text content of the element.
     *
     * @return A CompletableFuture containing the trimmed text content
     */
    public CompletableFuture<String> text() {
        String functionDeclaration = """
                (selector) => {
                    const el = document.querySelector(selector);
                    return el ? (el.textContent || '').trim() : null;
                }""";

        Map<String, Object> params = Map.of(
                "functionDeclaration", functionDeclaration,
                "target", Map.of("context", context),
                "arguments", List.of(Map.of("type", "string", "value", selector)),
                "awaitPromise", false,
                "resultOwnership", "root"
        );

        return client.send("script.callFunction", params).thenApply(result -> {
            JsonObject resultObj = result.getAsJsonObject("result");
            if (resultObj != null && "null".equals(resultObj.get("type").getAsString())) {
                throw new RuntimeException("Element not found: " + selector);
            }
            return resultObj != null && resultObj.has("value")
                    ? resultObj.get("value").getAsString()
                    : "";
        });
    }

    /**
     * Get an attribute value from the element.
     *
     * @param name The attribute name
     * @return A CompletableFuture containing the attribute value, or null if not present
     */
    public CompletableFuture<String> getAttribute(String name) {
        String functionDeclaration = """
                (selector, attrName) => {
                    const el = document.querySelector(selector);
                    return el ? el.getAttribute(attrName) : null;
                }""";

        Map<String, Object> params = Map.of(
                "functionDeclaration", functionDeclaration,
                "target", Map.of("context", context),
                "arguments", List.of(
                        Map.of("type", "string", "value", selector),
                        Map.of("type", "string", "value", name)
                ),
                "awaitPromise", false,
                "resultOwnership", "root"
        );

        return client.send("script.callFunction", params).thenApply(result -> {
            JsonObject resultObj = result.getAsJsonObject("result");
            if (resultObj == null || "null".equals(resultObj.get("type").getAsString())) {
                return null;
            }
            return resultObj.has("value") ? resultObj.get("value").getAsString() : null;
        });
    }

    /**
     * Check if the element is visible.
     *
     * @return A CompletableFuture containing true if visible
     */
    public CompletableFuture<Boolean> isVisible() {
        String functionDeclaration = """
                (selector) => {
                    const el = document.querySelector(selector);
                    if (!el) return false;
                    const style = window.getComputedStyle(el);
                    return style.display !== 'none' &&
                           style.visibility !== 'hidden' &&
                           style.opacity !== '0';
                }""";

        Map<String, Object> params = Map.of(
                "functionDeclaration", functionDeclaration,
                "target", Map.of("context", context),
                "arguments", List.of(Map.of("type", "string", "value", selector)),
                "awaitPromise", false,
                "resultOwnership", "root"
        );

        return client.send("script.callFunction", params).thenApply(result -> {
            JsonObject resultObj = result.getAsJsonObject("result");
            return resultObj != null &&
                   "boolean".equals(resultObj.get("type").getAsString()) &&
                   resultObj.get("value").getAsBoolean();
        });
    }

    /**
     * Check if the element is enabled.
     *
     * @return A CompletableFuture containing true if enabled
     */
    public CompletableFuture<Boolean> isEnabled() {
        String functionDeclaration = """
                (selector) => {
                    const el = document.querySelector(selector);
                    return el ? !el.disabled : false;
                }""";

        Map<String, Object> params = Map.of(
                "functionDeclaration", functionDeclaration,
                "target", Map.of("context", context),
                "arguments", List.of(Map.of("type", "string", "value", selector)),
                "awaitPromise", false,
                "resultOwnership", "root"
        );

        return client.send("script.callFunction", params).thenApply(result -> {
            JsonObject resultObj = result.getAsJsonObject("result");
            return resultObj != null &&
                   "boolean".equals(resultObj.get("type").getAsString()) &&
                   resultObj.get("value").getAsBoolean();
        });
    }

    /**
     * Hover over the element.
     *
     * @return A CompletableFuture that completes when hovering is done
     */
    public CompletableFuture<Void> hover() {
        return client.send("vibium:hover", Map.of(
                "context", context,
                "selector", selector
        )).thenApply(result -> null);
    }

    /**
     * Focus the element.
     *
     * @return A CompletableFuture that completes when focusing is done
     */
    public CompletableFuture<Void> focus() {
        return client.send("vibium:focus", Map.of(
                "context", context,
                "selector", selector
        )).thenApply(result -> null);
    }

    /**
     * Scroll the element into view.
     *
     * @return A CompletableFuture that completes when scrolling is done
     */
    public CompletableFuture<Void> scrollIntoView() {
        return client.send("vibium:scrollIntoView", Map.of(
                "context", context,
                "selector", selector
        )).thenApply(result -> null);
    }

    @Override
    public String toString() {
        return String.format("Element<%s selector=\"%s\">", info.tag(), selector);
    }
}
