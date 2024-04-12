package com.devoxx.genie.web.rest;

import com.devoxx.genie.security.AuthoritiesConstants;
import com.devoxx.genie.service.LanguageModelService;
import com.devoxx.genie.service.OllamaModelHandlerService;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import com.devoxx.genie.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LanguageModelResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageModelResource.class);

    private final LanguageModelService languageModelService;
    private final OllamaModelHandlerService ollamaModelHandlerService;

    public LanguageModelResource(LanguageModelService languageModelService,
                                 OllamaModelHandlerService ollamaModelHandlerService) {
        this.languageModelService = languageModelService;
        this.ollamaModelHandlerService = ollamaModelHandlerService;
    }

    /**
     * POST /lang-model: Create new language model
     *
     * @param languageModelDTO the language model to upload
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping("/lang-model")
    public ResponseEntity<LanguageModelDTO> createLanguageModel(@RequestBody LanguageModelDTO languageModelDTO) {
        LOGGER.debug("Adding URL language Model: {}", languageModelDTO);
        LanguageModelDTO savedModel = languageModelService.save(languageModelDTO);
        return ResponseEntity.ok().body(savedModel);
    }

    /**
     * PUT /lang-model: Update language model
     *
     * @param languageModelDTO the language model to upload
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PutMapping("/lang-model")
    public ResponseEntity<LanguageModelDTO> updateLanguageModel(@RequestBody LanguageModelDTO languageModelDTO) {
        LOGGER.debug("Adding URL language Model: {}", languageModelDTO);
        LanguageModelDTO savedModel = languageModelService.save(languageModelDTO);
        return ResponseEntity.ok().body(savedModel);
    }

    @GetMapping("/lang-model")
    public ResponseEntity<List<LanguageModelDTO>> getLanguageModels(Pageable pageable) {
        LOGGER.debug("REST request to get supported LLMs");
        Page<LanguageModelDTO> page = languageModelService.findAll(pageable);
        var headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/lang-model");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/lang-model/{id}")
    public ResponseEntity<LanguageModelDTO> getLanguageModelById(@PathVariable Long id) {
        LOGGER.debug("REST request to get LLM by id: {}", id);
        LanguageModelDTO model = languageModelService.findById(id);
        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/lang-model/ollama")
    public ResponseEntity<List<LanguageModelDTO>> getOllamaModels() {
        LOGGER.debug("REST request to get Ollam models");
        try {
            ollamaModelHandlerService.findOllamaModels();
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/lang-model/{id}")
    public ResponseEntity<Void> deleteLanguageModel(@PathVariable Long id) {
        LOGGER.debug("Deleting language model: {}", id);
        languageModelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
