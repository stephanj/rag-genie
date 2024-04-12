package com.devoxx.genie.service.embedding.cohere;

public enum CohereEmbeddingType {
    FLOAT("float"),
    INT8("int8"),
    UINT8("uint8"),
    BINARY("binary"),
    UBINARY("ubinary");

    private final String value;

    CohereEmbeddingType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
