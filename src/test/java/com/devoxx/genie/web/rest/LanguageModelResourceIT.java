package com.devoxx.genie.web.rest;

import com.devoxx.genie.web.GenieWebTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GenieWebTest
class LanguageModelResourceIT extends AbstractMVCContextIT {

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void getLanguageModels_isOk(String role) throws Exception {
        mvc.perform(
                get(API_LANG_MODEL)
                    .contentType(MediaType.APPLICATION_JSON)
            .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(jsonPath("[*].id").value(notNullValue()))
            .andExpect(jsonPath("[*].name").value(notNullValue()))
            .andExpect(jsonPath("[*].modelType").value(notNullValue()))
            .andExpect(jsonPath("[*].description").value(notNullValue()))
            .andExpect(jsonPath("[*].version").value(notNullValue()))
            .andExpect(jsonPath("[*].costInput1M").value(notNullValue()))
            .andExpect(jsonPath("[*].costOutput1M").value(notNullValue()))
            .andExpect(jsonPath("[*].paramSize").value(notNullValue()))
            .andExpect(jsonPath("[*].tokens").value(notNullValue()))
            .andExpect(status().isOk());
    }
}
