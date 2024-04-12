package com.devoxx.genie.service;

import com.devoxx.genie.domain.LanguageModel;
import com.devoxx.genie.repository.LanguageModelRepository;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import com.devoxx.genie.service.mapper.LanguageModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for managing LanguageModel.
 */
@Service
public class LanguageModelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageModelService.class);
    private final LanguageModelRepository languageModelRepository;
    private final LanguageModelMapper languageModelMapper;

    public LanguageModelService(LanguageModelRepository languageModelRepository,
                                LanguageModelMapper languageModelMapper) {
        this.languageModelRepository = languageModelRepository;
        this.languageModelMapper = languageModelMapper;
    }

    /**
     * Save a languageModel.
     * @param languageModelDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public LanguageModelDTO save(LanguageModelDTO languageModelDTO) {
        LOGGER.debug("Request to save LanguageModel : {}", languageModelDTO);
        LanguageModel languageModel = languageModelMapper.toEntity(languageModelDTO);
        LanguageModel savedLanguageModel = languageModelRepository.save(languageModel);
        return languageModelMapper.toDto(savedLanguageModel);
    }

    /**
     * Find a languageModel by id.
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public LanguageModelDTO findById(Long id) {
        LOGGER.debug("Request to get LanguageModel : {}", id);
        return languageModelRepository.findById(id)
            .map(languageModelMapper::toDto)
            .orElseThrow(() -> new RuntimeException("LanguageModel not found"));
    }

    /**
     * Get all the languageModels.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LanguageModelDTO> findAll(Pageable pageable) {
        LOGGER.debug("Request to get all LanguageModels");
        return languageModelRepository.findAll(pageable)
            .map(languageModelMapper::toDto);
    }

    /**
     * Find a languageModel by name.
     * @param name the name of the entity.
     * @return the related LanguageModelDTO.
     */
    @Transactional(readOnly = true)
    public Optional<LanguageModelDTO> findByName(String name) {
        LOGGER.debug("Request to get all LanguageModels by name");
        return languageModelRepository.findByName(name)
            .map(languageModelMapper::toDto);
    }

    /**
     * Delete the languageModel by id.
     * @param id the id of the entity.
     * @return true if the entity was deleted, false otherwise.
     */
    @Transactional
    public Optional<Boolean> deleteById(Long id) {
        LOGGER.debug("Delete language model by id : {}", id);
        return languageModelRepository.findById(id)
            .map(key -> {
                languageModelRepository.deleteById(key.getId());
                return true;
            });
    }
}
