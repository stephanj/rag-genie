package dev.langchain4j.model.cohere.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class DetokenizeRequest {

    /**
     * The list of tokens to be detokenized.
     */
    private List<Integer> tokens;

    /**
     * An optional parameter to provide the model name.
     * This will ensure that the detokenization is done by the tokenizer used by that model.
     */
    private String model;

    public DetokenizeRequest() {
    }
}
