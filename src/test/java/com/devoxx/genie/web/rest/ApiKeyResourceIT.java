package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.Authority;
import com.devoxx.genie.domain.LanguageModel;
import com.devoxx.genie.domain.User;
import com.devoxx.genie.domain.UserAPIKey;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.repository.LanguageModelRepository;
import com.devoxx.genie.repository.UserApiKeyRepository;
import com.devoxx.genie.repository.UserRepository;
import com.devoxx.genie.service.dto.UserAPIKeyDTO;
import com.devoxx.genie.web.GenieWebTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GenieWebTest
class ApiKeyResourceIT extends AbstractMVCContextIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LanguageModelRepository languageModelRepository;
    @Autowired
    private UserApiKeyRepository userApiKeyRepository;

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void get_ApiKeys_isOk(String role) throws Exception {

        mvc.perform(
                get(API_KEYS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(jsonPath("[*].id").value(notNullValue()))
            .andExpect(jsonPath("[*].name").value(notNullValue()))
            .andExpect(jsonPath("[*].keyMask").value(notNullValue()))
            .andExpect(jsonPath("[*].createdBy").value(notNullValue()))
            .andExpect(jsonPath("[*].createdDate").value(notNullValue()))
            .andExpect(jsonPath("[*].lastModifiedBy").value(notNullValue()))
            .andExpect(jsonPath("[*].lastModifiedDate").value(notNullValue()))
            .andExpect(jsonPath("[*].languageType").value(notNullValue()))
            .andExpect(jsonPath("[*].apiKey").value(notNullValue()))
            .andExpect(jsonPath("[*].lastUsed").value(notNullValue()))
            .andExpect(jsonPath("[*].userId").value(notNullValue()))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void post_ApiKey_isOk(String role) throws Exception {
        User user = userRepository.findAll().getFirst();

        UserAPIKeyDTO userAPIKeyDTO = new UserAPIKeyDTO();
        userAPIKeyDTO.setUserId(user.getId());
        userAPIKeyDTO.setName("test");
        userAPIKeyDTO.setApiKey("123123123");
        userAPIKeyDTO.setLanguageType(LanguageModelType.OLLAMA);

        mvc.perform(
            MockMvcRequestBuilders
                .post( API_KEYS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userAPIKeyDTO))
                    .with(user("admin").password("password").roles(role)))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void get_HasApiKey_notOk(String role) throws Exception {
        LanguageModel languageModel =
            languageModelRepository
                .findAll()
                .stream()
                .filter(LanguageModel::isApiKeyRequired)
                .findFirst().orElseThrow();

        // We have no API keys by default, so 400 expected
        mvc.perform(
            get(API_KEYS + "/" + languageModel.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").password("password").roles(role)))
            .andExpect(status().is4xxClientError());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN" })
    void delete_ApiKey_isOk(String role) throws Exception {
        UserAPIKey userAPIKey = new UserAPIKey();

        // There are only 3 users, so lets iterate over them
        userRepository
            .findAll()
            .forEach(user -> {
                Set<Authority> authorities = user.getAuthorities();
                // check authorities includes role
                if (authorities.stream().anyMatch(authority -> authority.getName().equals("ROLE_"+role.toUpperCase()))) {
                    userAPIKey.setUser(user);
                }
            });

        userAPIKey.setName("test");
        userAPIKey.setKeyMask("t**t");
        userAPIKey.setApiKey("123123123");
        userAPIKey.setLanguageType(LanguageModelType.OLLAMA);

        UserAPIKey savedApiKey = userApiKeyRepository.save(userAPIKey);
        assertThat(savedApiKey).isNotNull();
        assertThat(savedApiKey.getId()).isNotNull();

        mvc.perform(
                delete(API_KEYS + "/" + savedApiKey.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andExpect(status().isOk());

        assertThat(userApiKeyRepository.findById(savedApiKey.getId()).isEmpty());
    }
}
