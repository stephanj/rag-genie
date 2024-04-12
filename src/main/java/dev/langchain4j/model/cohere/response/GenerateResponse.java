package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GenerateResponse {

    private String id;
    private String prompt;
    private List<Generation> generations;
    private Meta meta;

    public List<String> getTexts() {
        List<String> texts = new ArrayList<>();
        for (Generation generation : generations) {
            texts.add(generation.getText());
        }
        return texts;
    }
}
