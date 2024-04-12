package com.devoxx.genie.service.chatmodel.lmstudio;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LMStudioChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        String baseUrl = "http://localhost:1234/v1";
        return LocalAiChatModel.builder()
            .baseUrl(baseUrl)
            .modelName(chatModelDTO.getLanguageModelDTO().getName())
            .temperature(chatModelDTO.getTemperature())
            .topP(chatModelDTO.getTopP())
            .maxTokens(chatModelDTO.getMaxTokens())
            .maxRetries(chatModelDTO.getMaxRetries())
            .timeout(Duration.ofSeconds(chatModelDTO.getTimeout()))
            .build();
    }
}
