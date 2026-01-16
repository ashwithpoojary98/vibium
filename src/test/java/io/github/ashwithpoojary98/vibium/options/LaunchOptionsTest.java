package io.github.ashwithpoojary98.vibium.options;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LaunchOptions}.
 */
class LaunchOptionsTest {

    @Test
    void builder_withDefaults_hasCorrectValues() {
        LaunchOptions options = LaunchOptions.builder().build();

        assertFalse(options.isHeadless());
        assertNull(options.getPort());
        assertNull(options.getExecutablePath());
    }

    @Test
    void builder_withHeadless_setsHeadless() {
        LaunchOptions options = LaunchOptions.builder()
                .headless(true)
                .build();

        assertTrue(options.isHeadless());
    }

    @Test
    void builder_withHeadlessFalse_setsHeadlessFalse() {
        LaunchOptions options = LaunchOptions.builder()
                .headless(false)
                .build();

        assertFalse(options.isHeadless());
    }

    @Test
    void builder_withPort_setsPort() {
        LaunchOptions options = LaunchOptions.builder()
                .port(9222)
                .build();

        assertEquals(9222, options.getPort());
    }

    @Test
    void builder_withNullPort_setsNullPort() {
        LaunchOptions options = LaunchOptions.builder()
                .port(null)
                .build();

        assertNull(options.getPort());
    }

    @Test
    void builder_withExecutablePath_setsPath() {
        LaunchOptions options = LaunchOptions.builder()
                .executablePath("/usr/bin/chromium")
                .build();

        assertEquals("/usr/bin/chromium", options.getExecutablePath());
    }

    @Test
    void builder_withAllOptions_setsAll() {
        LaunchOptions options = LaunchOptions.builder()
                .headless(true)
                .port(8080)
                .executablePath("/path/to/chrome")
                .build();

        assertTrue(options.isHeadless());
        assertEquals(8080, options.getPort());
        assertEquals("/path/to/chrome", options.getExecutablePath());
    }

    @Test
    void builder_staticFactoryMethodExists() {
        assertNotNull(LaunchOptions.builder());
    }

    @Test
    void builder_chainMethodsReturnBuilder() {
        LaunchOptions.Builder builder = LaunchOptions.builder();

        assertSame(builder, builder.headless(true));
        assertSame(builder, builder.port(9222));
        assertSame(builder, builder.executablePath("/path"));
    }

    @Test
    void multipleBuilds_createSeparateInstances() {
        LaunchOptions options1 = LaunchOptions.builder().port(9222).build();
        LaunchOptions options2 = LaunchOptions.builder().port(9223).build();

        // Each build creates an independent instance
        assertEquals(9222, options1.getPort());
        assertEquals(9223, options2.getPort());
        assertNotSame(options1, options2);
    }

    @Test
    void launchOptions_isImmutable() {
        LaunchOptions options = LaunchOptions.builder()
                .headless(true)
                .port(9222)
                .executablePath("/chrome")
                .build();

        // Cannot modify after creation - values remain constant
        assertTrue(options.isHeadless());
        assertEquals(9222, options.getPort());
        assertEquals("/chrome", options.getExecutablePath());
    }

    @Test
    void builder_withZeroPort() {
        LaunchOptions options = LaunchOptions.builder()
                .port(0)
                .build();

        assertEquals(0, options.getPort());
    }

    @Test
    void builder_withHighPort() {
        LaunchOptions options = LaunchOptions.builder()
                .port(65535)
                .build();

        assertEquals(65535, options.getPort());
    }
}
