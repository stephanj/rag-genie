package dev.langchain4j.model.cohere.request.enumeration;

public enum CohereLanguageModel {
    EMBED_ENGLISH_V3_0("embed-english-v3.0", 1024),
    EMBED_MULTILINGUAL_V3_0("embed-multilingual-v3.0", 1024),
    EMBED_ENGLISH_LIGHT_V3_0("embed-english-light-v3.0", 384),
    EMBED_MULTILINGUAL_LIGHT_V3_0("embed-multilingual-light-v3.0", 384),
    EMBED_ENGLISH_V2_0("embed-english-v2.0", 4096),
    EMBED_ENGLISH_LIGHT_V2_0("embed-english-light-v2.0", 1024),
    EMBED_MULTILINGUAL_V2_0("embed-multilingual-v2.0", 768);

    private final String modelName;
    private final int dimensions;

    CohereLanguageModel(String modelName, int dimensions) {
        this.modelName = modelName;
        this.dimensions = dimensions;
    }

    public String getModelName() {
        return modelName;
    }

    public int getDimensions() {
        return dimensions;
    }
}
