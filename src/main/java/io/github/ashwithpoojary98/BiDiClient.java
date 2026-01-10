package io.github.ashwithpoojary98;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket client for BiDi protocol.
 */
public class BiDiClient implements AutoCloseable {

    private static final Gson GSON = new Gson();

    private final WebSocketClient ws;
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, CompletableFuture<JsonObject>> pending = new ConcurrentHashMap<>();

    private BiDiClient(WebSocketClient ws) {
        this.ws = ws;
    }

    /**
     * Connect to a BiDi WebSocket server.
     *
     * @param url WebSocket URL (e.g., "ws://localhost:9515")
     * @return A CompletableFuture containing a connected BiDiClient instance
     */
    public static CompletableFuture<BiDiClient> connect(String url) {
        CompletableFuture<BiDiClient> future = new CompletableFuture<>();

        try {
            URI uri = URI.create(url);
            BiDiWebSocketClient wsClient = new BiDiWebSocketClient(uri, future);
            wsClient.connect();
        } catch (Exception e) {
            future.completeExceptionally(new RuntimeException("Failed to connect to " + url, e));
        }

        return future;
    }

    /**
     * Send a command and wait for the response.
     *
     * @param method The BiDi method name (e.g., "browsingContext.navigate")
     * @return A CompletableFuture containing the result
     */
    public CompletableFuture<JsonObject> send(String method) {
        return send(method, null);
    }

    /**
     * Send a command and wait for the response.
     *
     * @param method The BiDi method name (e.g., "browsingContext.navigate")
     * @param params Parameters for the command (can be null)
     * @return A CompletableFuture containing the result
     */
    public CompletableFuture<JsonObject> send(String method, Object params) {
        int msgId = nextId.getAndIncrement();

        JsonObject command = new JsonObject();
        command.addProperty("id", msgId);
        command.addProperty("method", method);

        if (params != null) {
            command.add("params", GSON.toJsonTree(params));
        } else {
            command.add("params", new JsonObject());
        }

        CompletableFuture<JsonObject> responseFuture = new CompletableFuture<>();
        pending.put(msgId, responseFuture);

        try {
            ws.send(GSON.toJson(command));
        } catch (Exception e) {
            pending.remove(msgId);
            responseFuture.completeExceptionally(e);
            return responseFuture;
        }

        return responseFuture.thenApply(response -> {
            pending.remove(msgId);

            // Check for error response
            if (response.has("type") && "error".equals(response.get("type").getAsString())) {
                JsonObject errorInfo = response.has("error")
                    ? response.getAsJsonObject("error")
                    : new JsonObject();
                String error = errorInfo.has("error")
                    ? errorInfo.get("error").getAsString()
                    : "unknown";
                String message = errorInfo.has("message")
                    ? errorInfo.get("message").getAsString()
                    : "Unknown error";
                throw new BiDiError(error, message);
            }

            return response.has("result") ? response.getAsJsonObject("result") : new JsonObject();
        });
    }

    /**
     * Check if the WebSocket connection is open.
     */
    public boolean isOpen() {
        return ws.isOpen();
    }

    /**
     * Close the WebSocket connection.
     */
    @Override
    public void close() {
        ws.close();
        // Cancel all pending futures
        for (CompletableFuture<JsonObject> future : pending.values()) {
            if (!future.isDone()) {
                future.completeExceptionally(new RuntimeException("Connection closed"));
            }
        }
        pending.clear();
    }

    /**
     * Close the WebSocket connection asynchronously.
     */
    public CompletableFuture<Void> closeAsync() {
        return CompletableFuture.runAsync(this::close);
    }

    /**
     * Internal WebSocket client implementation.
     */
    private static class BiDiWebSocketClient extends WebSocketClient {

        private final CompletableFuture<BiDiClient> connectionFuture;
        private BiDiClient client;

        BiDiWebSocketClient(URI serverUri, CompletableFuture<BiDiClient> connectionFuture) {
            super(serverUri);
            this.connectionFuture = connectionFuture;
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            client = new BiDiClient(this);
            connectionFuture.complete(client);
        }

        @Override
        public void onMessage(String message) {
            if (client == null) return;

            try {
                JsonObject data = GSON.fromJson(message, JsonObject.class);

                if (data.has("id")) {
                    int msgId = data.get("id").getAsInt();
                    CompletableFuture<JsonObject> future = client.pending.get(msgId);
                    if (future != null) {
                        future.complete(data);
                    }
                }
                // TODO: Handle events (messages without id) when implementing event subscriptions
            } catch (Exception e) {
                // Log parsing error
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            if (client != null) {
                // Cancel all pending futures on close
                for (CompletableFuture<JsonObject> future : client.pending.values()) {
                    if (!future.isDone()) {
                        future.completeExceptionally(
                            new RuntimeException("Connection closed: " + reason)
                        );
                    }
                }
                client.pending.clear();
            }

            if (!connectionFuture.isDone()) {
                connectionFuture.completeExceptionally(
                    new RuntimeException("Connection closed before completing: " + reason)
                );
            }
        }

        @Override
        public void onError(Exception ex) {
            if (!connectionFuture.isDone()) {
                connectionFuture.completeExceptionally(ex);
            }

            if (client != null) {
                for (CompletableFuture<JsonObject> future : client.pending.values()) {
                    if (!future.isDone()) {
                        future.completeExceptionally(ex);
                    }
                }
            }
        }
    }
}
