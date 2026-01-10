package io.github.ashwithpoojary98;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages a clicker subprocess.
 */
public class ClickerProcess {

    private final Process process;
    private final int port;

    private ClickerProcess(Process process, int port) {
        this.process = process;
        this.port = port;
    }

    /**
     * Get the WebSocket port the clicker is listening on.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the underlying process.
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Check if the process is still running.
     */
    public boolean isAlive() {
        return process.isAlive();
    }

    /**
     * Start a clicker process.
     *
     * @param headless       Run browser in headless mode
     * @param port           WebSocket port (null for auto-assigned)
     * @param executablePath Path to clicker binary (null for auto-detect)
     * @return A CompletableFuture containing a ClickerProcess instance
     */
    public static CompletableFuture<ClickerProcess> start(
            boolean headless,
            Integer port,
            String executablePath) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                String binary = executablePath != null ? executablePath : Clicker.findClicker();

                // Ensure Chrome is installed (auto-download if needed)
                Clicker.ensureBrowserInstalled(binary);

                // Build command arguments
                List<String> args = new ArrayList<>();
                args.add(binary);
                args.add("serve");

                if (headless) {
                    args.add("--headless");
                }
                if (port != null) {
                    args.add("--port");
                    args.add(String.valueOf(port));
                }

                // Start the process
                ProcessBuilder pb = new ProcessBuilder(args);
                pb.redirectErrorStream(false);
                Process process = pb.start();

                // Read the port from stdout
                // Clicker prints "Listening on ws://localhost:PORT"
                int actualPort = port != null ? port : 9515;

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                if (line != null && line.contains("Listening on")) {
                    // Extract port from "Listening on ws://localhost:9515"
                    String[] parts = line.trim().split(":");
                    if (parts.length > 0) {
                        try {
                            actualPort = Integer.parseInt(parts[parts.length - 1]);
                        } catch (NumberFormatException e) {
                            // Keep default port
                        }
                    }
                }

                // Give it a moment to start
                Thread.sleep(100);

                // Check if process is still running
                if (!process.isAlive()) {
                    String stderr = new String(process.getErrorStream().readAllBytes());
                    throw new RuntimeException("Clicker failed to start: " + stderr);
                }

                return new ClickerProcess(process, actualPort);

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to start clicker process", e);
            }
        });
    }

    /**
     * Start a clicker process with default settings.
     */
    public static CompletableFuture<ClickerProcess> start() {
        return start(false, null, null);
    }

    /**
     * Start a clicker process.
     *
     * @param headless Run browser in headless mode
     */
    public static CompletableFuture<ClickerProcess> start(boolean headless) {
        return start(headless, null, null);
    }

    /**
     * Start a clicker process.
     *
     * @param headless Run browser in headless mode
     * @param port     WebSocket port (null for auto-assigned)
     */
    public static CompletableFuture<ClickerProcess> start(boolean headless, Integer port) {
        return start(headless, port, null);
    }

    /**
     * Stop the clicker process.
     */
    public void stop() {
        if (process.isAlive()) {
            process.destroy();
            try {
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Stop the clicker process and wait for termination.
     *
     * @return A CompletableFuture that completes when the process has stopped
     */
    public CompletableFuture<Void> stopAsync() {
        return CompletableFuture.runAsync(this::stop);
    }
}
