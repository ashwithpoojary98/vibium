package io.github.ashwithpoojary98;

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
 * </pre>
 */
public class CLI {

    private static final String VERSION = loadVersion();

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String command = args[0];

        switch (command) {
            case "install" -> installBrowser();
            case "version" -> printVersion();
            case "help", "-h", "--help" -> printUsage();
            default -> {
                System.err.println("Unknown command: " + command);
                System.err.println("Run 'vibium help' for usage.");
                System.exit(1);
            }
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
        System.out.println("vibium " + VERSION);
    }

    private static void installBrowser() {
        String clicker;
        try {
            clicker = Clicker.findClicker();
        } catch (ClickerNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
            return;
        }

        System.out.println("Installing Chrome for Testing...");
        try {
            Clicker.ensureBrowserInstalled(clicker);
            System.out.println("Done.");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error installing browser: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String loadVersion() {
        try (InputStream is = CLI.class.getResourceAsStream("/vibium.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                return props.getProperty("version", "unknown");
            }
        } catch (IOException e) {
            // Ignore
        }

        // Fallback: try to get from package info
        Package pkg = CLI.class.getPackage();
        if (pkg != null && pkg.getImplementationVersion() != null) {
            return pkg.getImplementationVersion();
        }

        return "1.0-SNAPSHOT";
    }
}
