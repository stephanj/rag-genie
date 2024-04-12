package com.devoxx.genie.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PgVectorEmbeddingStore384DimConfig {

    @Bean(name = "dbEmbeddingStore384")
    public EmbeddingStore<TextSegment> dbEmbeddingStore(@Value("${genie.database.name}") String dbName,
                                                        @Value("${genie.database.hostname}") String dbHost,
                                                        @Value("${genie.database.port}") int dbPort,
                                                        @Value("${genie.database.username}") String dbUser,
                                                        @Value("${genie.database.password}") String dbPassword) {

        boolean useIndex = true;
        boolean createTable = true;
        boolean dropTable = false;

        return new PgVectorEmbeddingStore(dbHost,
            dbPort,
            dbUser,
            dbPassword,
            dbName,
            "genie_document_384",
            384,
            useIndex,
            1000,
            createTable,
            dropTable);
    }
}
