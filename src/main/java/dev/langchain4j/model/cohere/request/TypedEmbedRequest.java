package dev.langchain4j.model.cohere.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class TypedEmbedRequest extends EmbedRequest {

    /**
     * The embedding types
     * "float": Use this when you want to get back the default float embeddings. Valid for all models.
     * "int8": Use this when you want to get back signed int8 embeddings. Valid for only v3 models.
     * "uint8": Use this when you want to get back unsigned int8 embeddings. Valid for only v3 models.
     * "binary": Use this when you want to get back signed binary embeddings. Valid for only v3 models.
     * "ubinary": Use this when you want to get back unsigned binary embeddings. Valid for only v3 models.
     */
    protected String[] embedding_types;
}
