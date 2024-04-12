package com.devoxx.genie.service.chatmodel.mistral;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;

import java.time.Duration;

public class MistralChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        return MistralAiChatModel.builder()
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
