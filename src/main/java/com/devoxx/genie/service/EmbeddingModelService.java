package com.devoxx.genie.service;

import com.devoxx.genie.domain.EmbeddingModelReference;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.repository.EmbeddingModelReferenceRepository;
import com.devoxx.genie.service.dto.DocumentDTO;
import com.devoxx.genie.service.dto.EmbeddingModelReferenceDTO;
import com.devoxx.genie.service.mapper.EmbeddingModelMapper;
import dev.langchain4j.model.cohere.CohereEmbeddingModel;
import dev.langchain4j.model.cohere.request.enumeration.CohereInputType;
import dev.langchain4j.model.cohere.request.enumeration.CohereLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.BgeSmallEnEmbeddingModel;
import dev.langchain4j.model.embedding.E5SmallV2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class EmbeddingModelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddingModelService.class);
    private final EmbeddingModelReferenceRepository embeddingModelReferenceRepository;
    private final EmbeddingModelMapper embeddingModelMapper;
    private final ApiKeyService apiKeyService;

    public EmbeddingModelService(EmbeddingModelMapper embeddingModelMapper,
                                 ApiKeyService apiKeyService,
                                 EmbeddingModelReferenceRepository embeddingModelReferenceRepository) {
        this.embeddingModelMapper = embeddingModelMapper;
        this.apiKeyService = apiKeyService;
        this.embeddingModelReferenceRepository = embeddingModelReferenceRepository;
    }

    public List<EmbeddingModelReferenceDTO> findAll() {
        LOGGER.debug("Getting all embedding models");
        return embeddingModelReferenceRepository.findAll()
            .stream()
            .map(embeddingModelMapper::toDto)
            .toList();
    }

    public EmbeddingModelReferenceDTO getEmbeddingModelReferenceDTOById(Long id) {
        LOGGER.debug("Getting embedding model: {}", id);
        return embeddingModelReferenceRepository.findById(id)
            .map(embeddingModelMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Embedding model not found: " + id));
    }

    public EmbeddingModelReference getEmbeddingModelReferenceById(Long embedId) {
        LOGGER.debug("Getting embedding model reference from embed ID: {}", embedId);
        return embeddingModelReferenceRepository.findById(embedId)
            .orElseThrow(() -> new IllegalArgumentException("Embedding model not found: " + embedId));
    }

    public Optional<EmbeddingModel> getEmbeddingModelByUserIdAndRefId(Long userId, Long referenceId) {
        LOGGER.debug("Getting embedding model by reference id: {}", referenceId);
        return embeddingModelReferenceRepository.findById(referenceId)
            .map(embeddingModelReference -> getEmbeddingModel(userId, embeddingModelReference));
    }

    /**
     * Get embedding model by user id and reference id.
     * @param userId                  the user id, used for API key
     * @param embeddingModelReference the embedding model reference
     * @return the embedding model
     */
    @NotNull
    private EmbeddingModel getEmbeddingModel(Long userId, EmbeddingModelReference embeddingModelReference) {
        LOGGER.debug("Getting embedding model: {}", embeddingModelReference);
        String embeddingModelName = embeddingModelReference.getSlug();
        return switch (embeddingModelName) {
            case "minilm-l6-v2" -> new AllMiniLmL6V2EmbeddingModel();
            case "bge-small-en" -> new BgeSmallEnEmbeddingModel();
            case "e5-small-v2" -> new E5SmallV2EmbeddingModel();
            case "embed-english-light-v3.0" ->
                getCohereEmbeddingModel(userId, CohereLanguageModel.EMBED_ENGLISH_LIGHT_V3_0);
            case "embed-multilingual-light-v3.0" ->
                getCohereEmbeddingModel(userId, CohereLanguageModel.EMBED_MULTILINGUAL_LIGHT_V3_0);
            case "embed-english-v3.0" -> getCohereEmbeddingModel(userId, CohereLanguageModel.EMBED_ENGLISH_V3_0);
            case "embed-multilingual-v3.0" -> getCohereEmbeddingModel(userId, CohereLanguageModel.EMBED_MULTILINGUAL_V3_0);
            case "text-embedding-3-small" -> getOpenAiEmbeddingModel(userId, "text-embedding-3-small", embeddingModelReference.getDimSize());
            case "text-embedding-3-large" -> getOpenAiEmbeddingModel(userId, "text-embedding-3-large", embeddingModelReference.getDimSize());
            default -> throw new IllegalArgumentException("Unknown embedding model: " + embeddingModelName);
        };
    }

    /**
     * Get the Cohere embedding model
     *
     * @param userId             the user if for which we'll get the Cohere api key
     * @param embedLanguageModel the cohere embedding language model to use
     * @return the LangChain4J compliant EmbeddingModel
     */
    private EmbeddingModel getCohereEmbeddingModel(final Long userId,
                                                   final CohereLanguageModel embedLanguageModel) {
        return this.apiKeyService.getApiKeyForUserIdAndLanguageModelType(userId, LanguageModelType.COHERE)
            .map(apiKey -> new CohereEmbeddingModel(null,
                apiKey,
                embedLanguageModel,
                CohereInputType.SEARCH_QUERY,
                Duration.ofSeconds(60),
                false,
                false))
            .orElseThrow();
    }

    private OpenAiEmbeddingModel getOpenAiEmbeddingModel(final Long userId,
                                                         final String modelName,
                                                         final int dimension) {
        return this.apiKeyService.getApiKeyForUserIdAndLanguageModelType(userId, LanguageModelType.OPENAI)
            .map(apiKey -> new OpenAiEmbeddingModel(
                "https://api.openai.com/v1",
                apiKey,
                "",
                modelName,
                dimension,
                "Devoxx",
                Duration.ofSeconds(10),
                3,
                null,
                false,
                false,
                new OpenAiTokenizer(modelName))
            ).orElseThrow();
    }
}
