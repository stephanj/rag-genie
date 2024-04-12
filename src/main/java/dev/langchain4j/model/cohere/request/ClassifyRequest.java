package dev.langchain4j.model.cohere.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class ClassifyRequest {

    /**
     * Represents a list of queries to be classified, each entry must not be empty. The maximum is 96 inputs.
     */
    private List<String> inputs;

    /**
     * An array of examples to provide context to the model. Each example is a text string and its associated label/class.
     * Each unique label requires at least 2 examples associated with it; the maximum number of examples is 2500,
     * and each example has a maximum length of 512 tokens. The values should be structured as {text: "...",label: "..."}.
     * Note: Custom Models trained on classification examples don't require the examples parameter to be passed in explicitly.
     */
    private List<Example> examples;

    /**
     * The identifier of the model. Currently available models are embed-multilingual-v2.0, embed-english-light-v2.0,
     * and embed-english-v2.0 (default). Smaller "light" models are faster, while larger models will perform better.
     * Custom models can also be supplied with their full ID.
     */
    private String model;

    /**
     * The ID of a custom playground preset. You can create presets in the playground.
     * If you use a preset, all other parameters become optional, and any included parameters will override the preset's parameters.
     */
    private String preset;

    /**
     * One of NONE|START|END to specify how the API will handle inputs longer than the maximum token length.
     * Passing START will discard the start of the input. END will discard the end of the input.
     * In both cases, input is discarded until the remaining input is exactly the maximum input token length for the model.
     * If NONE is selected, when the input exceeds the maximum input token length an error will be returned.
     * Default: END
     */
    private String truncate;

    ClassifyRequest() {
    }

    public static class Example {
        private final String text;
        private final String label;

        public Example(String text, String label) {
            this.text = text;
            this.label = label;
        }
    }
}
