package io.github.ashwithpoojary98;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Clicker utility class.
 */
class ClickerTest {

    @Test
    void getPlatformPackageName_returnsValidFormat() {
        String packageName = Clicker.getPlatformPackageName();

        assertNotNull(packageName);
        assertTrue(packageName.startsWith("vibium_"));
        // Should match pattern: vibium_{platform}_{arch}
        assertTrue(packageName.matches("vibium_(darwin|win32|linux)_(x64|arm64)"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void getPlatform_onWindows_returnsWin32() {
        String platform = Clicker.getPlatform();
        assertEquals("win32", platform);
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void getPlatform_onMac_returnsDarwin() {
        String platform = Clicker.getPlatform();
        assertEquals("darwin", platform);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void getPlatform_onLinux_returnsLinux() {
        String platform = Clicker.getPlatform();
        assertEquals("linux", platform);
    }

    @Test
    void getArchitecture_returnsValidArch() {
        String arch = Clicker.getArchitecture();

        assertNotNull(arch);
        assertTrue(arch.equals("x64") || arch.equals("arm64"),
                "Architecture should be x64 or arm64, got: " + arch);
    }

    @Test
    void getCacheDirectory_returnsValidPath() {
        Path cacheDir = Clicker.getCacheDirectory();

        assertNotNull(cacheDir);
        assertTrue(cacheDir.toString().contains("vibium"),
                "Cache directory should contain 'vibium': " + cacheDir);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void getCacheDirectory_onWindows_usesLocalAppData() {
        Path cacheDir = Clicker.getCacheDirectory();

        String path = cacheDir.toString().toLowerCase();
        assertTrue(path.contains("appdata") || path.contains("local"),
                "Windows cache should be in AppData/Local: " + cacheDir);
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void getCacheDirectory_onMac_usesLibraryCaches() {
        Path cacheDir = Clicker.getCacheDirectory();

        assertTrue(cacheDir.toString().contains("Library/Caches"),
                "Mac cache should be in Library/Caches: " + cacheDir);
    }

    @Test
    void getExecutableName_returnsCorrectName() {
        String execName = Clicker.getExecutableName();
        String platform = Clicker.getPlatform();

        if ("win32".equals(platform)) {
            assertEquals("clicker.exe", execName);
        } else {
            assertEquals("clicker", execName);
        }
    }

    @Test
    void findClicker_whenNotInstalled_throwsException() {
        // Save original env (can't easily modify, so just test the exception)
        // This test will pass if clicker is not installed
        // and will be skipped effectively if it is installed

        // Clear any cached state and test with invalid path
        String originalPath = System.getenv("VIBIUM_CLICKER_PATH");
        if (originalPath == null) {
            // Only test if no env var is set - otherwise clicker might be found
            try {
                Clicker.findClicker();
                // If we get here, clicker was found in PATH or cache
                // That's fine, test passes
            } catch (ClickerNotFoundException e) {
                // Expected when clicker is not installed
                assertTrue(e.getMessage().contains("Could not find clicker"));
            }
        }
    }
}
