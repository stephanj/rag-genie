package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.ContentService;
import com.devoxx.genie.service.DocumentService;
import com.devoxx.genie.service.ReRankService;
import com.devoxx.genie.service.dto.DocumentDTO;
import com.devoxx.genie.service.dto.VectorDocumentDTO;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import com.devoxx.genie.web.rest.util.PaginationUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;
import static com.devoxx.genie.web.rest.util.DimensionUtil.isInvalidDimension;
import static com.devoxx.genie.web.rest.util.PaginationUtil.TOTAL_COUNT;

@RestController
@RequestMapping("/api")
public class VectorDocumentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VectorDocumentResource.class);
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USERNOTFOUND = "usernotfound";
    private final DocumentService documentService;
    private final ContentService contentService;
    private final ReRankService reRankService;

    public VectorDocumentResource(ContentService contentService,
                                  DocumentService documentService,
                                  ReRankService reRankService) {
        this.contentService = contentService;
        this.documentService = documentService;
        this.reRankService = reRankService;
    }

    /**
     * Store document chunks.
     * @param contentId        the content id
     * @param embeddingModelId the embedding model id
     * @param chunks           the text chunks to store in vector db
     * @return list of similarities
     */
    @PostMapping(value = "/vector-document/{contentId}/{embeddingModelId}")
    public ResponseEntity<Object> storeDocuments(@PathVariable Long contentId,
                                                 @PathVariable Long embeddingModelId,
                                                 @RequestBody List<String> chunks) {

        LOGGER.debug("REST Store documents using embedding model {} with chunks: {}", embeddingModelId, chunks);

        documentService.save(HARD_CODED_USER_ID, contentId, chunks, embeddingModelId);

        return ResponseEntity.ok().build();
    }

    /**
     * Update document.
     * @param dimension the vector dimension
     * @param vectorDocument the vector document
     * @return the response entity
     */
    @PutMapping(value = "/vector-document/{dimension}")
    public ResponseEntity<Object> updateDocument(@PathVariable int dimension,
                                                 @RequestBody VectorDocumentDTO vectorDocument) {

        LOGGER.debug("REST request to store chunks: {}", vectorDocument);

        documentService.update(HARD_CODED_USER_ID, dimension, vectorDocument);

        return ResponseEntity.ok().build();
    }

    /**
     * Get documents for user by dimension.
     * @param dimension the dimension
     * @param pageable the pageable
     * @return the documents for user by dimension
     */
    @GetMapping(value = "/vector-document/{dimension}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsForUserByDimension(@PathVariable int dimension,
                                                                            Pageable pageable) {

        LOGGER.debug("REST request to vector db for user with dimensions : {}", dimension);

        if (isInvalidDimension(dimension)) {
            return ResponseEntity.badRequest().build();
        }

        Page<DocumentDTO> userDocuments = documentService.getByUserIdAndDimension(HARD_CODED_USER_ID, dimension, pageable);

        userDocuments.forEach(contentService::addContentInfo);

        var headers = PaginationUtil.generatePaginationHttpHeaders(userDocuments, "/api/vector-document");

        return ResponseEntity.ok().headers(headers).body(userDocuments.getContent());
    }

    /**
     * Get the vector document by dimension and uuid.
     * @param dimension the dimension
     * @param uuid the uuid
     * @return the document by dimension and id
     */
    @GetMapping(value = "/vector-document/{dimension}/{uuid}")
    public ResponseEntity<DocumentDTO> getDocumentByDimensionAndId(@PathVariable Integer dimension,
                                                                   @PathVariable UUID uuid) {
        LOGGER.debug("REST request to vector db by uuid : {} and dimension: {}", uuid, dimension);

        if (isInvalidDimension(dimension)) {
            return ResponseEntity.badRequest().build();
        }

        return documentService.getDocumentByUserIdDimensionAndDocumentUUID(uuid, dimension)
            .map(documentDTO -> {
                contentService.addContentInfo(documentDTO);
                return ResponseEntity.ok().body(documentDTO);
            }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get the total documents for user.
     * @return documens size
     */
    @GetMapping(value = "/vector-document")
    public ResponseEntity<Integer> totalDocumentsForUser() {
        LOGGER.debug("REST request get total documents count for user");

        int size = documentService.getByUserIdAndDimension(HARD_CODED_USER_ID).size();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", size + "");
        return ResponseEntity.ok().headers(headers).build();
    }

    /**
     * Get vector db chunks for user
     * @param question   the question
     * @param embedId    the embedding model id
     * @param maxResults the max results
     * @param minScore   the min score
     * @return list of chunks
     */
    @GetMapping(value = "/vector-document/similarity")
    public ResponseEntity<String> findRelevantDocuments(@RequestParam String question,
                                                        @RequestParam Long embedId,
                                                        @RequestParam(defaultValue = "false") boolean rerank,
                                                        @RequestParam(defaultValue = "3") int maxResults,
                                                        @RequestParam(defaultValue = "0.6") double minScore) {

        LOGGER.debug("REST request to get {} with min score {} chunks for query: {}", maxResults, minScore, question);

        List<EmbeddingMatch<TextSegment>> userResults =
            documentService.findRelevantDocuments(HARD_CODED_USER_ID, embedId, question, maxResults, minScore)
                .stream()
                .filter(match -> match.embedded().metadata().get("userId").equals(HARD_CODED_USER_ID.toString()))
                .toList();

        JsonArray responseArray;

        if (rerank) {
            List<DocumentDTO> usedDocuments = new ArrayList<>();
            userResults.forEach(match ->
                usedDocuments.add(DocumentDTO.builder()
                                             .text(match.embedded().text())
                                             .build()));
            List<DocumentDTO> documentDTOS = reRankService.reRankDocuments(question, usedDocuments);
            responseArray = buildJsonResponseForDocumentList(documentDTOS);

        } else {
            responseArray = buildJsonResponse(userResults);
        }

        return ResponseEntity.ok().body(responseArray.toString());
    }

    /**
     * Filter documents by query
     * @param query the query
     * @return the list of documents
     */
    @GetMapping(value = "/vector-document/search")
    public ResponseEntity<List<DocumentDTO>> filterDocumentsByQuery(@RequestParam String dimension,
                                                                    @RequestParam String query) {
        LOGGER.debug("REST request to filter documents by query: {}", query);

        List<DocumentDTO> foundDocuments = documentService.filterDocumentsByQuery(HARD_CODED_USER_ID, dimension, query);

        foundDocuments.forEach(contentService::addContentInfo);
        documentService.addEmbeddingInfo(foundDocuments);

        HttpHeaders headers = new HttpHeaders();
        headers.add(TOTAL_COUNT, Long.toString(foundDocuments.size()));

        return ResponseEntity.ok().headers(headers).body(foundDocuments);
    }

    /**
     * Delete all documents from vector db for current user
     * @return the response entity
     */
    @DeleteMapping(value = "/vector-document/{dimension}")
    public ResponseEntity<Object> deleteAllDocumentsByDimensionForAuthenticatedUser(@PathVariable int dimension) {
        LOGGER.debug("REST request to delete all documents from vector db");

        if (isInvalidDimension(dimension)) {
            return ResponseEntity.badRequest().body("Invalid dimension");
        }

        try {
            documentService.deleteAllByDimensionForUserId(dimension, HARD_CODED_USER_ID);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting documents, maybe you still have some evaluations linked to them?");
        }
    }

    /**
     * Delete a document from vector db by uuid and dimension.
     * @param dimension the dimension
     * @param uuid the document uuid
     * @return the response entity
     */
    @DeleteMapping(value = "/vector-document/{dimension}/{uuid}")
    public ResponseEntity<Object> deleteDocumentByDimensionAndId(@PathVariable int dimension,
                                                                 @PathVariable UUID uuid) {
        LOGGER.debug("REST request to delete document with id: {} and dimension {}", uuid, dimension);

        if (isInvalidDimension(dimension)) {
            return ResponseEntity.badRequest().body("Invalid dimension");
        }

        return documentService.getDocumentByUserIdDimensionAndDocumentUUID(uuid, dimension)
            .map(documentDTO -> {
                documentService.delete(dimension, UUID.fromString(documentDTO.getId()));
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Builds the JSON response.
     * @param matches the matches
     * @return the JSON response
     */
    private JsonArray buildJsonResponse(List<EmbeddingMatch<TextSegment>> matches) {
        JsonArray responseArray = new JsonArray();
        matches.forEach(match -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", match.embeddingId());
            response.addProperty("content", match.embedded().text());
            response.addProperty("score", match.score());
            Metadata metadata = match.embedded().metadata();
            if (metadata != null) {
                response.addProperty("metadata", metadata.toString());
            }
            responseArray.add(response);
        });
        return responseArray;
    }

    private JsonArray buildJsonResponseForDocumentList(List<DocumentDTO> documents) {
        JsonArray responseArray = new JsonArray();
        documents.forEach(document -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", document.getId());
            response.addProperty("content", document.getText());
            response.addProperty("score", document.getScore());
            responseArray.add(response);
        });
        return responseArray;
    }
}
