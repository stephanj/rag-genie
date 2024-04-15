package com.devoxx.genie.service;

import com.devoxx.genie.domain.Content;
import com.devoxx.genie.repository.ContentRepository;
import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.dto.DocumentDTO;
import com.devoxx.genie.service.mapper.ContentMapper;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentService.class);

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;

    public ContentService(ContentRepository contentRepository,
                          ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
    }

    /**
     * Save content.
     * @param contentDTO the content DTO
     * @return the saved content DTO
     */
    @Transactional
    public ContentDTO save(ContentDTO contentDTO) {
        LOGGER.debug("Save content");

        Content content = contentMapper.toEntity(contentDTO);

        // Set estimated tokens (independently of a selected model) for a ballpark figure.
        content.setTokenCount(TokenService.countTokens(content.getValue()));

        Content save = contentRepository.save(content);
        return contentMapper.toDto(save);
    }

    /**
     * Find all content for a user.
     * @param pageable  the pageable
     * @param userId    the user id
     * @param value     the value
     * @param matchMode the match mode
     * @return the page of content DTOs
     */
    @Transactional(readOnly = true)
    public Page<ContentDTO> findAllByUserId(Pageable pageable,
                                            Long userId,
                                            String value,
                                            String matchMode) {
        LOGGER.debug("Find all content for user: {}", userId);

        if (value == null || value.isBlank()) {
            return contentRepository.findAllByUserId(pageable, userId).map(contentMapper::toDto);
        }

        return contentRepository.findAll(
            Specification.where(ContentSpecifications.withNameMatching(value, matchMode))
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId)),
            pageable
        ).map(contentMapper::toDto);
    }

    /**
     * Find all content for a user.
     * @param pageable the pageable
     * @param userId   the user id
     * @return the page of content DTOs
     */
    @Transactional(readOnly = true)
    public Page<ContentDTO> findAllByUserId(Pageable pageable, Long userId) {
        LOGGER.debug("Find all content for user: {}", userId);

        return contentRepository.findAllByUserId(pageable, userId)
            .map(contentMapper::toDto);
    }

    /**
     * Find all content using paging.
     * @param pageable the pageable
     * @return the page of content DTOs
     */
    @Transactional(readOnly = true)
    public Page<ContentDTO> findAll(Pageable pageable) {
        LOGGER.debug("Find all content");

        return contentRepository.findAll(pageable)
            .map(contentMapper::toDto);
    }

    /**
     * Find all content.
     * @return the list of content DTOs
     */
    @Transactional(readOnly = true)
    public List<ContentDTO> findAll() {
        LOGGER.debug("Find all content");

        return contentRepository.findAll()
            .stream()
            .map(contentMapper::toDto)
            .toList();
    }

    /**
     * Find content by id.
     * @param id the id
     * @return the content DTO
     */
    @Transactional(readOnly = true)
    public ContentDTO findById(Long id) {
        LOGGER.debug("Find content by id : {}", id);

        return contentRepository.findById(id)
            .map(contentMapper::toDto)
            .orElseThrow(() -> new RuntimeException("Content not found"));
    }

    /**
     * Add content id and name to document DTO.
     * @param documentDTO the document DTO
     * @return the document DTO
     */
    @Transactional(readOnly = true)
    public DocumentDTO addContentInfo(DocumentDTO documentDTO) {
        String metaData = documentDTO.getMetadata();
        JsonObject asJsonObject = JsonParser.parseString(metaData).getAsJsonObject();
        String contentId = asJsonObject.get("contentId").getAsString();
        ContentDTO byId = findById(Long.parseLong(contentId));
        documentDTO.setContentId(contentId);
        documentDTO.setContentName(byId.getName());
        return documentDTO;
    }

    /**
     * Find content by name.
     * @param name the name
     * @return the content DTO
     */
    public Optional<ContentDTO> findByName(String name) {
        LOGGER.debug("Find content by name : {}", name);
        return contentRepository.findByName(name)
            .map(contentMapper::toDto);
    }

    /**
     * Delete content by id.
     * @param id the id
     * @return Optional true if the content was deleted, false otherwise
     */
    @Transactional
    public Optional<Boolean> deleteById(Long id) {
        LOGGER.debug("Delete content by id : {}", id);
        return contentRepository.findById(id)
            .map(key -> {
                contentRepository.deleteById(key.getId());
                return true;
            });
    }

    /**
     * Count the total tokens for a user.
     * @param userId the user id
     * @return the total tokens
     */
    @Transactional(readOnly = true)
    public int calcTotalTokensForUser(Long userId) {
        return contentRepository.findAll()
            .stream()
            .filter(content -> content.getUser().getId().equals(userId))
            .mapToInt(Content::getTokenCount)
            .sum();
    }
}
