package com.devoxx.genie.service.embedding;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

public class PgVectorEmbeddingStore1024Dim extends PgVectorEmbeddingStore implements EmbeddingStore<TextSegment> {

    public PgVectorEmbeddingStore1024Dim(
        String host,
        Integer port,
        String user,
        String password,
        String database,
        Boolean useIndex,
        Integer indexListSize,
        Boolean createTable,
        Boolean dropTableFirst) {
        super(host, port, user, password, database, "genie_document_1024", 1024, useIndex, indexListSize, createTable, dropTableFirst);
    }
}
