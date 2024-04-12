package com.devoxx.genie.service.summary;

import com.devoxx.genie.service.chatmodel.ChatModelFactory;
import com.devoxx.genie.service.chatmodel.ChatModelFactoryCreator;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {
    private final ChatModelFactoryCreator chatModelFactoryCreator;

    public SummaryService(ChatModelFactoryCreator chatModelFactoryCreator) {
        this.chatModelFactoryCreator = chatModelFactoryCreator;
    }

    /**
     * Summarize a chunk of text using the given language model and prompt.
     *
     * @param languageModel the language model to use.
     * @param temperature   the temperature to use.
     * @param prompt        the prompt to use.
     * @return the summarized text.
     */
    public String summarize(LanguageModelDTO languageModel, Double temperature, String prompt) {
        ChatLanguageModel chatModel = getChatModel(languageModel, temperature);
        return chatModel.generate(prompt);
    }

    /**
     * Summarize a chunk of text using the given language model.
     *
     * @param modelDTO the language model to use.
     * @return the summarized text.
     */
    private ChatLanguageModel getChatModel(LanguageModelDTO modelDTO, Double temperature) {

        // TODO Pass topP, maxOutputTokens, maxRetries, and timeout as parameters

        ChatModelFactory factory = chatModelFactoryCreator.getFactory(modelDTO.getModelType());

        if (modelDTO.getBaseUrl() != null && !modelDTO.getBaseUrl().isBlank()) {
            factory.setBaseUrl(modelDTO.getBaseUrl());
        } else {
            String baseUrl = factory.getBaseUrlByType(modelDTO.getModelType());
            factory.setBaseUrl(baseUrl);
        }

        return factory.createChatModel(ChatModelDTO.builder()
            .languageModelDTO(modelDTO)
            .temperature(temperature)
            .build());
    }
}
