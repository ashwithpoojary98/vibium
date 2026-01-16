package io.github.ashwithpoojary98.vibium.internal;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.ashwithpoojary98.vibium.exception.BiDiException;
import io.github.ashwithpoojary98.vibium.exception.ConnectionException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * BiDi / CDP WebSocket client.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Send commands to the browser</li>
 *   <li>Match responses by ID</li>
 *   <li>Dispatch browser events to subscribers</li>
 *   <li>Manage connection lifecycle</li>
 * </ul>
 *
 * <p>Thread-safe: All public methods can be called from any thread.
 *
 * <p><b>Note:</b> This is an internal class and may change without notice.
 */
public final class BiDiClient extends WebSocketClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(BiDiClient.class);
    private static final Duration DEFAULT_COMMAND_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(30);

    private final Gson gson = new Gson();
    private final AtomicLong commandIdGenerator = new AtomicLong(0);

    /** Shared scheduler for command timeouts - uses daemon threads */
    private final ScheduledExecutorService timeoutScheduler;

    /** id -> pending command future */
    private final ConcurrentHashMap<Long, CompletableFuture<JsonObject>> pendingCommands =
            new ConcurrentHashMap<>();

    /** id -> timeout task (for cancellation when response arrives) */
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> pendingTimeouts =
            new ConcurrentHashMap<>();

    /** eventName -> subscribers */
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Consumer<JsonObject>>> eventSubscribers =
            new ConcurrentHashMap<>();

    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    private volatile boolean connected = false;
    private volatile boolean closed = false;

    /**
     * Create a new BiDiClient for the given WebSocket URI.
     *
     * @param serverUri the WebSocket URI to connect to
     */
    public BiDiClient(URI serverUri) {
        super(serverUri);
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "vibium-timeout-scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Connect to a WebSocket URL and return the client.
     *
     * @param url the WebSocket URL (e.g., "ws://localhost:9222")
     * @return CompletableFuture containing the connected client
     */
    public static CompletableFuture<BiDiClient> connect(String url) {
        return connect(url, DEFAULT_CONNECTION_TIMEOUT);
    }

    /**
     * Connect to a WebSocket URL with a custom timeout.
     *
     * @param url     the WebSocket URL
     * @param timeout connection timeout
     * @return CompletableFuture containing the connected client
     */
    public static CompletableFuture<BiDiClient> connect(String url, Duration timeout) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BiDiClient client = new BiDiClient(new URI(url));
                client.connectBlocking(timeout);
                return client;
            } catch (Exception e) {
                throw new ConnectionException("Failed to connect to " + url, e);
            }
        });
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.debug("WebSocket connection opened");
        connected = true;
        connectionLatch.countDown();
    }

    @Override
    public void onMessage(String message) {
        log.trace("Received message: {}", message);
        JsonObject json = gson.fromJson(message, JsonObject.class);

        if (json.has("id")) {
            handleResponse(json);
        } else if (json.has("method")) {
            handleEvent(json);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.debug("WebSocket closed: code={}, reason={}, remote={}", code, reason, remote);
        closed = true;
        connected = false;

        ConnectionException error = new ConnectionException("WebSocket closed: " + reason);

        for (Map.Entry<Long, CompletableFuture<JsonObject>> entry : pendingCommands.entrySet()) {
            entry.getValue().completeExceptionally(error);
        }
        pendingCommands.clear();
        pendingTimeouts.clear();
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket error", ex);
        onClose(-1, ex.getMessage(), true);
    }

    /**
     * Connect and block until the connection is established.
     *
     * @param timeout maximum time to wait for connection
     * @throws InterruptedException if the thread is interrupted
     * @throws ConnectionException  if connection times out
     */
    public void connectBlocking(Duration timeout) throws InterruptedException {
        super.connect();
        if (!connectionLatch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            throw new ConnectionException("Timed out waiting for WebSocket connection");
        }
    }

    /**
     * Check if the client is connected.
     *
     * @return true if connected and not closed
     */
    public boolean isConnected() {
        return connected && !closed;
    }

    /**
     * Send a command to the browser with default timeout.
     *
     * @param method the BiDi method name
     * @param params the command parameters (may be null)
     * @return CompletableFuture containing the result
     */
    public CompletableFuture<JsonObject> sendCommand(String method, JsonObject params) {
        return sendCommand(method, params, DEFAULT_COMMAND_TIMEOUT);
    }

    /**
     * Send a command to the browser with custom timeout.
     *
     * @param method  the BiDi method name
     * @param params  the command parameters (may be null)
     * @param timeout command timeout
     * @return CompletableFuture containing the result
     */
    public CompletableFuture<JsonObject> sendCommand(
            String method,
            JsonObject params,
            Duration timeout
    ) {
        if (!connected || closed) {
            return CompletableFuture.failedFuture(
                    new ConnectionException("WebSocket is not connected")
            );
        }

        long id = commandIdGenerator.incrementAndGet();

        JsonObject command = new JsonObject();
        command.addProperty("id", id);
        command.addProperty("method", method);
        command.add("params", params != null ? params : new JsonObject());

        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        pendingCommands.put(id, future);

        // Schedule timeout using shared scheduler
        ScheduledFuture<?> timeoutTask = timeoutScheduler.schedule(() -> {
            CompletableFuture<JsonObject> f = pendingCommands.remove(id);
            pendingTimeouts.remove(id);
            if (f != null && !f.isDone()) {
                log.warn("Command timed out: {} (id={})", method, id);
                f.completeExceptionally(
                        new TimeoutException("Command timed out: " + method)
                );
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);
        pendingTimeouts.put(id, timeoutTask);

        log.debug("Sending command: {} (id={})", method, id);
        send(gson.toJson(command));
        return future;
    }

    private void handleResponse(JsonObject json) {
        long id = json.get("id").getAsLong();
        CompletableFuture<JsonObject> future = pendingCommands.remove(id);

        // Cancel the timeout task
        ScheduledFuture<?> timeoutTask = pendingTimeouts.remove(id);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }

        if (future == null) {
            log.debug("Received response for unknown command id={}", id);
            return;
        }

        if (json.has("error") && !json.get("error").isJsonNull()) {
            JsonElement errorElement = json.get("error");
            String message;
            int code = -1;

            if (errorElement.isJsonObject()) {
                JsonObject error = errorElement.getAsJsonObject();
                message = error.has("message")
                        ? error.get("message").getAsString()
                        : "Unknown error";
                code = error.has("code")
                        ? error.get("code").getAsInt()
                        : -1;
            } else {
                message = errorElement.getAsString();
            }

            log.debug("Command {} failed: {} (code={})", id, message, code);
            future.completeExceptionally(new BiDiException(code, message));
        } else {
            JsonElement resultElement = json.get("result");
            JsonObject result;

            if (resultElement == null || resultElement.isJsonNull()) {
                result = new JsonObject();
            } else if (resultElement.isJsonObject()) {
                result = resultElement.getAsJsonObject();
            } else {
                // Wrap primitive result in an object
                result = new JsonObject();
                result.add("value", resultElement);
            }

            log.debug("Command {} completed successfully", id);
            future.complete(result);
        }
    }

    private void handleEvent(JsonObject json) {
        String method = json.get("method").getAsString();
        JsonObject params = json.has("params")
                ? json.getAsJsonObject("params")
                : new JsonObject();

        List<Consumer<JsonObject>> handlers = eventSubscribers.get(method);
        if (handlers == null || handlers.isEmpty()) {
            log.trace("No handlers for event: {}", method);
            return;
        }

        log.debug("Dispatching event: {} to {} handlers", method, handlers.size());
        for (Consumer<JsonObject> handler : handlers) {
            CompletableFuture.runAsync(() -> {
                try {
                    handler.accept(params);
                } catch (Exception e) {
                    log.error("Error in event handler for {}", method, e);
                }
            });
        }
    }

    /**
     * Subscribe to a browser event.
     *
     * @param event   the event name (e.g., "page.loadEventFired")
     * @param handler the callback to invoke when the event occurs
     */
    public void on(String event, Consumer<JsonObject> handler) {
        eventSubscribers
                .computeIfAbsent(event, k -> new CopyOnWriteArrayList<>())
                .add(handler);
        log.debug("Subscribed to event: {}", event);
    }

    /**
     * Unsubscribe from a browser event.
     *
     * @param event   the event name
     * @param handler the callback to remove
     */
    public void off(String event, Consumer<JsonObject> handler) {
        List<Consumer<JsonObject>> handlers = eventSubscribers.get(event);
        if (handlers != null) {
            handlers.remove(handler);
            log.debug("Unsubscribed from event: {}", event);
        }
    }

    /**
     * Close the WebSocket connection and release resources.
     */
    @Override
    public void close() {
        if (!closed) {
            log.debug("Closing BiDiClient");
            closed = true;

            // Cancel all pending timeouts
            pendingTimeouts.values().forEach(task -> task.cancel(false));
            pendingTimeouts.clear();

            // Shutdown the timeout scheduler
            timeoutScheduler.shutdown();

            closeConnection(1000, "Client closed");
        }
    }
}
