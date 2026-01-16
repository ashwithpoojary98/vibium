package io.github.ashwithpoojary98.vibium;

import com.google.gson.JsonObject;
import io.github.ashwithpoojary98.vibium.internal.BiDiClient;
import io.github.ashwithpoojary98.vibium.model.Box;
import io.github.ashwithpoojary98.vibium.model.ElementInfo;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * Async representation of a DOM element in the browser.
 *
 * <p>For a simpler blocking API, use {@link Element} instead.
 *
 * <p>Provides async methods to interact with the element such as clicking,
 * typing, and retrieving properties.
 *
 * @see Element
 */
@Getter
public final class ElementAsync {

    private final BiDiClient client;
    private final String contextId;
    private final String selector;
    private final ElementInfo info;

    public ElementAsync(BiDiClient client, String contextId, String selector, ElementInfo info) {
        this.client = client;
        this.contextId = contextId;
        this.selector = selector;
        this.info = info;
    }

    /**
     * Get the element's tag name.
     *
     * @return the tag name (e.g., "button", "div")
     */
    public String getTagName() {
        return info.getTagName();
    }

    /**
     * Get the element's text content.
     *
     * @return the text content
     */
    public String getText() {
        return info.getTextContent();
    }

    /**
     * Get the element's bounding box.
     *
     * @return the bounding box
     */
    public Box getBox() {
        return info.getBox();
    }

    /**
     * Click the element at its center point.
     *
     * @return CompletableFuture that completes when the click is done
     */
    public CompletableFuture<Void> click() {
        JsonObject params = new JsonObject();
        params.addProperty("context", contextId);
        params.addProperty("selector", selector);
        params.addProperty("timeout", 5000);

        return client.sendCommand("vibium:click", params)
                .thenApply(result -> null);
    }

    /**
     * Type text into the element.
     *
     * @param text the text to type
     * @return CompletableFuture that completes when typing is done
     */
    public CompletableFuture<Void> type(String text) {
        JsonObject params = new JsonObject();
        params.addProperty("context", contextId);
        params.addProperty("selector", selector);
        params.addProperty("text", text);

        return client.sendCommand("vibium:type", params)
                .thenApply(result -> null);
    }

    /**
     * Clear the element's value (for input fields).
     *
     * @return CompletableFuture that completes when clearing is done
     */
    public CompletableFuture<Void> clear() {
        JsonObject params = new JsonObject();
        params.addProperty("context", contextId);
        params.addProperty("selector", selector);

        return client.sendCommand("vibium:clear", params)
                .thenApply(result -> null);
    }

    /**
     * Get an attribute value from the element.
     *
     * @param name the attribute name
     * @return CompletableFuture containing the attribute value or null
     */
    public CompletableFuture<String> getAttribute(String name) {
        JsonObject params = new JsonObject();
        params.addProperty("context", contextId);
        params.addProperty("selector", selector);
        params.addProperty("attribute", name);

        return client.sendCommand("vibium:getAttribute", params)
                .thenApply(result -> {
                    if (result.has("value") && !result.get("value").isJsonNull()) {
                        return result.get("value").getAsString();
                    }
                    return null;
                });
    }

    /**
     * Check if the element is visible.
     *
     * @return CompletableFuture containing true if visible
     */
    public CompletableFuture<Boolean> isVisible() {
        Box box = info.getBox();
        return CompletableFuture.completedFuture(
                box.getWidth() > 0 && box.getHeight() > 0
        );
    }

    @Override
    public String toString() {
        return String.format("ElementAsync[%s selector='%s']", info.getTagName(), selector);
    }
}
