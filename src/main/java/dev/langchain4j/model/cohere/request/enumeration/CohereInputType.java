package dev.langchain4j.model.cohere.request.enumeration;

public enum CohereInputType {
    // Used for embeddings stored in a vector database for search use-cases.
    SEARCH_DOCUMENT("search_document"),
    // Used for embeddings of search queries run against a vector DB to find relevant documents.
    SEARCH_QUERY("search_query"),
    // Used for embeddings passed through a text classifier.
    CLASSIFICATION("classification"),
    // Used for the embeddings run through a clustering algorithm.
    CLUSTERING("clustering");

    private final String type;

    CohereInputType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
