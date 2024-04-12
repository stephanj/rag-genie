package com.devoxx.genie.service;

import com.devoxx.genie.domain.Interaction;
import com.devoxx.genie.repository.InteractionRepository;
import com.devoxx.genie.service.dto.InteractionDTO;
import com.devoxx.genie.service.mapper.InteractionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InteractionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionService.class);
    private final InteractionRepository interactionRepository;
    private final InteractionMapper interactionMapper;

    public InteractionService(InteractionRepository interactionRepository,
                              InteractionMapper interactionMapper) {
        this.interactionRepository = interactionRepository;
        this.interactionMapper = interactionMapper;
    }

    @Transactional
    public InteractionDTO save(InteractionDTO interactionDTO) {
        LOGGER.debug("Request to save interaction : {}", interactionDTO);
        Interaction interaction = interactionMapper.toEntity(interactionDTO);
        Interaction savedInteraction = interactionRepository.save(interaction);
        return interactionMapper.toDto(savedInteraction);
    }

    @Transactional(readOnly = true)
    public Page<InteractionDTO> findAllByUserId(Pageable pageable, Long userId) {
        LOGGER.debug("Request to get all interaction for user with id {}", userId);
        return interactionRepository.findAllByUserId(pageable, userId)
            .map(interactionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<InteractionDTO> findByIdAndUserId(Long id, Long userId) {
        LOGGER.debug("Request to get interaction : {} for user with id {}", id, userId);
        return interactionRepository.findByIdAndUserId(id, userId)
            .map(interactionMapper::toDto);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        LOGGER.debug("Request to delete interaction : {} for user with id {}", id, userId);
        interactionRepository.findByIdAndUserId(id, userId).ifPresent(interactionRepository::delete);
    }

    @Transactional
    public void deleteAllForUserId(Long userId) {
        LOGGER.debug("Request to delete all user interactions for user with id: {}", userId);
        interactionRepository.findAllByUserId(Pageable.unpaged(), userId)
            .forEach(item -> interactionRepository.deleteById(item.getId()));
    }
}
