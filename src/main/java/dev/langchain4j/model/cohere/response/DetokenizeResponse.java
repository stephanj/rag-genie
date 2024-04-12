package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DetokenizeResponse {
    private String text;
    private Meta meta;
}
