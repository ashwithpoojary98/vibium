package io.github.ashwithpoojary98.vibium;

import io.github.ashwithpoojary98.vibium.exception.ClickerNotFoundException;
import io.github.ashwithpoojary98.vibium.internal.Clicker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Command-line interface for Vibium.
 *
 * <p>Usage:
 * <pre>
 * java -jar vibium.jar install   # Download Chrome for Testing
 * java -jar vibium.jar version   # Show version
 * java -jar vibium.jar help      # Show help
 * </pre>
 */
public final class CLI {

    private static final String DEFAULT_VERSION = "unknown";
    private static final String VERSION = loadVersion();

    private CLI() {
        // Prevent instantiation
    }

    /**
     * Main entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int exitCode = run(args);
        System.exit(exitCode);
    }

    /**
     * Run the CLI with the given arguments.
     *
     * @param args command-line arguments
     * @return exit code (0 for success, non-zero for failure)
     */
    static int run(String[] args) {
        if (args == null || args.length == 0) {
            printUsage();
            return 1;
        }

        try {
            Command command = Command.from(args[0]);
            switch (command) {
                case INSTALL:
                    return installBrowser();
                case VERSION:
                    printVersion();
                    return 0;
                case HELP:
                    printUsage();
                    return 0;
                default:
                    return 1;
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println("Run 'vibium help' for usage.");
            return 1;
        }
    }

    private static void printUsage() {
        System.out.println("Usage: vibium <command>");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  install    Download Chrome for Testing");
        System.out.println("  version    Show version");
        System.out.println("  help       Show this help message");
    }

    private static void printVersion() {
        System.out.printf("vibium %s%n", VERSION);
    }

    private static int installBrowser() {
        try {
            String clicker = Clicker.findClicker();
            System.out.println("Installing Chrome for Testing...");
            Clicker.ensureBrowserInstalled(clicker);
            System.out.println("Done.");
            return 0;
        } catch (ClickerNotFoundException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Installation interrupted");
        } catch (IOException e) {
            System.err.printf("Error installing browser: %s%n", e.getMessage());
        }
        return 1;
    }

    private static String loadVersion() {
        // Try to load from properties file
        try (InputStream is = CLI.class.getResourceAsStream("/vibium.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String version = props.getProperty("version");
                if (version != null && !version.isEmpty()) {
                    return version;
                }
            }
        } catch (IOException e) {
            // Fall through to package info
        }

        // Try to load from package implementation version
        Package pkg = CLI.class.getPackage();
        if (pkg != null && pkg.getImplementationVersion() != null) {
            return pkg.getImplementationVersion();
        }

        return DEFAULT_VERSION;
    }

    /**
     * CLI commands.
     */
    enum Command {
        INSTALL,
        VERSION,
        HELP;

        /**
         * Parse a command from string.
         *
         * @param value the command string
         * @return the Command enum value
         * @throws IllegalArgumentException if the command is unknown
         */
        static Command from(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Command cannot be null");
            }
            switch (value.toLowerCase()) {
                case "install":
                    return INSTALL;
                case "version":
                case "-v":
                case "--version":
                    return VERSION;
                case "help":
                case "-h":
                case "--help":
                    return HELP;
                default:
                    throw new IllegalArgumentException("Unknown command: " + value);
            }
        }
    }
}
