package com.devoxx.genie.config;

import com.devoxx.genie.service.embedding.PgVectorEmbeddingStore512Dim;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PgVectorEmbeddingStore512DimConfig {

    @Bean(name = "dbEmbeddingStore512")
    public EmbeddingStore<TextSegment> dbEmbeddingStore(@Value("${genie.database.name}") String dbName,
                                                        @Value("${genie.database.hostname}") String dbHost,
                                                        @Value("${genie.database.port}") int dbPort,
                                                        @Value("${genie.database.username}") String dbUser,
                                                        @Value("${genie.database.password}") String dbPassword) {

        boolean useIndex = true;
        boolean createTable = true;
        boolean dropTable = false;

        return new PgVectorEmbeddingStore512Dim(dbHost,
            dbPort,
            dbUser,
            dbPassword,
            dbName,
            useIndex,
            1000,
            createTable,
            dropTable);
    }
}
