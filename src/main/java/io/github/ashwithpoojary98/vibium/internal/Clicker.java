package io.github.ashwithpoojary98.vibium.internal;

import io.github.ashwithpoojary98.vibium.exception.ClickerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

/**
 * Clicker binary management - finding and browser installation.
 *
 * <p><b>Note:</b> This is an internal class and may change without notice.
 */
public final class Clicker {

    private static final Logger log = LoggerFactory.getLogger(Clicker.class);

    private Clicker() {
        // Utility class
    }

    /**
     * Get the current operating system type.
     */
    static String getPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac") || os.contains("darwin")) {
            return "darwin";
        } else if (os.contains("win")) {
            return "win32";
        } else {
            return "linux";
        }
    }

    /**
     * Get the current architecture.
     */
    static String getArchitecture() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.contains("amd64") || arch.contains("x86_64")) {
            return "x64";
        } else if (arch.contains("aarch64") || arch.contains("arm64")) {
            return "arm64";
        } else {
            return "x64"; // Default fallback
        }
    }

    /**
     * Get the platform-specific package name.
     */
    public static String getPlatformPackageName() {
        return String.format("vibium_%s_%s", getPlatform(), getArchitecture());
    }

    /**
     * Get the platform-specific cache directory.
     */
    public static Path getCacheDirectory() {
        String platform = getPlatform();
        String userHome = System.getProperty("user.home");

        switch (platform) {
            case "darwin":
                return Paths.get(userHome, "Library", "Caches", "vibium");
            case "win32":
                String localAppData = System.getenv("LOCALAPPDATA");
                if (localAppData == null || localAppData.isEmpty()) {
                    localAppData = Paths.get(userHome, "AppData", "Local").toString();
                }
                return Paths.get(localAppData, "vibium");
            default:
                String xdgCache = System.getenv("XDG_CACHE_HOME");
                if (xdgCache == null || xdgCache.isEmpty()) {
                    xdgCache = Paths.get(userHome, ".cache").toString();
                }
                return Paths.get(xdgCache, "vibium");
        }
    }

    /**
     * Get the executable name for the current platform.
     */
    static String getExecutableName() {
        return getPlatform().equals("win32") ? "clicker.exe" : "clicker";
    }

    /**
     * Find the clicker binary.
     *
     * <p>Search order:
     * <ol>
     *   <li>VIBIUM_CLICKER_PATH environment variable</li>
     *   <li>System PATH</li>
     *   <li>Platform cache directory</li>
     *   <li>Bundled binary (extracted to platform cache directory)</li>
     * </ol>
     *
     * @return Path to the clicker binary
     * @throws ClickerNotFoundException if the binary cannot be found
     */
    public static String findClicker() {
        String executableName = getExecutableName();
        log.debug("Looking for clicker binary: {}", executableName);

        // 1. Check environment variable
        String envPath = System.getenv("VIBIUM_CLICKER_PATH");
        if (envPath != null && Files.isRegularFile(Paths.get(envPath))) {
            log.debug("Found clicker via VIBIUM_CLICKER_PATH: {}", envPath);
            return envPath;
        }

        // 2. Check PATH
        String pathBinary = findInPath(executableName);
        if (pathBinary != null) {
            log.debug("Found clicker in PATH: {}", pathBinary);
            return pathBinary;
        }

        // 3. Check cache directory
        Path cacheBinary = getCacheDirectory().resolve(executableName);
        if (Files.isRegularFile(cacheBinary)) {
            log.debug("Found clicker in cache: {}", cacheBinary);
            return cacheBinary.toString();
        }

        // 4. Extract bundled binary
        log.debug("Extracting bundled clicker binary...");
        Path dumpedBinary = dumpClickerToLocalMachine();
        if (Files.isRegularFile(dumpedBinary)) {
            log.info("Extracted clicker to: {}", dumpedBinary);
            return dumpedBinary.toString();
        }

        // Fallback: not found
        throw new ClickerNotFoundException(
                "Could not find clicker binary. Install options:\n" +
                        "  1. Set VIBIUM_CLICKER_PATH environment variable to the binary path\n" +
                        "  2. Add clicker to your system PATH\n" +
                        "  3. Place clicker binary in: " + getCacheDirectory() + "\n" +
                        "Download from: https://github.com/VibiumDev/vibium/releases"
        );
    }

    /**
     * Find an executable in the system PATH.
     */
    private static String findInPath(String executable) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) {
            return null;
        }

        String pathSeparator = File.pathSeparator;
        for (String dir : pathEnv.split(pathSeparator)) {
            Path candidate = Paths.get(dir, executable);
            if (Files.isExecutable(candidate)) {
                return candidate.toString();
            }
        }
        return null;
    }

    /**
     * Ensure Chrome for Testing is installed.
     * Runs 'clicker install' if Chrome is not found.
     *
     * @param clickerPath path to the clicker executable
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void ensureBrowserInstalled(String clickerPath) throws IOException, InterruptedException {
        // Check if Chrome is installed by running 'clicker paths'
        if (isChromeInstalled(clickerPath)) {
            return;
        }

        // Chrome not found, run install
        System.out.println("Downloading Chrome for Testing...");
        System.out.flush();

        ProcessBuilder pb = new ProcessBuilder(clickerPath, "install");
        pb.inheritIO();
        Process installProcess = pb.start();

        boolean completed = installProcess.waitFor(5, TimeUnit.MINUTES);
        if (!completed) {
            installProcess.destroyForcibly();
            throw new RuntimeException("Chrome installation timed out");
        }

        if (installProcess.exitValue() != 0) {
            throw new RuntimeException("Failed to install Chrome: exit code " + installProcess.exitValue());
        }

        System.out.println("Chrome installed successfully.");
    }

    /**
     * Check if Chrome for Testing is already installed.
     */
    private static boolean isChromeInstalled(String clickerPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(clickerPath, "paths");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean completed = process.waitFor(10, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                return false;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Chrome:")) {
                        String chromePath = line.substring(line.indexOf(":") + 1).trim();
                        return Files.isRegularFile(Paths.get(chromePath));
                    }
                }
            }
        } catch (Exception e) {
            // Ignore and return false
        }
        return false;
    }

    private static String detectClickerResource() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (os.contains("win")) {
            return "clicker/clicker-windows-amd64.exe";
        }

        if (os.contains("mac") || os.contains("darwin")) {
            return arch.contains("aarch64") || arch.contains("arm")
                    ? "clicker/clicker-darwin-arm64"
                    : "clicker/clicker-darwin-amd64";
        }

        if (os.contains("linux")) {
            return arch.contains("aarch64") || arch.contains("arm")
                    ? "clicker/clicker-linux-arm64"
                    : "clicker/clicker-linux-amd64";
        }

        throw new UnsupportedOperationException("Unsupported OS/arch: " + os + " / " + arch);
    }

    private static Path dumpClickerToLocalMachine() {
        try {
            String resource = detectClickerResource();
            log.debug("Detected clicker resource: {}", resource);

            Path targetDir = getCacheDirectory();

            Files.createDirectories(targetDir);
            log.debug("Target directory: {}", targetDir);

            Path targetBinary = targetDir.resolve(
                    resource.contains("windows") ? "clicker.exe" : "clicker"
            );

            if (Files.exists(targetBinary)) {
                log.debug("Clicker binary already exists: {}", targetBinary);
                return targetBinary;
            }

            log.debug("Extracting clicker from resources to: {}", targetBinary);
            try (InputStream in = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(resource)) {

                if (in == null) {
                    log.error("Resource not found in classpath: {}", resource);
                    throw new RuntimeException("Missing resource: " + resource);
                }

                Files.copy(in, targetBinary, StandardCopyOption.REPLACE_EXISTING);
                boolean execSet = targetBinary.toFile().setExecutable(true);
                log.debug("Set executable permission: {}", execSet);
            }

            return targetBinary;

        } catch (IOException e) {
            log.error("Failed to extract clicker binary", e);
            throw new RuntimeException("Failed to extract clicker binary", e);
        }
    }
}
