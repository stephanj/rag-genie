package com.devoxx.genie.service;

import com.devoxx.genie.domain.EvaluationResult;
import com.devoxx.genie.repository.EvaluationResultRepository;
import com.devoxx.genie.service.dto.EvaluationResultDTO;
import com.devoxx.genie.service.mapper.EvaluationResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EvaluationResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationResultService.class);
    private final EvaluationResultRepository evaluationEntryRepository;
    private final EvaluationResultMapper evaluationResultMapper;

    public EvaluationResultService(EvaluationResultRepository evaluationEntryRepository,
                                   EvaluationResultMapper evaluationResultMapper) {
        this.evaluationEntryRepository = evaluationEntryRepository;
        this.evaluationResultMapper = evaluationResultMapper;
    }

    @Transactional
    public EvaluationResultDTO save(EvaluationResultDTO evaluationEntryDTO) {
        LOGGER.debug("Request to save evaluationEntry : {}", evaluationEntryDTO);
        EvaluationResult evaluationEntry = evaluationResultMapper.toEntity(evaluationEntryDTO);
        EvaluationResult savedEvaluationEntry = evaluationEntryRepository.save(evaluationEntry);
        return evaluationResultMapper.toDto(savedEvaluationEntry);
    }

    @Transactional(readOnly = true)
    public Page<EvaluationResultDTO> findAllByUserId(Pageable pageable, Long userId) {
        LOGGER.debug("Request to get all EvaluationEntries for user with id {}", userId);
        return evaluationEntryRepository.findAllByUserId(pageable, userId)
            .map(evaluationResultMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<EvaluationResultDTO> findByIdAndUserId(Long id, Long userId) {
        LOGGER.debug("Request to get EvaluationEntry : {} for user with id {}", id, userId);
        return evaluationEntryRepository.findByIdAndUserId(id, userId)
            .map(evaluationResultMapper::toDto);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        LOGGER.debug("Request to delete EvaluationEntry : {} for user with id {}", id, userId);
        evaluationEntryRepository.findByIdAndUserId(id, userId).ifPresent(evaluationEntryRepository::delete);
    }

    @Transactional
    public void deleteAllForUserId(Long userId) {
        LOGGER.debug("Request to delete all EvaluationEntry for user with id: {}", userId);
        evaluationEntryRepository.findAllByUserId(Pageable.unpaged(), userId)
            .forEach(item -> evaluationEntryRepository.deleteById(item.getId()));
    }
}
