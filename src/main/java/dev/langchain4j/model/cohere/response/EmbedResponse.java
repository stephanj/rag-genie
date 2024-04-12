package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmbedResponse {

    private String id;

    private String[] texts;

    private Float[][] embeddings;

    private Meta meta;
}
