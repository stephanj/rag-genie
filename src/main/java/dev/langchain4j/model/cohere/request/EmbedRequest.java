package dev.langchain4j.model.cohere.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class EmbedRequest {

    /**
     * An array of strings for the model to embed.
     * The maximum number of texts per call is 96.
     * We recommend reducing the length of each text to be under 512 tokens for optimal quality.
     */
    protected String[] texts;

    /**
     * The identifier of the model.
     * Smaller "light" models are faster, while larger models will perform better.
     * Custom models can also be supplied with their full ID.
     * Available models and corresponding embedding dimensions:
     * embed-english-v3.0 1024
     * embed-multilingual-v3.0 1024
     * embed-english-light-v3.0 384
     * embed-multilingual-light-v3.0 384
     * embed-english-v2.0 4096
     * embed-english-light-v2.0 1024
     * embed-multilingual-v2.0 768
     */
    protected String model;

    /**
     * One of NONE|START|END to specify how the API will handle inputs longer than the maximum token length.
     * Passing START will discard the start of the input.
     * END will discard the end of the input.
     * In both cases, input is discarded until the remaining input is exactly the maximum input token length for the model.
     * If NONE is selected, when the input exceeds the maximum input token length an error will be returned.
     * Default: END
     */
    protected String truncate;

    /**
     * Specifies the type of input passed to the model. Required for embedding models v3 and higher.
     * "search_document": Used for embeddings stored in a vector database for search use-cases.
     * "search_query": Used for embeddings of search queries run against a vector DB to find relevant documents.
     * "classification": Used for embeddings passed through a text classifier.
     * "clustering": Used for the embeddings run through a clustering algorithm.
     */
    protected String input_type;
}
