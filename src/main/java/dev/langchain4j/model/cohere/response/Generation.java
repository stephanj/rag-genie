package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Generation {

    private String id;
    private String text;
    private Integer index;
    private Double likelihood;
    private List<Likelihood> token_likelihoods;

    public String getText() {
        return text;
    }
}
