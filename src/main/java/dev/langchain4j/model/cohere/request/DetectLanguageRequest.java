package dev.langchain4j.model.cohere.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class DetectLanguageRequest {

    /**
     * List of strings to run the detection on.
     */
    private List<String> texts;

    DetectLanguageRequest() {
    }
}
