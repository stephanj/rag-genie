package com.devoxx.genie.service.splitter;

import com.devoxx.genie.service.EmbeddingModelService;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.DocumentDTO;
import com.devoxx.genie.service.dto.EmbeddingModelReferenceDTO;
import com.devoxx.genie.service.dto.enumeration.DocumentType;
import com.google.gson.JsonObject;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Service
public class SimilarityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarityService.class);
    private final EmbeddingStore<TextSegment> embeddingStore384;
    private final EmbeddingStore<TextSegment> embeddingStore512;
    private final EmbeddingStore<TextSegment> embeddingStore1024;
    private final EmbeddingModelService embeddingModelService;

    public SimilarityService(@Qualifier("dbEmbeddingStore384") EmbeddingStore<TextSegment> embeddingStore384,
                             @Qualifier("dbEmbeddingStore512") EmbeddingStore<TextSegment> embeddingStore512,
                             @Qualifier("dbEmbeddingStore1024") EmbeddingStore<TextSegment> embeddingStore1024,
                             EmbeddingModelService embeddingModelService) {
        this.embeddingStore384 = embeddingStore384;
        this.embeddingStore512 = embeddingStore512;
        this.embeddingStore1024 = embeddingStore1024;
        this.embeddingModelService = embeddingModelService;
    }

    /**
     * Find similar documents based on the query
     * @param chatModelDTO the chat model DTO
     * @return list of related query documents
     */
    public List<DocumentDTO> findSimilarDocumentsBasedOnQuery(ChatModelDTO chatModelDTO) {
        LOGGER.debug("Finding similar documents for user {} based on query: '{}'", chatModelDTO.getUserId(), chatModelDTO.getQuestion());

        EmbeddingModelReferenceDTO embeddingModelReferenceDTOById =
            embeddingModelService.getEmbeddingModelReferenceDTOById(chatModelDTO.getEmbeddingModelRefId());

        EmbeddingModel embeddingModel =
            embeddingModelService.getEmbeddingModelByUserIdAndRefId(chatModelDTO.getUserId(), chatModelDTO.getEmbeddingModelRefId())
            .orElseThrow(() -> new IllegalArgumentException("Embedding model not found"));

        Embedding embeddedQuery = embeddingModel.embed(chatModelDTO.getQuestion()).content();

        // Experimental LangChain4J feature as of March 27th 2024
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
            .queryEmbedding(embeddedQuery)
            .maxResults(chatModelDTO.getMaxResults())
            .minScore(chatModelDTO.getMinScore())
            .filter(metadataKey("userId").isEqualTo(chatModelDTO.getUserId()))
            .build();

        EmbeddingStore<TextSegment> embeddingStore = switch (embeddingModelReferenceDTOById.getDimSize()) {
            case 512 -> embeddingStore512;
            case 1024 -> embeddingStore1024;
            default -> embeddingStore384;
        };

        return embeddingStore.search(searchRequest)
            .matches().stream()
            .map(match -> {
                Integer contentId = match.embedded().metadata().getInteger("contentId");
                JsonObject json = new JsonObject();
                json.addProperty("contentId", contentId);
                return DocumentDTO.builder()
                    .id(match.embeddingId())
                    .text(match.embedded().text())
                    .score(match.score())
                    .docType(DocumentType.CONTENT)
                    .metadata(json.toString()).build();
            }).toList();
    }
}
