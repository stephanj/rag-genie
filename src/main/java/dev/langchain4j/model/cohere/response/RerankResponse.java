package dev.langchain4j.model.cohere.response;

import dev.langchain4j.model.cohere.Result;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RerankResponse {

    private List<Result> results;
    private Meta meta;
}
