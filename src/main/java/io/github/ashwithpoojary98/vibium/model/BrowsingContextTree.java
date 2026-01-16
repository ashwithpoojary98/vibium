package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

import java.util.List;

/**
 * Represents the browsing context tree returned by browsingContext.getTree.
 */
@Getter
public final class BrowsingContextTree {

    private final List<BrowsingContextInfo> contexts;

    public BrowsingContextTree(List<BrowsingContextInfo> contexts) {
        this.contexts = contexts;
    }
}
