package com.devoxx.genie.service;

import com.devoxx.genie.domain.Evaluation;
import com.devoxx.genie.repository.EvaluationRepository;
import com.devoxx.genie.service.dto.EvaluationDTO;
import com.devoxx.genie.service.mapper.EvaluationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationService.class);
    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             EvaluationMapper evaluationMapper) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
    }

    @Transactional
    public EvaluationDTO save(EvaluationDTO evaluationDTO) {
        LOGGER.debug("Request to save evaluation : {}", evaluationDTO);

        Evaluation evaluation = evaluationMapper.toEntity(evaluationDTO);
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return evaluationMapper.toDto(savedEvaluation);
    }

    @Transactional(readOnly = true)
    public Page<EvaluationDTO> findAllByUserId(Long userId, Pageable pageable) {
        LOGGER.debug("Request to get all Evaluations");
        return evaluationRepository.findAllByUserId(userId, pageable)
            .map(evaluationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<EvaluationDTO> findAllById(List<Long> ids) {
        LOGGER.debug("Request to get all Evaluations");
        return evaluationRepository.findAllById(ids)
            .stream()
            .map(evaluationMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public EvaluationDTO findById(Long id) {
        LOGGER.debug("Request to get Evaluation : {}", id);
        return evaluationRepository.findById(id)
            .map(evaluationMapper::toDto)
            .orElseThrow(() -> new RuntimeException("Evaluation not found"));
    }

    @Transactional
    public void deleteByIdForUserId(Long userId, Long id) {
        LOGGER.debug("Request to delete Evaluation : {}", id);
        evaluationRepository.findByIdAndUserId(id, userId)
            .ifPresentOrElse(evaluationRepository::delete, () -> {
                throw new RuntimeException("Evaluation not found");
            });
    }
}
