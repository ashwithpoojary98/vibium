package io.github.ashwithpoojary98.vibium.ui;

import io.github.ashwithpoojary98.vibium.Browser;
import io.github.ashwithpoojary98.vibium.Element;
import io.github.ashwithpoojary98.vibium.Vibe;
import io.github.ashwithpoojary98.vibium.options.LaunchOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Integration test for Vibium browser automation.
 *
 * <p>Note: Process cleanup is handled automatically by ProcessManager,
 * no manual cleanup required.
 */
class VibiumTest {

    private Vibe vibe;

    @Test
    void browserLaunchTest() throws IOException {
        LaunchOptions options = LaunchOptions.builder()
                .headless(false)
                .build();

        try {
            // Sync API - no .join() needed!
            vibe = new Browser().launch(options);
            vibe.go("https://asynccodinghub.in/");

            // Test CSS selector
            Element h1 = vibe.find("h1");
            System.out.println("Found element: " + h1.getText());

            // Test finding link element
            Element link = vibe.find("a");
            System.out.println("Found link with text: " + link.getText());

            // Take screenshot
            byte[] screenshot = vibe.screenshot();
            String filePath = System.getProperty("user.dir") + "/target/screenshot.png";
            Files.write(new File(filePath).toPath(), screenshot);
            Assertions.assertTrue(Files.exists(new File(filePath).toPath()));
        } finally {
            if (vibe != null) {
                vibe.quit();
            }
        }
    }

    @AfterEach
    void cleanup() {
        if (vibe != null) {
            try {
                vibe.quit();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
            vibe = null;
        }
    }

}
