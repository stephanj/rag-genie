package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.EvaluationLogicService;
import com.devoxx.genie.service.EvaluationService;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.EvaluationDTO;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.devoxx.genie.web.rest.ContentResource.USER_NOT_FOUND;
import static com.devoxx.genie.web.rest.ContentResource.USER_NOT_FOUND_CODE;

@RestController
@RequestMapping("/api")
public class EvaluationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationResource.class);

    private final EvaluationService evaluationService;
    private final EvaluationLogicService evaluationLogicService;
    private final UserService userService;

    public EvaluationResource(EvaluationService evaluationService,
                              EvaluationLogicService evaluationLogicService,
                              UserService userService) {
        this.evaluationService = evaluationService;
        this.evaluationLogicService = evaluationLogicService;
        this.userService = userService;
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

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        evaluationDTO.setUserId(user.getId());

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

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        evaluationDTO.setUserId(user.getId());

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

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        List<EvaluationDTO> entries = evaluationService.findAllByUserId(user.getId(), pageable).getContent();
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

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        EvaluationDTO entry = evaluationService.findById(id);

        if (entry.getUserId().equals(user.getId())) {
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

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        evaluationService.deleteByIdForUserId(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /evaluation/state start evaluation process
     * @param models          the models
     * @param chatModelDTO    the chat model DTO
     * @param evaluations     the evaluations
     * @return nothing
     */
    @PostMapping(path = "/evaluation/start", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<Void> startEvaluations(@RequestParam String models,
                                                 @RequestParam ChatModelDTO chatModelDTO,
                                                 @RequestParam() List<Long> evaluations) {

        LOGGER.debug("Start valuation process: {}", chatModelDTO);

        List<EvaluationDTO> evalEntries = evaluationService.findAllById(evaluations);

        if (evalEntries.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException("User not found", "USER", "usernotfound"));
        chatModelDTO.setUserId(user.getId());

        Arrays.stream(models.split(",")).forEach(modelId ->
            evalEntries.forEach(evaluationItem ->
                evaluationLogicService.evaluate(chatModelDTO, evaluationItem)));

        return ResponseEntity.ok().build();
    }
}
