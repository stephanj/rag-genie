package com.devoxx.genie.service.chatmodel.openai;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class OpenAIChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        return OpenAiChatModel.builder()
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
