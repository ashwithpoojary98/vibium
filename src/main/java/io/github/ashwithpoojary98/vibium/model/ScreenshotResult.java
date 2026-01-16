package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

import java.util.Base64;

/**
 * Result from a screenshot command.
 */
@Getter
public final class ScreenshotResult {

    private final String data;

    public ScreenshotResult(String data) {
        this.data = data;
    }

    /**
     * Decode the base64-encoded image data.
     *
     * @return the raw PNG image bytes
     */
    public byte[] decode() {
        return Base64.getDecoder().decode(data);
    }
}
