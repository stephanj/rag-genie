package com.devoxx.genie.web.rest;

import com.devoxx.genie.web.GenieWebTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GenieWebTest
class EvaluationResourceIT extends AbstractMVCContextIT {

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void getEvaluations_isOk(String role) throws Exception {
        mvc.perform(
                get(API_EVALUATION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(jsonPath("[*].id").value(notNullValue()))
            .andExpect(jsonPath("[*].name").value(notNullValue()))
            .andExpect(jsonPath("[*].question").value(notNullValue()))
            .andExpect(jsonPath("[*].answer").value(notNullValue()))
            .andExpect(jsonPath("[*].keywords").value(notNullValue()))
            .andExpect(jsonPath("[*].userId").value(notNullValue()))
            .andExpect(status().isOk());
    }
}
