package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.ApiKeyService;
import com.devoxx.genie.service.DocumentService;
import com.devoxx.genie.service.EmbeddingModelService;
import com.devoxx.genie.service.dto.DocumentWithEmbeddingDTO;
import com.devoxx.genie.service.dto.EmbeddingModelReferenceDTO;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umap.Umap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;
import static com.devoxx.genie.web.rest.util.DimensionUtil.isInvalidDimension;

@RestController
@RequestMapping("/api")
public class EmbeddingModelResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddingModelResource.class);
    private final DocumentService documentService;
    private final EmbeddingModelService embeddingModelService;
    private final ApiKeyService apiKeyService;

    public EmbeddingModelResource(DocumentService documentService,
                                  EmbeddingModelService embeddingModelService,
                                  ApiKeyService apiKeyService) {
        this.documentService = documentService;
        this.embeddingModelService = embeddingModelService;
        this.apiKeyService = apiKeyService;
    }

    /**
     * Get all used embedding models for authenticated user and language types.
     *
     * @return the response entity
     */
    @GetMapping("/embedding-model")
    public ResponseEntity<List<EmbeddingModelReferenceDTO>> getAllEmbeddingModels() {
        LOGGER.debug("Get all embeddings");

        List<EmbeddingModelReferenceDTO> embeddingModels = embeddingModelService.findAll();

        // Filter the commercial embeddings for which there are no user keys
        List<EmbeddingModelReferenceDTO> filteredList = new ArrayList<>();
        embeddingModels.forEach(embeddingModelDTO -> {
            if (embeddingModelDTO.getProvider().name().equals("EMBED") ||
                apiKeyService.isLanguageModelKeyDefined(HARD_CODED_USER_ID, embeddingModelDTO.getProvider())) {
                filteredList.add(embeddingModelDTO);
            }
        });

        return ResponseEntity.ok(filteredList);
    }

    /**
     * Get all used embedding models for authenticated user and language types.
     *
     * @return the response entity
     */
    @GetMapping("/embedding-model/used")
    public ResponseEntity<List<EmbeddingModelReferenceDTO>> getUsedEmbeddingModels() {
        LOGGER.debug("Get used embedding models");

        List<EmbeddingModelReferenceDTO> usedModels = documentService.getByUserIdAndDimension(HARD_CODED_USER_ID)
            .stream()
            .map(documentDTO -> {
                try {
                    Long embedId = JsonParser.parseString(documentDTO.getMetadata()).getAsJsonObject().get("embedId").getAsLong();
                    return embeddingModelService.getEmbeddingModelReferenceDTOById(embedId);
                } catch (Exception e) {
                    LOGGER.error("Error parsing metadata for document: {}", documentDTO.getId(), e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        return ResponseEntity.ok(usedModels);
    }

    /**
     * Get all RAW (float[][]) vector embeddings for visualization.
     * @param dimension the dimension
     * @param contentId the content id
     * @return the response entity
     */
    @GetMapping("/embeddings/{dimension}/{contentId}")
    public ResponseEntity<List<DocumentWithEmbeddingDTO>> getAll(@PathVariable Integer dimension,
                                                                 @PathVariable Long contentId) {
        LOGGER.debug("Get all embeddings for dimension {}", dimension);

        if (isInvalidDimension(dimension)) {
            return ResponseEntity.badRequest().build();
        }

        List<DocumentWithEmbeddingDTO> allContentEmbeddingsForUserId =
            documentService.findAllContentEmbeddingsForUserId(dimension, contentId, HARD_CODED_USER_ID);

        if (!allContentEmbeddingsForUserId.isEmpty()) {
            final Umap umap = new Umap();
            umap.setNumberComponents(3);         // number of dimensions in result
            umap.setNumberNearestNeighbours(15);
            umap.setThreads(1);                  // use > 1 to enable parallelism

            final float[][] allEmbeddingsForUserId = allContentEmbeddingsForUserId
                .stream()
                .map(DocumentWithEmbeddingDTO::getEmbedding)
                .toArray(float[][]::new);

            final float[][] result = umap.fitTransform(allEmbeddingsForUserId);

            for (int i = 0; i < allContentEmbeddingsForUserId.size(); i++) {
                allContentEmbeddingsForUserId.get(i).setEmbedding(result[i]);
            }

            return ResponseEntity.ok(allContentEmbeddingsForUserId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
