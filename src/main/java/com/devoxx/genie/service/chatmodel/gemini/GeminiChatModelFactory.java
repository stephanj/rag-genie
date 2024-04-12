package com.devoxx.genie.service.chatmodel.gemini;

import com.devoxx.genie.config.ApplicationProperties;
import com.devoxx.genie.service.chatmodel.AbstractChatModelFactory;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.StorageOptions;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;

import java.io.FileInputStream;
import java.io.IOException;

public class GeminiChatModelFactory extends AbstractChatModelFactory {

    private final ApplicationProperties applicationProperties;

    public GeminiChatModelFactory(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO) {

        String credentials = applicationProperties.getFirebaseSdk().getBase64Credentials();

        try {
            StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(credentials)))
                .build()
                .getService();

            return VertexAiGeminiChatModel.builder()
                .project("ragnet-7ed80")
                .location("us-central1")
                .modelName(chatModelDTO.getLanguageModelDTO().getName())
                .temperature(chatModelDTO.getTemperature().floatValue())
                .topP(chatModelDTO.getTopP().floatValue())
                .maxOutputTokens(chatModelDTO.getMaxTokens())
                .maxRetries(chatModelDTO.getMaxRetries())
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
