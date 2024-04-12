package com.devoxx.genie.service;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.domain.UserAPIKey;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.repository.UserRepository;
import com.devoxx.genie.service.dto.UserAPIKeyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@GenieServiceTest
class ApiKeyServiceIT {

    @Autowired
    ApiKeyService apiKeyService;

    @Autowired
    UserRepository userRepository;

    @Test
    void testSave() {
        User user = userRepository.findAll().getFirst();
        UserAPIKeyDTO userAPIKeyDTO = new UserAPIKeyDTO();
        userAPIKeyDTO.setApiKey("1234567890");
        userAPIKeyDTO.setApiKey("1234567890");
        userAPIKeyDTO.setName("test");
        userAPIKeyDTO.setLanguageType(LanguageModelType.OLLAMA);
        userAPIKeyDTO.setUserId(user.getId());

        UserAPIKey savedApiKey = apiKeyService.save(user, userAPIKeyDTO);
        assertThat(savedApiKey.getId()).isNotNull();
    }

    @Test
    void testFindAllByUserId() {
        User user = userRepository.findAll().getFirst();
        List<UserAPIKeyDTO> allByUserId = apiKeyService.findAllByUserId(user.getId());
        int currentSize = allByUserId.size();

        UserAPIKeyDTO userAPIKeyDTO = new UserAPIKeyDTO();
        userAPIKeyDTO.setApiKey("1234567890");
        userAPIKeyDTO.setName("test");
        userAPIKeyDTO.setLanguageType(LanguageModelType.OLLAMA);
        userAPIKeyDTO.setUserId(user.getId());

        UserAPIKey savedApiKey = apiKeyService.save(user, userAPIKeyDTO);
        assertThat(savedApiKey.getId()).isNotNull();

        List<UserAPIKeyDTO> keys = apiKeyService.findAllByUserId(user.getId());
        assertThat(keys.size()).isGreaterThan(currentSize);
    }

    @Test
    void testFindAllByUserIdAndProvider() {
        User user = userRepository.findAll().getFirst();

        // Create Ollama key
        UserAPIKeyDTO userAPIKeyDTO = new UserAPIKeyDTO();
        userAPIKeyDTO.setApiKey("1234567890");
        userAPIKeyDTO.setName("test");
        userAPIKeyDTO.setLanguageType(LanguageModelType.OLLAMA);
        userAPIKeyDTO.setUserId(user.getId());

        UserAPIKey savedApiKey = apiKeyService.save(user, userAPIKeyDTO);
        assertThat(savedApiKey.getId()).isNotNull();

        boolean ollamaKeyPresent =
            apiKeyService.isLanguageModelKeyDefined(user.getId(), LanguageModelType.OLLAMA);

        assertThat(ollamaKeyPresent).isTrue();

        boolean geminiKeyPresent =
            apiKeyService.isLanguageModelKeyDefined(user.getId(), LanguageModelType.GROQ);

        assertThat(geminiKeyPresent).isFalse();
    }

    @Test
    void testDeleteById_notOk() {
        User user = userRepository.findAll().getFirst();
        apiKeyService.deleteById(user.getId(), 123L)
            .ifPresentOrElse(aBoolean -> assertThat(true).isFalse(),
                () -> assertThat(true).isTrue());
    }

    @Test
    void testDeleteById() {
        User user = userRepository.findAll().getFirst();

        UserAPIKeyDTO userAPIKeyDTO = new UserAPIKeyDTO();
        userAPIKeyDTO.setApiKey("1234567890");
        userAPIKeyDTO.setName("test");
        userAPIKeyDTO.setLanguageType(LanguageModelType.OLLAMA);
        userAPIKeyDTO.setUserId(user.getId());

        UserAPIKey savedUserApiKey = apiKeyService.save(user, userAPIKeyDTO);

        apiKeyService.deleteById(user.getId(), savedUserApiKey.getId())
            .ifPresentOrElse(aBoolean -> assertThat(true).isTrue(),
                () -> assertThat(false).isTrue());
    }

    @Test
    void testGetApiKeyForUserIdAndLanguageModelType() {
        String apiKey = "123456789012345678901234567890";
        User user = userRepository.findAll().getFirst();
        UserAPIKeyDTO userAPIKeyDTO = new UserAPIKeyDTO();
        userAPIKeyDTO.setApiKey(apiKey);
        userAPIKeyDTO.setName("test");
        userAPIKeyDTO.setLanguageType(LanguageModelType.OLLAMA);
        userAPIKeyDTO.setUserId(user.getId());
        UserAPIKey savedUserApiKey = apiKeyService.save(user, userAPIKeyDTO);

        apiKeyService.getApiKeyForUserIdAndLanguageModelType(user.getId(), LanguageModelType.OLLAMA)
            .ifPresentOrElse(key -> assertThat(key).isEqualTo(apiKey),
                () -> assertThat(true).isTrue());
    }
}
