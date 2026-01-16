package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

/**
 * Result from a navigation command.
 */
@Getter
public final class NavigationResult {

    private final String navigation;
    private final String url;

    public NavigationResult(String navigation, String url) {
        this.navigation = navigation;
        this.url = url;
    }
}
