package io.github.ashwithpoojary98.vibium;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Debug test to see actual protocol responses from clicker.
 * Run with: mvn test -Dtest=ProtocolDebugTest
 */
@Disabled("Manual debug test - enable when needed")
class ProtocolDebugTest {

    @Test
    void debugProtocol() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9555")) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected!");

                // Test browsingContext.getTree
                String cmd = "{\"id\":1,\"method\":\"browsingContext.getTree\",\"params\":{}}";
                System.out.println("Sending: " + cmd);
                send(cmd);
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Received: " + message);
                latch.countDown();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        };

        client.connectBlocking(5, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
        client.closeBlocking();
    }
}
