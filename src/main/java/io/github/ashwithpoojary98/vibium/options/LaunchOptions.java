package io.github.ashwithpoojary98.vibium.options;

import lombok.Getter;

/**
 * Options for launching a browser instance.
 *
 * <p>Example usage:
 * <pre>{@code
 * LaunchOptions options = LaunchOptions.builder()
 *     .headless(true)
 *     .port(9222)
 *     .build();
 *
 * Vibe vibe = new Browser().launch(options).join();
 * }</pre>
 */
@Getter
public final class LaunchOptions {

    private final boolean headless;
    private final Integer port;
    private final String executablePath;

    private LaunchOptions(Builder builder) {
        this.headless = builder.headless;
        this.port = builder.port;
        this.executablePath = builder.executablePath;
    }

    /**
     * Create a new builder for LaunchOptions.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link LaunchOptions}.
     */
    public static final class Builder {

        private boolean headless = false;
        private Integer port = null;
        private String executablePath = null;

        private Builder() {
        }

        /**
         * Set whether to run the browser in headless mode.
         *
         * @param headless true for headless mode
         * @return this builder
         */
        public Builder headless(boolean headless) {
            this.headless = headless;
            return this;
        }

        /**
         * Set the port for the browser's WebSocket server.
         *
         * @param port the port number (null for automatic)
         * @return this builder
         */
        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        /**
         * Set the path to the browser executable.
         *
         * @param executablePath path to the executable (null for auto-detect)
         * @return this builder
         */
        public Builder executablePath(String executablePath) {
            this.executablePath = executablePath;
            return this;
        }

        /**
         * Build the LaunchOptions instance.
         *
         * @return a new LaunchOptions
         */
        public LaunchOptions build() {
            return new LaunchOptions(this);
        }
    }
}
