package com.devoxx.genie.service.chatmodel.gpt4all;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;

import java.time.Duration;

public class GPT4AllChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        return LocalAiChatModel.builder()
            .baseUrl(getBaseUrl())
            .modelName(chatModelDTO.getLanguageModelDTO().getName())
            .maxRetries(chatModelDTO.getMaxRetries())
            .maxTokens(chatModelDTO.getMaxTokens())
            .temperature(chatModelDTO.getTemperature())
            .timeout(Duration.ofSeconds(chatModelDTO.getTimeout()))
            .topP(chatModelDTO.getTopP())
            .build();
    }
}
