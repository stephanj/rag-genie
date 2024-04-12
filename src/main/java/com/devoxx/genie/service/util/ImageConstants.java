package com.devoxx.genie.service.util;

public enum ImageConstants {
    THUMBNAIL("thumbnail"),
    MEDIUM("medium"),
    LARGE("large"),
    JPG("jpg"),
    WEBP("webp");

    private final String value;

    ImageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
