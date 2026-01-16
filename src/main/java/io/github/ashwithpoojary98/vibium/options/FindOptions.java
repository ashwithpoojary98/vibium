package io.github.ashwithpoojary98.vibium.options;

import lombok.Getter;

import java.time.Duration;

/**
 * Options for finding elements.
 *
 * <p>Example usage:
 * <pre>{@code
 * FindOptions options = FindOptions.builder()
 *     .timeout(Duration.ofSeconds(10))
 *     .build();
 *
 * Element element = vibe.find("button", options).join();
 * }</pre>
 */
@Getter
public final class FindOptions {

    private final Duration timeout;

    private FindOptions(Builder builder) {
        this.timeout = builder.timeout;
    }

    /**
     * Create a new builder for FindOptions.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link FindOptions}.
     */
    public static final class Builder {

        private Duration timeout = Duration.ofSeconds(30);

        private Builder() {
        }

        /**
         * Set the timeout for finding the element.
         *
         * @param timeout maximum time to wait for the element
         * @return this builder
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Build the FindOptions instance.
         *
         * @return a new FindOptions
         */
        public FindOptions build() {
            return new FindOptions(this);
        }
    }
}
