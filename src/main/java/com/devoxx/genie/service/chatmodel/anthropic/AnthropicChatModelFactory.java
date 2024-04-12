package com.devoxx.genie.service.chatmodel.anthropic;

import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatLanguageModel;

import static dev.langchain4j.model.anthropic.AnthropicChatModelName.*;

public class AnthropicChatModelFactory extends AbstractChatModelFactory {

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {
        AnthropicChatModelName modelName = getType(chatModelDTO.getLanguageModelDTO().getName());
        return AnthropicChatModel.builder()
            .apiKey(getApiKey())
            .temperature(chatModelDTO.getTemperature())
            .topP(chatModelDTO.getTopP())
            .maxTokens(chatModelDTO.getMaxTokens())
            .modelName(modelName)
            .maxRetries(chatModelDTO.getMaxRetries())
            .build();
    }

    private AnthropicChatModelName getType(String languageModelName) {
        return switch (languageModelName) {
            case "Claude 2.0" -> CLAUDE_2;
            case "Claude 2.1" -> CLAUDE_2_1;
            case "Claude 3 Sonnet" -> CLAUDE_3_SONNET_20240229;
            case "Claude 3 Opus" -> CLAUDE_3_OPUS_20240229;
            default -> CLAUDE_INSTANT_1_2;
        };
    }
}
