package com.devoxx.genie.service;

import com.devoxx.genie.domain.Content;
import com.devoxx.genie.domain.EmbeddingModelReference;
import com.devoxx.genie.repository.ContentRepository;
import com.devoxx.genie.service.dto.DocumentDTO;
import com.devoxx.genie.service.dto.VectorDocumentDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;

import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.apache.commons.compress.utils.ArchiveUtils.sanitize;


@Service
public class DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
    public static final String EMBED_ID = "embedId";
    public static final String CONTENT_ID = "contentId";
    public static final String USER_ID = "userId";
    public static final String GENIE_DOCUMENT_TABLE_PREFIX = "genie_document_";
    public static final int DIMENSION_384 = 384;
    public static final int DIMENSION_512 = 512;
    public static final int DIMENSION_1024 = 1024;
    private final EmbeddingStore<TextSegment> embeddingStore384;
    private final EmbeddingStore<TextSegment> embeddingStore512;
    private final EmbeddingStore<TextSegment> embeddingStore1024;
    private final EmbeddingModelService embeddingModelService;
    private final ContentRepository contentRepository;
    private final JdbcTemplate jdbcTemplate;

    public DocumentService(@Qualifier("dbEmbeddingStore384") EmbeddingStore<TextSegment> embeddingStore384,
                           @Qualifier("dbEmbeddingStore512") EmbeddingStore<TextSegment> embeddingStore512,
                           @Qualifier("dbEmbeddingStore1024") EmbeddingStore<TextSegment> embeddingStore1024,
                           JdbcTemplate jdbcTemplate,
                           EmbeddingModelService embeddingModelService,
                           ContentRepository contentRepository) {
        this.embeddingStore384 = embeddingStore384;
        this.embeddingStore512 = embeddingStore512;
        this.embeddingStore1024 = embeddingStore1024;
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingModelService = embeddingModelService;
        this.contentRepository = contentRepository;
    }

    /**
     * Save content chunks as embedding documents for user
     * @param userId           the user id
     * @param contentId        the content id
     * @param chunks           the text chunks
     * @param embeddingModelId the embedding model id to use
     * @return document ids
     */
    @Transactional
    public List<String> save(Long userId, Long contentId, List<String> chunks, Long embeddingModelId) {
        LOGGER.debug("Storing chunks for user id: {}", userId);

        // Early return if content is not found, reducing nesting
        Optional<Content> contentById = contentRepository.findById(contentId);
        if (contentById.isEmpty()) {
            LOGGER.error("Content with id {} not found", contentId);
            return Collections.emptyList();
        }

        // Stream API to process chunks and collect document IDs
        return chunks.stream()
            .map(chunk -> storeEmbedding(userId, contentId, chunk, embeddingModelId))
            .flatMap(Optional::stream)
            .toList();
    }

    /**
     * Create TextSegment and save it to the embedding store for the given user and content
     * @param userId    the user id
     * @param contentId the content id
     * @param chunk     the text chunk
     * @param embeddingModelId the embedding model id
     */
    private Optional<String> storeEmbedding(Long userId,
                                            Long contentId,
                                            String chunk,
                                            Long embeddingModelId) {
        return embeddingModelService.getEmbeddingModelByUserIdAndRefId(userId, embeddingModelId)
            .map(embeddingModel -> {

                // Create metadata for the TextSegment
                Metadata metaData = getMetaData(userId, contentId, embeddingModelId);

                // Create TextSegment
                TextSegment textSegment = new TextSegment(chunk, metaData);

                // Embed the chunk
                Embedding embedding = embeddingModel.embed(chunk).content();

                // Store the embedding
                return storeEmbedding(embedding, textSegment);
            });
    }

    /**
     * Store embedding in the appropriate embedding store
     * @param embedding the embedding
     * @param textSegment the text segment
     * @return the embedding id
     */
    private String storeEmbedding(Embedding embedding, TextSegment textSegment) {
        // Store it in the appropriate embedding store based on the dimension
        String embeddingId = "";
        if (embedding.dimension() == DIMENSION_384) {
            embeddingId = embeddingStore384.add(embedding, textSegment);
        } else if (embedding.dimension() == DIMENSION_512) {
            embeddingId = embeddingStore512.add(embedding, textSegment);
        } else if (embedding.dimension() == DIMENSION_1024) {
            embeddingId = embeddingStore1024.add(embedding, textSegment);
        }
        return embeddingId;
    }

    /**
     * Create metadata for the TextSegment
     * @param userId the user id
     * @param contentId the content id
     * @param embeddingModelId the embedding model id
     * @return the metadata
     */
    @NotNull
    private static Metadata getMetaData(Long userId, Long contentId, Long embeddingModelId) {
        Metadata metaData = new Metadata();
        metaData.add(USER_ID, userId.toString());
        metaData.add(CONTENT_ID, contentId.toString());
        metaData.add(EMBED_ID, embeddingModelId);
        return metaData;
    }

    /**
     * Update vector document
     * @param userId            the user id
     * @param dimension         the dimension
     * @param vectorDocumentDTO the vector document DTO
     */
    @Transactional
    public void update(Long userId,
                       int dimension,
                       VectorDocumentDTO vectorDocumentDTO) {
        LOGGER.debug("Updating vector document: {}", vectorDocumentDTO);

        String sql = String.format("""
        UPDATE genie_document_%d
        SET text = ?
        WHERE embedding_id = ?
        """, dimension);

        jdbcTemplate.update(sql, vectorDocumentDTO.text(), vectorDocumentDTO.id());

        getDocumentByUserIdDimensionAndDocumentUUID(vectorDocumentDTO.id(), dimension)
            .ifPresent(documentDTO -> {
                deleteAllByDimensionForUserId(dimension, userId);

                storeEmbedding(userId,
                    Long.parseLong(documentDTO.getContentId()),
                    vectorDocumentDTO.text(),
                    Long.parseLong(documentDTO.getId()));
            });
    }

    /**
     * Get all documents for user Id
     * @param userId the user id
     * @return list of documents
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> getByUserIdAndDimension(Long userId) {
        int[] dimensions = {DIMENSION_384, DIMENSION_512, DIMENSION_1024};
        List<CompletableFuture<List<DocumentDTO>>> futures = Arrays.stream(dimensions)
            .mapToObj(dimension ->
                CompletableFuture.supplyAsync(() ->
                    getByUserIdAndDimension(userId, dimension, Pageable.unpaged()).getContent()))
            .toList();

        return futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .toList();
    }

    /**
     * Get vector documents by user id, dimension and pageable
     * @param userId   the user id
     * @param dimension the vector dimension size
     * @param pageable the pageable
     * @return the vector documents
     */
    @Transactional(readOnly = true)
    public Page<DocumentDTO> getByUserIdAndDimension(Long userId, Integer dimension, Pageable pageable) {
        LOGGER.debug("REST request to get chunks for user id: {}", userId);

        String tableName = GENIE_DOCUMENT_TABLE_PREFIX + dimension;

        if (pageable.isUnpaged()) {
            String sql = String.format("""
                SELECT * FROM %s
                WHERE metadata ->> 'userId' = CAST(%d AS TEXT)
                ORDER BY embedding_id
                """, tableName, userId);

            List<DocumentDTO> documents = getDocuments(sql);
            addEmbeddingInfo(documents);
            return new PageImpl<>(documents);
        } else {
            String pagedSql = String.format("""
                    SELECT * FROM %s
                    WHERE metadata ->> 'userId' = CAST(%d AS TEXT)
                    ORDER BY embedding_id LIMIT %d OFFSET %d
                """, tableName, userId, pageable.getPageSize(), pageable.getOffset());

            List<DocumentDTO> documents = getDocuments(pagedSql);

            String countSql = String.format("""
                    SELECT COUNT(*) FROM %s
                    WHERE metadata ->> 'userId' = CAST(%d AS TEXT)
                """, tableName, userId);

            int total = jdbcTemplate.queryForObject(countSql, Integer.class);

            addEmbeddingInfo(documents);
            return new PageImpl<>(documents, pageable, total);
        }

    }

    private void addEmbeddingInfo(List<DocumentDTO> documents) {
        documents.forEach(document -> {
            String metadata = document.getMetadata();
            JsonObject jsonObject = JsonParser.parseString(metadata).getAsJsonObject();
            document.setEmbeddingModelRefId(jsonObject.get(EMBED_ID).getAsLong());

            EmbeddingModelReference embeddingModelReferenceById =
                embeddingModelService.getEmbeddingModelReferenceById(document.getEmbeddingModelRefId());
            document.setEmbeddingModelName(embeddingModelReferenceById.getName());
        });
    }

    /**
     * Get all embeddings for user id, content id and dimension.
     * @param dimension the vector dimension size
     * @param contentId the content id
     * @param userId the user id
     * @return the embeddings
     */
    @Transactional(readOnly = true)
    public float[][] findAllContentEmbeddingsForUserId(Integer dimension, Long contentId, Long userId) {
        LOGGER.debug("REST request to get all documents");

        String tableName = GENIE_DOCUMENT_TABLE_PREFIX + dimension;
        String sql = String.format("""
            SELECT * FROM %s
            WHERE metadata ->> 'userId' = CAST(%d AS TEXT)
            AND metadata ->> 'contentId' = CAST(%d AS TEXT)
            """, tableName, userId, contentId);

        List<DocumentDTO> documents = getDocumentsWithEmbeddings(sql);

        float[][] embeddings = new float[documents.size()][];

        int index = 0;
        for (DocumentDTO document : documents) {
            LOGGER.debug("Document: {}", document);

            String embeddingValues = document.getEmbedding();

            if (embeddingValues.isEmpty()) {
                LOGGER.info("Embedding with id {} is empty", document.getId());
                continue;
            }

            float[] floats = convertStringToFloatArray(embeddingValues);
            embeddings[index++] = floats;
        }
        return embeddings;
    }

    /**
     * Convert string of floats to float array
     *
     * @param input the input string
     * @return the float array
     */
    private static float[] convertStringToFloatArray(String input) {
        // Step 1: Trim the brackets
        input = input.substring(1, input.length() - 1);

        // Step 2: Split the string
        String[] stringArray = input.split(",");

        // Step 3: Parse and store in Float[]
        float[] floatArray = new float[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            floatArray[i] = Float.parseFloat(stringArray[i]);
        }

        return floatArray;
    }

    /**
     * Get documents by uuid and dimension.
     * @param uuid the document uuid
     * @param dimension  the dimension
     * @return the vector document
     */
    @Transactional(readOnly = true)
    public Optional<DocumentDTO> getDocumentByUserIdDimensionAndDocumentUUID(UUID uuid, Integer dimension) {

        LOGGER.debug("REST request to get document with dim {} by uuid: {}", dimension, uuid);

        String tableName = GENIE_DOCUMENT_TABLE_PREFIX + dimension;

        String safeEmbeddingId = sanitize(uuid.toString());

        String sql = String.format("SELECT * FROM %s WHERE embedding_id = '%s'", tableName, safeEmbeddingId);

        try {
            List<DocumentDTO> foundDocuments = jdbcTemplate.query(sql, (rs, na) -> mapRowToDocumentDTO(rs));
            addEmbeddingInfo(foundDocuments);
            return foundDocuments.stream().findFirst();
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid UUID: {}", uuid, e);
            throw new IllegalArgumentException("Invalid document ID", e);
        }
    }

    /**
     * Map result set row to document DTO
     * @param rs the result set
     * @return the document DTO
     * @throws SQLException if an error occurs
     */
    private DocumentDTO mapRowToDocumentDTO(ResultSet rs) throws SQLException {
        return DocumentDTO
            .builder()
            .id(rs.getString("embedding_id"))
            .text(rs.getString("text"))
            .metadata(rs.getString("metadata"))
            .build();
    }

    /**
     * Find relevant documents
     * @param userId           the user id, used to get api keys
     * @param embeddingModelId the embedding model id
     * @param query            the query
     * @param maxResults       the max results
     * @param minScore         the min score
     * @return the list of relevant documents
     */
    @Transactional(readOnly = true)
    public List<EmbeddingMatch<TextSegment>> findRelevantDocuments(Long userId,
                                                                   Long embeddingModelId,
                                                                   String query,
                                                                   int maxResults,
                                                                   double minScore) {
        LOGGER.debug("Finding relevant documents for query: {}", query);

        EmbeddingModelReference embeddingModelReferenceFromEmbedId =
            embeddingModelService.getEmbeddingModelReferenceById(embeddingModelId);

        Integer dimSize = embeddingModelReferenceFromEmbedId.getDimSize();

        return embeddingModelService.getEmbeddingModelByUserIdAndRefId(userId, embeddingModelId)
            .map(embeddingModel -> {
                Embedding embeddingQuery = embeddingModel.embed(query).content();
                if (dimSize == DIMENSION_384) {
                    return embeddingStore384.findRelevant(embeddingQuery, maxResults, minScore);
                } else if (dimSize == DIMENSION_512) {
                    return embeddingStore512.findRelevant(embeddingQuery, maxResults, minScore);
                } else if (dimSize == DIMENSION_1024) {
                    return embeddingStore1024.findRelevant(embeddingQuery, maxResults, minScore);
                } else {
                    throw new IllegalArgumentException("Invalid dimension: " + dimSize);
                }
            }).orElseThrow(() -> new IllegalArgumentException("Embedding model not found: " + embeddingModelId));
    }

    /**
     * Delete vector document by id
     * @param dimension the dimension of the document, which specifies the table
     * @param documentId the document id
     */
    @Transactional
    public void delete(int dimension, UUID documentId) {
        String tableName = GENIE_DOCUMENT_TABLE_PREFIX + dimension;
        LOGGER.debug("Deleting from table: {}", tableName);
        LOGGER.debug("Document ID: {}", documentId);

        String sql = "DELETE FROM " + tableName + " WHERE embedding_id = ?";
        LOGGER.debug("Executing SQL: {}", sql);

        try {
            jdbcTemplate.update(sql, documentId);
            LOGGER.debug("Delete successful for document ID: {}", documentId);
        } catch (DataAccessException e) {
            LOGGER.error("Error executing delete: {}", e.getMessage(), e);
            // Optionally rethrow or handle the exception based on your use case
        }
    }

    /**
     * Delete all vector documents by user id
     * @param dimension the dimension of the document, which specifies the table
     * @param userId the user id
     */
    @Transactional
    public void deleteAllByDimensionForUserId(int dimension, Long userId) {
        LOGGER.debug("REST request to delete all documents for user id: {}", userId);

        String tableName = GENIE_DOCUMENT_TABLE_PREFIX + dimension;
        String sql = "DELETE FROM " + tableName + " WHERE metadata ->> 'userId' = CAST(" + userId + " AS TEXT)";
        jdbcTemplate.execute(sql);
    }

    /**
     * Filter documents by query
     * @param userId the user id
     * @param dimension the dimension of the document, which specifies the table
     * @param query the query
     * @return the list of documents
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> filterDocumentsByQuery(Long userId, String dimension, String query) {
        LOGGER.debug("REST request to filter documents by query: {}", query);

        String tableName = GENIE_DOCUMENT_TABLE_PREFIX + dimension;

        String sql = String.format("""
            SELECT * FROM %s
            WHERE metadata ->> 'userId' = CAST(%d AS TEXT)
            AND text LIKE '%%%s%%'
            """, tableName, userId, query);

        return getDocuments(sql)
            .stream()
            .toList();
    }

    /**
     * Get documents from the database
     * @param sql the sql query
     * @return the list of documents
     */
    @NotNull
    private List<DocumentDTO> getDocuments(String sql) {
        return jdbcTemplate.query(sql, (rs, na) -> DocumentDTO.builder()
            .id(rs.getString("embedding_id"))
            .text(rs.getString("text"))
            .metadata(rs.getString("metadata"))
            .build());
    }

    @NotNull
    private List<DocumentDTO> getDocumentsWithEmbeddings(String sql) {
        return jdbcTemplate.query(sql, (rs, na) -> DocumentDTO.builder()
            .id(rs.getString("embedding_id"))
            .text(rs.getString("text"))
            .metadata(rs.getString("metadata"))
            .embedding(rs.getString("embedding"))
            .build());
    }
}
