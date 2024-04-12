package dev.langchain4j.model.cohere.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class RerankRequest {

    /**
     * The identifier of the model to use, one of: rerank-english-v2.0, rerank-multilingual-v2.0
     */
    private String model;

    /**
     * The search query.
     */
    private String query;

    /**
     * A list of document objects or strings to rerank.
     * If a document is provided the text fields is required and all other fields will be preserved in the response.
     * The total max chunks (length of documents * max_chunks_per_doc) must be less than 10000.
     */
    private List<String> documents;

    /**
     * The number of most relevant documents or indices to return, defaults to the length of the documents
     */
    private Integer top_n;

    /**
     * If false, returns results without the doc text - the api will return a list of {index, relevance score}
     * where index is inferred from the list passed into the request.
     * If true, returns results with the doc text passed in - the api will return an ordered list of {index, text,
     * relevance score} where index + text refers to the list passed into the request.
     */
    private Boolean return_documents;
    /**
     * The maximum number of chunks to produce internally from a document
     */
    private Integer max_chunks_per_doc;

    public RerankRequest() {

    }
}
