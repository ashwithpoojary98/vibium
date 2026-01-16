package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

/**
 * Result from a vibium:find command.
 */
@Getter
public final class VibiumFindResult {

    private final String tag;
    private final String text;
    private final Box box;

    public VibiumFindResult(String tag, String text, Box box) {
        this.tag = tag;
        this.text = text;
        this.box = box;
    }
}
