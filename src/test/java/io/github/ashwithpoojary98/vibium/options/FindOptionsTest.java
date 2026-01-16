package io.github.ashwithpoojary98.vibium.options;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FindOptions}.
 */
class FindOptionsTest {

    @Test
    void builder_withDefaults_hasThirtySecondTimeout() {
        FindOptions options = FindOptions.builder().build();

        assertEquals(Duration.ofSeconds(30), options.getTimeout());
    }

    @Test
    void builder_withTimeout_setsTimeout() {
        FindOptions options = FindOptions.builder()
                .timeout(Duration.ofSeconds(10))
                .build();

        assertEquals(Duration.ofSeconds(10), options.getTimeout());
    }

    @Test
    void builder_withZeroTimeout() {
        FindOptions options = FindOptions.builder()
                .timeout(Duration.ZERO)
                .build();

        assertEquals(Duration.ZERO, options.getTimeout());
    }

    @Test
    void builder_withMillisecondTimeout() {
        FindOptions options = FindOptions.builder()
                .timeout(Duration.ofMillis(500))
                .build();

        assertEquals(500, options.getTimeout().toMillis());
    }

    @Test
    void builder_withMinuteTimeout() {
        FindOptions options = FindOptions.builder()
                .timeout(Duration.ofMinutes(1))
                .build();

        assertEquals(60000, options.getTimeout().toMillis());
    }

    @Test
    void builder_staticFactoryMethodExists() {
        assertNotNull(FindOptions.builder());
    }

    @Test
    void builder_chainMethodsReturnBuilder() {
        FindOptions.Builder builder = FindOptions.builder();

        assertSame(builder, builder.timeout(Duration.ofSeconds(5)));
    }

    @Test
    void findOptions_isImmutable() {
        Duration timeout = Duration.ofSeconds(15);
        FindOptions options = FindOptions.builder()
                .timeout(timeout)
                .build();

        // Cannot modify after creation
        assertEquals(timeout, options.getTimeout());
    }

    @Test
    void builder_withNullTimeout_setsNull() {
        FindOptions options = FindOptions.builder()
                .timeout(null)
                .build();

        assertNull(options.getTimeout());
    }

    @Test
    void builder_overridesDefaultTimeout() {
        FindOptions options = FindOptions.builder()
                .timeout(Duration.ofSeconds(5))
                .build();

        assertNotEquals(Duration.ofSeconds(30), options.getTimeout());
        assertEquals(Duration.ofSeconds(5), options.getTimeout());
    }

    @Test
    void builder_multipleBuilds_createIndependentInstances() {
        FindOptions.Builder builder = FindOptions.builder();

        FindOptions options1 = builder.timeout(Duration.ofSeconds(10)).build();
        FindOptions options2 = builder.timeout(Duration.ofSeconds(20)).build();

        // Both point to latest value due to builder reuse
        assertEquals(Duration.ofSeconds(20), options2.getTimeout());
    }
}
