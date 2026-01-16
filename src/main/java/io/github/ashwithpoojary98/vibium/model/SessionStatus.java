package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

/**
 * Session status information from the browser.
 */
@Getter
public final class SessionStatus {

    private final boolean ready;
    private final String message;

    public SessionStatus(boolean ready, String message) {
        this.ready = ready;
        this.message = message;
    }
}
