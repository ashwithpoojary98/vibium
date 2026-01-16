package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

import java.util.List;

/**
 * Information about a browsing context (tab/frame).
 */
@Getter
public final class BrowsingContextInfo {

    private final String context;
    private final String url;
    private final List<BrowsingContextInfo> children;
    private final String parent;

    public BrowsingContextInfo(String context, String url, List<BrowsingContextInfo> children, String parent) {
        this.context = context;
        this.url = url;
        this.children = children;
        this.parent = parent;
    }
}
