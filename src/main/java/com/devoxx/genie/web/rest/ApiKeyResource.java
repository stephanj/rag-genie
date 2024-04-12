package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.domain.UserAPIKey;
import com.devoxx.genie.service.ApiKeyService;
import com.devoxx.genie.service.dto.UserAPIKeyDTO;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiKeyResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyResource.class);

    private final UserService userService;
    private final ApiKeyService apiKeyService;

    public ApiKeyResource(UserService userService,
                          ApiKeyService apiKeyService) {
        this.userService = userService;
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/api-keys")
    public ResponseEntity<Object> postApiKey(@RequestBody UserAPIKeyDTO userAPIKeyDTO) {
        LOGGER.debug("REST request to post a new API key");

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException("USER_NOT_FOUND", "USER", "usernotfound"));

        UserAPIKey savedEntity = apiKeyService.save(user, userAPIKeyDTO);
        if (savedEntity != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api-keys : get all the User API keys.
     *
     * @return list of api keys
     */
    @GetMapping("/api-keys")
    public ResponseEntity<List<UserAPIKeyDTO>> getUserApiKeys() {
        LOGGER.debug("REST request to get all API keys");
        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException("USER_NOT_FOUND", "USER", "usernotfound"));

        List<UserAPIKeyDTO> userAPIKeyDTOS = apiKeyService.findAllByUserId(user.getId());
        return ResponseEntity.ok(userAPIKeyDTOS);
    }

    /**
     * GET /api-keys/modelTypeName: validate if API key is present for the provided model type name.
     * @param modelTypeName the model type name
     * @return list of api keys
     */
    @GetMapping("/api-keys/{modelTypeName}")
    public ResponseEntity<List<UserAPIKeyDTO>> hasApiKey(@PathVariable String modelTypeName) {
        LOGGER.debug("REST verify if API key exists for model type name {}", modelTypeName);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException("USER_NOT_FOUND", "USER", "usernotfound"));

        Optional<UserAPIKeyDTO> first = apiKeyService.findAllByUserId(user.getId())
            .stream()
            .filter(apiKey -> apiKey.getLanguageType().name().equals(modelTypeName)).findFirst();

        if (first.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api-keys/:id: delete the "id" apiKey.
     *
     * @param id the id of the apiKey to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/api-keys/{id}")
    public ResponseEntity<Object> deleteApiKey(@PathVariable Long id) {
        LOGGER.debug("REST request to post a new API key");

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException("USER_NOT_FOUND", "USER", "usernotfound"));

        if (apiKeyService.deleteById(user.getId(), id).isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
