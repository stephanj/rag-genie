package dev.langchain4j.model.cohere.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class TokenizeRequest {

    /**
     * The string to be tokenized.
     * The minimum text length is 1 character, and the maximum text length is 65536 characters.
     */
    private String text;

    /**
     * An optional parameter to provide the model name.
     * This will ensure that the tokenization uses the tokenizer used by that model.
     */
    private String model;

    TokenizeRequest() {
    }
}
