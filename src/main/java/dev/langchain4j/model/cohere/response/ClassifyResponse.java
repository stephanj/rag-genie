package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ClassifyResponse {

    private String id;

    private Classification[] classifications;

    private Meta meta;

    public static class ClassificationDetail {
        private Float confidence;
    }

    public static class Classification {
        private String id;
        @Getter
        private String input;
        @Getter
        private String prediction;
        @Getter
        private Float confidence;
        private Map<String, ClassificationDetail> labels;

        public Float getConfidence(String label) {
            return labels.get(label).confidence;
        }
    }
}
