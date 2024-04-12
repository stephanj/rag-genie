package com.devoxx.genie.service;

import com.devoxx.genie.service.chatmodel.ChatModelFactory;
import com.devoxx.genie.service.chatmodel.ChatModelFactoryCreator;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatModelService.class);

    private final ApiKeyService apiKeyService;
    private final ChatModelFactoryCreator chatModelFactoryCreator;

    public ChatModelService(ApiKeyService apiKeyService,
                            ChatModelFactoryCreator chatModelFactoryCreator) {
        this.apiKeyService = apiKeyService;
        this.chatModelFactoryCreator = chatModelFactoryCreator;
    }

    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {

        LOGGER.debug("CreateChatModel with params: {}", chatModelDTO);

        if (chatModelDTO.getLanguageModelDTO() == null) {
            throw new BadRequestAlertException("Language model not found", "languageModel", "notfound");
        }

        if (Boolean.TRUE.equals(chatModelDTO.getLanguageModelDTO().getApiKeyRequired())) {
            ChatModelFactory factory = getChatModelFactory(chatModelDTO);
            return factory.createChatModel(chatModelDTO);
        } else {
            LanguageModelDTO languageModelDTO = chatModelDTO.getLanguageModelDTO();
            ChatModelFactory factory = chatModelFactoryCreator.getFactory(languageModelDTO.getModelType());
            if (languageModelDTO.getBaseUrl() != null && !languageModelDTO.getBaseUrl().isBlank()) {
                factory.setBaseUrl(languageModelDTO.getBaseUrl());
            } else {
                String baseUrl = factory.getBaseUrlByType(languageModelDTO.getModelType());
                factory.setBaseUrl(baseUrl);
            }
            return factory.createChatModel(chatModelDTO);
        }
    }

    @NotNull
    private ChatModelFactory getChatModelFactory(ChatModelDTO chatModelDTO) {

        String apiKey = apiKeyService.getApiKeyForUserIdAndLanguageModelType(chatModelDTO)
            .orElseThrow(() -> new BadRequestAlertException("API key not found", "apiKey", "notfound"));

        ChatModelFactory factory = chatModelFactoryCreator.getFactory(chatModelDTO.getLanguageModelDTO().getModelType());
        factory.setApiKey(apiKey);
        return factory;
    }
}
