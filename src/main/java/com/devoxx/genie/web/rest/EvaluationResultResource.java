package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.EvaluationResultService;
import com.devoxx.genie.service.dto.EvaluationResultDTO;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devoxx.genie.web.rest.ContentResource.USER_NOT_FOUND;
import static com.devoxx.genie.web.rest.ContentResource.USER_NOT_FOUND_CODE;

@RestController
@RequestMapping("/api")
public class EvaluationResultResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationResultResource.class);

    private final EvaluationResultService evaluationResultService;
    private final UserService userService;

    public EvaluationResultResource(EvaluationResultService evaluationResultService,
                                    UserService userService) {
        this.evaluationResultService = evaluationResultService;
        this.userService = userService;
    }

    @GetMapping("/evaluation-result")
    public ResponseEntity<List<EvaluationResultDTO>> getAll(Pageable pageable) {
        LOGGER.debug("Get all evaluations");

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        List<EvaluationResultDTO> entries = evaluationResultService.findAllByUserId(pageable, user.getId()).getContent();
        return ResponseEntity.ok().body(entries);
    }

    @GetMapping("/evaluation-result/{id}")
    public ResponseEntity<EvaluationResultDTO> getById(@PathVariable Long id) {
        LOGGER.debug("Get evaluation result by id: {}", id);

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        return evaluationResultService.findByIdAndUserId(id, user.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/evaluation-result/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        LOGGER.debug("Delete evaluation result with id: {}", id);

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        evaluationResultService.delete(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/evaluation-result/all")
    public ResponseEntity<Object> deleteAll() {
        LOGGER.debug("Delete all evaluation result");

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        evaluationResultService.deleteAllForUserId(user.getId());
        return ResponseEntity.ok().build();
    }
}
