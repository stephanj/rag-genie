package com.devoxx.genie.service.chatmodel.ollama;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OllamaChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        return OllamaChatModel.builder()
            .baseUrl(getBaseUrl())
            .modelName(chatModelDTO.getLanguageModelDTO().getName())
            .temperature(chatModelDTO.getTemperature())
            .topP(chatModelDTO.getTopP())
            .maxRetries(chatModelDTO.getMaxRetries())
            .timeout(Duration.ofSeconds(chatModelDTO.getTimeout()))
            .build();
    }
}
