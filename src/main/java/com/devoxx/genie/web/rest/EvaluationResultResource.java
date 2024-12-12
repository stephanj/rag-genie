package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.EvaluationResultService;
import com.devoxx.genie.service.dto.EvaluationResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;

@RestController
@RequestMapping("/api")
public class EvaluationResultResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationResultResource.class);

    private final EvaluationResultService evaluationResultService;

    public EvaluationResultResource(EvaluationResultService evaluationResultService) {
        this.evaluationResultService = evaluationResultService;
    }

    @GetMapping("/evaluation-result")
    public ResponseEntity<List<EvaluationResultDTO>> getAll(Pageable pageable) {
        LOGGER.debug("Get all evaluations");

        List<EvaluationResultDTO> entries = evaluationResultService.findAllByUserId(pageable, HARD_CODED_USER_ID).getContent();
        return ResponseEntity.ok().body(entries);
    }

    @GetMapping("/evaluation-result/{id}")
    public ResponseEntity<EvaluationResultDTO> getById(@PathVariable Long id) {
        LOGGER.debug("Get evaluation result by id: {}", id);

        return evaluationResultService.findByIdAndUserId(id, HARD_CODED_USER_ID)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/evaluation-result/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        LOGGER.debug("Delete evaluation result with id: {}", id);

        evaluationResultService.delete(id, HARD_CODED_USER_ID);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/evaluation-result/all")
    public ResponseEntity<Object> deleteAll() {
        LOGGER.debug("Delete all evaluation result");

        evaluationResultService.deleteAllForUserId(HARD_CODED_USER_ID);
        return ResponseEntity.ok().build();
    }
}
