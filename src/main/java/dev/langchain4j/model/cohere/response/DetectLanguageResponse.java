package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DetectLanguageResponse {

    private String id;
    private List<Result> results;
    private Meta meta;

    public static class Result {
        String language_code;
        String language_name;
    }
}
