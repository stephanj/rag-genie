package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.InteractionService;
import com.devoxx.genie.service.dto.InteractionDTO;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;
import static com.devoxx.genie.web.rest.ContentResource.USER_NOT_FOUND;
import static com.devoxx.genie.web.rest.ContentResource.USER_NOT_FOUND_CODE;

@RestController
@RequestMapping("/api")
public class InteractionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionResource.class);

    private final InteractionService interactionService;

    public InteractionResource(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping("/interaction")
    public ResponseEntity<List<InteractionDTO>> getAll(@RequestParam(required = false) Long filterLanguageModelId,
                                                       Pageable pageable) {
        LOGGER.debug("Get all interactions with optional filter {}", filterLanguageModelId);

          if (filterLanguageModelId == null) {
            return ResponseEntity.ok(interactionService.findAllByUserId(pageable, HARD_CODED_USER_ID).getContent());
        }

        List<InteractionDTO> list = interactionService.findAllByUserId(Pageable.unpaged(), HARD_CODED_USER_ID).getContent().stream()
            .filter(interaction -> interaction.getLanguageModel().getId().equals(filterLanguageModelId))
            .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/interaction/{id}")
    public ResponseEntity<InteractionDTO> getById(@PathVariable Long id) {
        LOGGER.debug("Get interaction by id: {}", id);

        return interactionService.findByIdAndUserId(id, HARD_CODED_USER_ID)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/interaction/{id}")
    public ResponseEntity<List<InteractionDTO>> delete(@PathVariable Long id) {
        LOGGER.debug("Delete interaction by id: {}", id);

        interactionService.delete(id, HARD_CODED_USER_ID);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/interaction/all")
    public ResponseEntity<Object> deleteAll() {
        LOGGER.debug("Delete all user interactions");

        interactionService.deleteAllForUserId(HARD_CODED_USER_ID);
        return ResponseEntity.ok().build();
    }
}
