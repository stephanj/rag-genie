package com.devoxx.genie.service.chatmodel;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import dev.langchain4j.model.chat.ChatLanguageModel;

public interface ChatModelFactory {

    /**
     * Create a chat model with the given parameters.
     * @param chatModelDTO the chat model DTO
     * @return the chat model
     */
    ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO);

    void setApiKey(String apiKey);

    String getApiKey();

    void setBaseUrl(String baseUrl);

    String getBaseUrl();

    default String getBaseUrlByType(LanguageModelType modelType) {
        return switch (modelType) {
            case GPT4ALL -> "http://localhost:4891/v1";
            case LMSTUDIO -> "http://localhost:1234/v1";
            case OLLAMA -> "http://localhost:11434";
            // TODO Add MLX LLM, LLAMA.CCP
            default -> throw new BadRequestAlertException("Language model not found", "languageModel", "notfound");
        };
    }
}
