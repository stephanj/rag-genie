package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Likelihood {
    private String token;
    private Double likelihood;
}
