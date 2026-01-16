package io.github.ashwithpoojary98.vibium.internal;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages the Clicker browser process lifecycle.
 *
 * <p>All processes are automatically tracked and cleaned up on JVM shutdown
 * via {@link ProcessManager}, so manual cleanup is not required.
 *
 * <p><b>Note:</b> This is an internal class and may change without notice.
 */
@Getter
public final class ClickerProcess implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ClickerProcess.class);
    private static final Pattern PORT_PATTERN =
            Pattern.compile("listening on ws://localhost:(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(10);

    private final Process process;
    private final int port;
    private volatile boolean stopped = false;

    private ClickerProcess(Process process, int port) {
        this.process = process;
        this.port = port;

        // Register with ProcessManager for automatic cleanup
        ProcessManager.register(this);
    }

    /**
     * Whether the process is alive.
     *
     * @return true if the process is running
     */
    public boolean isAlive() {
        return process.isAlive();
    }

    /**
     * Start Clicker asynchronously.
     *
     * @param headless       whether to run in headless mode
     * @param port           the port to use (null for automatic)
     * @param executablePath path to the clicker executable (null for auto-detect)
     * @return CompletableFuture containing the started process
     */
    public static CompletableFuture<ClickerProcess> start(
            boolean headless,
            Integer port,
            String executablePath
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Starting Clicker (headless={}, port={})", headless, port);

                String binary = executablePath != null
                        ? executablePath
                        : Clicker.findClicker();
                log.info("Using clicker binary: {}", binary);

                Clicker.ensureBrowserInstalled(binary);

                log.debug("Launching clicker process...");
                Process process = createProcess(headless, port, binary);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );

                long deadline = System.currentTimeMillis() + STARTUP_TIMEOUT.toMillis();

                String line;
                Integer detectedPort = null;

                while ((line = reader.readLine()) != null) {
                    log.trace("Clicker output: {}", line);
                    Matcher matcher = PORT_PATTERN.matcher(line);
                    if (matcher.find()) {
                        detectedPort = Integer.parseInt(matcher.group(1));
                        break;
                    }

                    if (!process.isAlive()) {
                        int exitCode = process.exitValue();
                        log.error("Clicker exited with code {} before startup completed", exitCode);
                        throw new RuntimeException("Clicker exited with code " + exitCode + " before startup completed");
                    }

                    if (System.currentTimeMillis() > deadline) {
                        process.destroyForcibly();
                        log.error("Timed out waiting for Clicker to start");
                        throw new RuntimeException("Timed out waiting for Clicker to start");
                    }
                }

                if (detectedPort == null) {
                    process.destroyForcibly();
                    log.error("Failed to detect Clicker listening port");
                    throw new RuntimeException("Failed to detect Clicker listening port");
                }

                log.info("Clicker started on port {}", detectedPort);
                return new ClickerProcess(process, detectedPort);

            } catch (Exception e) {
                String message = "Failed to start Clicker: " + e.getMessage();
                if (e.getCause() != null) {
                    message += " (caused by: " + e.getCause().getMessage() + ")";
                }
                log.error(message, e);
                throw new CompletionException(new RuntimeException(message, e));
            }
        });
    }

    private static Process createProcess(boolean headless, Integer port, String binary) throws IOException {
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

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true); // avoid stderr deadlocks

        return pb.start();
    }

    /**
     * Start with defaults.
     *
     * @return CompletableFuture containing the started process
     */
    public static CompletableFuture<ClickerProcess> start() {
        return start(false, null, null);
    }

    /**
     * Start with headless option.
     *
     * @param headless whether to run in headless mode
     * @return CompletableFuture containing the started process
     */
    public static CompletableFuture<ClickerProcess> start(boolean headless) {
        return start(headless, null, null);
    }

    /**
     * Start with headless and port options.
     *
     * @param headless whether to run in headless mode
     * @param port     the port to use
     * @return CompletableFuture containing the started process
     */
    public static CompletableFuture<ClickerProcess> start(boolean headless, Integer port) {
        return start(headless, port, null);
    }

    /**
     * Gracefully stop the Clicker process.
     */
    public void stop() {
        if (stopped) {
            return;
        }
        stopped = true;

        // Unregister from ProcessManager
        ProcessManager.unregister(this);

        stopInternal();
    }

    /**
     * Internal stop method called by ProcessManager during shutdown.
     * Does not unregister from ProcessManager to avoid ConcurrentModificationException.
     */
    void stopInternal() {
        if (!process.isAlive()) {
            return;
        }

        log.debug("Stopping clicker process on port {}", port);

        // First, kill all descendant processes (ChromeDriver, Chrome)
        // This is necessary on Windows where process.destroy() doesn't kill children
        try {
            ProcessHandle processHandle = process.toHandle();
            processHandle.descendants().forEach(ph -> {
                log.debug("Killing descendant process: {} ({})", ph.pid(),
                        ph.info().command().orElse("unknown"));
                ph.destroyForcibly();
            });
        } catch (Exception e) {
            log.debug("Error killing descendant processes: {}", e.getMessage());
        }

        // Now stop the clicker process itself
        process.destroy();

        try {
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                log.debug("Force killing clicker process");
                process.destroyForcibly();
            }
        } catch (InterruptedException e) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stop asynchronously.
     *
     * @return CompletableFuture that completes when stopped
     */
    public CompletableFuture<Void> stopAsync() {
        return CompletableFuture.runAsync(this::stop);
    }

    /**
     * AutoCloseable support - calls {@link #stop()}.
     */
    @Override
    public void close() {
        stop();
    }
}
