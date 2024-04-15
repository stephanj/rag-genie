package com.devoxx.genie.service;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@GenieServiceTest
class ChatModelServiceIT {

    @Autowired
    LanguageModelService languageModelService;

    @Autowired
    ChatModelService chatModelService;

    @MockBean
    ApiKeyService apiKeyService;

    @Test
    void testCreateChatModel_withApiKey() {
        LanguageModelDTO languageModelDTO =
            languageModelService
                .findAll(Pageable.unpaged())
                .getContent()
                .stream()
                .filter(LanguageModelDTO::getApiKeyRequired)
                .filter(l -> l.getModelType().equals(LanguageModelType.OPENAI))
                .findFirst()
                .orElseThrow();

        when(apiKeyService.getApiKeyForUserIdAndLanguageModelType(ArgumentMatchers.any()))
            .thenReturn(Optional.of("apiKey"));

        ChatModelDTO chatModelDTO = ChatModelDTO.builder()
            .languageModelDTO(languageModelDTO)
            .build();

        // Test implementation
        ChatLanguageModel chatModel = chatModelService.createChatModel(chatModelDTO);

        verify(apiKeyService).getApiKeyForUserIdAndLanguageModelType(ArgumentMatchers.any());
        assertThat(chatModel).isNotNull();
    }

    @Test
    void testCreateChatModel_withoutApiKey() {
        LanguageModelDTO languageModelDTO =
            languageModelService
                .findAll(Pageable.unpaged())
                .getContent()
                .stream()
                .filter(l -> !l.getApiKeyRequired())
                .findFirst()
                .orElseThrow();

        ChatModelDTO chatModelDTO = ChatModelDTO.builder()
            .languageModelDTO(languageModelDTO)
            .build();

        // Test implementation
        ChatLanguageModel chatModel = chatModelService.createChatModel(chatModelDTO);

        assertThat(chatModel).isNotNull();
    }
}
