package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.QuestionService;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.InteractionDTO;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import dev.ai4j.openai4j.OpenAiHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;

@RestController
@RequestMapping("/api")
public class QuestionResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionResource.class);

    private final QuestionService questionService;

    public QuestionResource(final QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Get answer to question.
     *
     * @param chatModelDTO the chat model dto
     * @return the response entity
     */
    @PostMapping(value = "/question", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> getAnswerToQuestion(@RequestBody @NotNull ChatModelDTO chatModelDTO) {

        LOGGER.debug("Received question: {}", chatModelDTO);

        chatModelDTO.setUserId(HARD_CODED_USER_ID);

        try {
            InteractionDTO interactionDTO =
                questionService.generateResponseFromSimilarityOrAllContent(chatModelDTO);

            if (interactionDTO == null) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(interactionDTO);

        } catch (OpenAiHttpException e) {
            LOGGER.error("Error generating response from documents", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
