package com.devoxx.genie.service.chatmodel.groq;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class GroqChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        return OpenAiChatModel.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .apiKey(getApiKey())
            .modelName(chatModelDTO.getLanguageModelDTO().getName())
            .maxRetries(chatModelDTO.getMaxRetries())
            .maxTokens(chatModelDTO.getMaxTokens())
            .temperature(chatModelDTO.getTemperature())
            .timeout(Duration.ofSeconds(chatModelDTO.getTimeout()))
            .topP(chatModelDTO.getTopP())
            .build();
    }
}
