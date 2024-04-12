package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummarizeResponse {
    private String id;
    private String summary;
    private Meta meta;

    public String getSummary() {
        return summary;
    }
}
