package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.EvaluationLogicService;
import com.devoxx.genie.service.EvaluationService;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.EvaluationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;

@RestController
@RequestMapping("/api")
public class EvaluationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationResource.class);

    private final EvaluationService evaluationService;
    private final EvaluationLogicService evaluationLogicService;

    public EvaluationResource(EvaluationService evaluationService,
                              EvaluationLogicService evaluationLogicService) {
        this.evaluationService = evaluationService;
        this.evaluationLogicService = evaluationLogicService;
    }

    /**
     * POST /evaluation save evaluation
     *
     * @param evaluationDTO the evaluation entry to save
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping("/evaluation")
    public ResponseEntity<EvaluationDTO> save(@RequestBody EvaluationDTO evaluationDTO) {
        LOGGER.debug("Adding evaluation: {}", evaluationDTO);

        // We hard-code user id because this is an anonymous version of Rag Genie
        evaluationDTO.setUserId(HARD_CODED_USER_ID);

        EvaluationDTO savedEntry = evaluationService.save(evaluationDTO);
        return ResponseEntity.ok().body(savedEntry);
    }

    /**
     * PUT /evaluation update evaluation
     *
     * @param evaluationDTO the evaluation entry to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated content
     */
    @PutMapping("/evaluation")
    public ResponseEntity<EvaluationDTO> update(@RequestBody EvaluationDTO evaluationDTO) {
        LOGGER.debug("Adding evaluation : {}", evaluationDTO);

        // We hard-code user id because this is an anonymous version of Rag Genie
        evaluationDTO.setUserId(HARD_CODED_USER_ID);

        EvaluationDTO savedEntry = evaluationService.save(evaluationDTO);
        return ResponseEntity.ok().body(savedEntry);
    }

    /**
     * GET /evaluation get all evaluations for authenticated user
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body the list of evaluations
     */
    @GetMapping("/evaluation")
    public ResponseEntity<List<EvaluationDTO>> getAll(Pageable pageable) {
        LOGGER.debug("Get all evaluations");

        // We hard-code user id because this is an anonymous version of Rag Genie
        List<EvaluationDTO> entries = evaluationService.findAllByUserId(HARD_CODED_USER_ID, pageable).getContent();

        return ResponseEntity.ok().body(entries);
    }

    /**
     * GET /evaluation/{id} get evaluation by id for authenticated user
     *
     * @param id the evaluation id
     * @return the ResponseEntity with status 200 (OK) and with body the evaluation
     */
    @GetMapping("/evaluation/{id}")
    public ResponseEntity<EvaluationDTO> getById(@PathVariable Long id) {
        LOGGER.debug("Get all evaluations");

        // We hard-code user id because this is an anonymous version of Rag Genie
        EvaluationDTO entry = evaluationService.findById(id);

        if (entry.getUserId().equals(HARD_CODED_USER_ID)) {
            return ResponseEntity.ok().body(entry);
        }

        return ResponseEntity.badRequest().build();
    }

    /**
     * DELETE /evaluation/{id} delete evaluation by id for authenticated user
     *
     * @param id the evaluation id to remove
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/evaluation/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        LOGGER.debug("Deleting evaluation: {}", id);

        // We hard-code user id because this is an anonymous version of Rag Genie

        evaluationService.deleteByIdForUserId(HARD_CODED_USER_ID, id);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /evaluation/state start evaluation process
     * @param models the models to use
     * @param temperature the temperature
     * @param embedId the embedding model id
     * @param minScore the minimum score
     * @param maxOutputTokens the maximum output tokens
     * @param size the size
     * @param evaluations the evaluations
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping(path = "/evaluation/start", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<Void> startEvaluations(@RequestParam String models,
                                                 @RequestParam Double temperature,
                                                 @RequestParam Long embedId,
                                                 @RequestParam(defaultValue = "0.7") Double minScore,
                                                 @RequestParam(defaultValue = "4000") Integer maxOutputTokens,
                                                 @RequestParam(defaultValue = "3") Integer size,
                                                 @RequestParam() List<Long> evaluations) {

        LOGGER.debug("Start valuation process: {}", models);

        List<EvaluationDTO> evalEntries = evaluationService.findAllById(evaluations);

        if (evalEntries.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ChatModelDTO chatModelDTO = ChatModelDTO.builder()
            .temperature(temperature)
            .maxTokens(maxOutputTokens)
            .embeddingModelRefId(embedId)
            .minScore(minScore)
            .maxResults(size)
            .embeddingModelRefId(embedId)
            .build();

        Arrays.stream(models.split(",")).forEach(languageModelId ->
            evalEntries.forEach(evaluationItem ->
                evaluationLogicService.evaluate(languageModelId, chatModelDTO, evaluationItem)));

        return ResponseEntity.ok().build();
    }
}
