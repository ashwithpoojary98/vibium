package io.github.ashwithpoojary98.vibium.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Clicker}.
 */
class ClickerTest {

    @Test
    void getPlatform_returnsValidPlatform() {
        String platform = Clicker.getPlatform();

        assertTrue(
                platform.equals("darwin") ||
                        platform.equals("win32") ||
                        platform.equals("linux"),
                "Platform should be darwin, win32, or linux but was: " + platform
        );
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void getPlatform_onWindows_returnsWin32() {
        assertEquals("win32", Clicker.getPlatform());
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void getPlatform_onMac_returnsDarwin() {
        assertEquals("darwin", Clicker.getPlatform());
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void getPlatform_onLinux_returnsLinux() {
        assertEquals("linux", Clicker.getPlatform());
    }

    @Test
    void getArchitecture_returnsValidArchitecture() {
        String arch = Clicker.getArchitecture();

        assertTrue(
                arch.equals("x64") || arch.equals("arm64"),
                "Architecture should be x64 or arm64 but was: " + arch
        );
    }

    @Test
    void getPlatformPackageName_containsPlatformAndArch() {
        String packageName = Clicker.getPlatformPackageName();

        assertTrue(packageName.startsWith("vibium_"));
        assertTrue(
                packageName.contains("darwin") ||
                        packageName.contains("win32") ||
                        packageName.contains("linux")
        );
        assertTrue(packageName.contains("x64") || packageName.contains("arm64"));
    }

    @Test
    void getCacheDirectory_returnsNonNullPath() {
        Path cacheDir = Clicker.getCacheDirectory();

        assertNotNull(cacheDir);
        assertTrue(cacheDir.toString().contains("vibium"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void getCacheDirectory_onWindows_usesLocalAppData() {
        Path cacheDir = Clicker.getCacheDirectory();

        assertTrue(
                cacheDir.toString().contains("AppData") ||
                        cacheDir.toString().contains("Local"),
                "Windows cache should be in AppData: " + cacheDir
        );
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void getCacheDirectory_onMac_usesLibraryCaches() {
        Path cacheDir = Clicker.getCacheDirectory();

        assertTrue(cacheDir.toString().contains("Library/Caches"));
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void getCacheDirectory_onLinux_usesCache() {
        Path cacheDir = Clicker.getCacheDirectory();

        assertTrue(cacheDir.toString().contains(".cache") ||
                System.getenv("XDG_CACHE_HOME") != null);
    }

    @Test
    void getExecutableName_returnsCorrectName() {
        String execName = Clicker.getExecutableName();

        if (Clicker.getPlatform().equals("win32")) {
            assertEquals("clicker.exe", execName);
        } else {
            assertEquals("clicker", execName);
        }
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void getExecutableName_onWindows_hasExeExtension() {
        assertEquals("clicker.exe", Clicker.getExecutableName());
    }

    @Test
    @EnabledOnOs({OS.MAC, OS.LINUX})
    void getExecutableName_onUnix_hasNoExtension() {
        assertEquals("clicker", Clicker.getExecutableName());
    }

    @Test
    void getPlatformPackageName_formatIsCorrect() {
        String packageName = Clicker.getPlatformPackageName();

        // Should be in format: vibium_<platform>_<arch>
        String[] parts = packageName.split("_");
        assertEquals(3, parts.length);
        assertEquals("vibium", parts[0]);
    }
}
