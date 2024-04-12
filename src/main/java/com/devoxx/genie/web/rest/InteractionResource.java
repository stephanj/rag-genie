package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.InteractionService;
import com.devoxx.genie.service.dto.InteractionDTO;
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
public class InteractionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionResource.class);

    private final InteractionService interactionService;
    private final UserService userService;

    public InteractionResource(InteractionService interactionService,
                               UserService userService) {
        this.interactionService = interactionService;
        this.userService = userService;
    }

    @GetMapping("/interaction")
    public ResponseEntity<List<InteractionDTO>> getAll(@RequestParam(required = false) Long filterLanguageModelId,
                                                       Pageable pageable) {
        LOGGER.debug("Get all interactions with optional filter {}", filterLanguageModelId);

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        if (filterLanguageModelId == null) {
            return ResponseEntity.ok(interactionService.findAllByUserId(pageable, user.getId()).getContent());
        }

        List<InteractionDTO> list = interactionService.findAllByUserId(Pageable.unpaged(), user.getId()).getContent().stream()
            .filter(interaction -> interaction.getLanguageModel().getId().equals(filterLanguageModelId))
            .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/interaction/{id}")
    public ResponseEntity<InteractionDTO> getById(@PathVariable Long id) {
        LOGGER.debug("Get interaction by id: {}", id);

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        return interactionService.findByIdAndUserId(id, user.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/interaction/{id}")
    public ResponseEntity<List<InteractionDTO>> delete(@PathVariable Long id) {
        LOGGER.debug("Delete interaction by id: {}", id);

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        interactionService.delete(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/interaction/all")
    public ResponseEntity<Object> deleteAll() {
        LOGGER.debug("Delete all user interactions");

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        interactionService.deleteAllForUserId(user.getId());
        return ResponseEntity.ok().build();
    }
}
