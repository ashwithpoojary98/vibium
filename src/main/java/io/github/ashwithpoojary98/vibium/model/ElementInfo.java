package io.github.ashwithpoojary98.vibium.model;

import lombok.Getter;

/**
 * Information about a DOM element including its tag, text content, and bounding box.
 */
@Getter
public final class ElementInfo {

    private final String tagName;
    private final String textContent;
    private final Box box;

    private ElementInfo(Builder builder) {
        this.tagName = builder.tagName;
        this.textContent = builder.textContent;
        this.box = builder.box;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String tagName;
        private String textContent;
        private Box box;

        public Builder tagName(String tagName) {
            this.tagName = tagName;
            return this;
        }

        public Builder textContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public Builder box(Box box) {
            this.box = box;
            return this;
        }

        public ElementInfo build() {
            return new ElementInfo(this);
        }
    }
}
