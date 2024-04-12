package com.devoxx.genie.service.chatmodel.fireworks;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class FireworksChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        return OpenAiChatModel.builder()
            .baseUrl("https://api.fireworks.ai/inference/v1/")
            .apiKey(getApiKey())
            .modelName(chatModelDTO.getLanguageModelDTO().getName())
            .maxRetries(chatModelDTO.getMaxRetries())
            .temperature(chatModelDTO.getTemperature())
            .maxTokens(chatModelDTO.getMaxTokens())
            .timeout(Duration.ofSeconds(chatModelDTO.getTimeout()))
            .topP(chatModelDTO.getTopP())
            .build();
    }
}
