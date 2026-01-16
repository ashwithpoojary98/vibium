package io.github.ashwithpoojary98.vibium.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all Vibium browser processes for automatic cleanup.
 *
 * <p>Ensures all Chrome/ChromeDriver processes are properly terminated
 * when the JVM exits, even if the user forgets to call quit().
 *
 * <p><b>Note:</b> This is an internal class and may change without notice.
 */
public final class ProcessManager {

    private static final Logger log = LoggerFactory.getLogger(ProcessManager.class);

    /** All active clicker processes */
    private static final Set<ClickerProcess> activeProcesses = ConcurrentHashMap.newKeySet();

    /** Global shutdown hook registered flag */
    private static volatile boolean shutdownHookRegistered = false;

    private ProcessManager() {
        // Utility class
    }

    /**
     * Register a clicker process for automatic cleanup.
     *
     * @param process the process to register
     */
    public static synchronized void register(ClickerProcess process) {
        ensureShutdownHook();
        activeProcesses.add(process);
        log.debug("Registered process on port {} (total active: {})",
                process.getPort(), activeProcesses.size());
    }

    /**
     * Unregister a clicker process (called when it's stopped).
     *
     * @param process the process to unregister
     */
    public static void unregister(ClickerProcess process) {
        activeProcesses.remove(process);
        log.debug("Unregistered process on port {} (total active: {})",
                process.getPort(), activeProcesses.size());
    }

    /**
     * Get count of active processes.
     *
     * @return number of active processes
     */
    public static int getActiveCount() {
        return activeProcesses.size();
    }

    /**
     * Stop all active processes.
     * Called automatically on JVM shutdown, but can be called manually if needed.
     */
    public static void stopAll() {
        if (activeProcesses.isEmpty()) {
            return;
        }

        log.debug("Stopping all {} active processes", activeProcesses.size());
        for (ClickerProcess process : activeProcesses) {
            try {
                process.stopInternal();
            } catch (Exception e) {
                log.debug("Error stopping process on port {}: {}",
                        process.getPort(), e.getMessage());
            }
        }
        activeProcesses.clear();
    }

    /**
     * Ensure the global shutdown hook is registered.
     */
    private static void ensureShutdownHook() {
        if (shutdownHookRegistered) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.debug("Shutdown hook triggered - cleaning up {} processes", activeProcesses.size());
            stopAll();
        }, "vibium-global-shutdown"));

        shutdownHookRegistered = true;
        log.debug("Global shutdown hook registered");
    }
}
