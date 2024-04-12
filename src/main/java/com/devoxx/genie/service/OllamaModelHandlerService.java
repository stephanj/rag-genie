package com.devoxx.genie.service;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import com.devoxx.genie.service.dto.OllamaModelDTO;
import com.devoxx.genie.service.dto.OllamaModelEntryDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class OllamaModelHandlerService {

    private final LanguageModelService languageModelService;

    public OllamaModelHandlerService(LanguageModelService languageModelService) {
        this.languageModelService = languageModelService;
    }

    public void findOllamaModels() {
        // TODO Use Ollama BaseURL
        OllamaModelDTO ollamaModelDTO =
            new RestTemplate().getForObject("http://localhost:11434/api/tags", OllamaModelDTO.class);
//            new RestTemplate().getForObject("https://ollama.cleverapps.io/api/tags", OllamaModelDTO.class);

        if (ollamaModelDTO == null ||
            ollamaModelDTO.getModels() == null ||
            ollamaModelDTO.getModels().length == 0) {
            throw new RuntimeException("No models found");
        }

        Arrays.stream(ollamaModelDTO.getModels()).forEach(this::createLanguageModelDTO);
    }

    /**
     * Create a LanguageModelDTO from the given OllamaModelEntryDTO
     *
     * @param model the OllamaModelEntryDTO to create the LanguageModelDTO from
     */
    private void createLanguageModelDTO(OllamaModelEntryDTO model) {
        languageModelService.findByName(model.getName())
            .ifPresentOrElse(
                languageModelDTO -> {
                    languageModelDTO.setVersion(model.getModified_at());
                    languageModelService.save(languageModelDTO);
                },
                () -> {
                    LanguageModelDTO languageModelDTO = new LanguageModelDTO();
                    languageModelDTO.setName(model.getName());
                    languageModelDTO.setModelType(LanguageModelType.OLLAMA);
                    languageModelDTO.setVersion(model.getModified_at());
                    languageModelService.save(languageModelDTO);
                }
            );
    }
}
