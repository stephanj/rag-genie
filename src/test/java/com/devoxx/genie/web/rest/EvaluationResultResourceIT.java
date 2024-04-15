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
class EvaluationResultResourceIT extends AbstractMVCContextIT {

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void getEvaluationResults_isOk(String role) throws Exception {
        mvc.perform(
                get(API_EVALUATION_RESULT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(jsonPath("[*].id").value(notNullValue()))
            .andExpect(jsonPath("[*].createdOn").value(notNullValue()))
            .andExpect(jsonPath("[*].similarityScore").value(notNullValue()))
            .andExpect(jsonPath("[*].answer").value(notNullValue()))
            .andExpect(jsonPath("[*].keywordMatch").value(notNullValue()))
            .andExpect(jsonPath("[*].languageModel").value(notNullValue()))
            .andExpect(jsonPath("[*].embeddingModelReference").value(notNullValue()))
            .andExpect(jsonPath("[*].evaluation").value(notNullValue()))
            .andExpect(jsonPath("[*].durationInMs").value(notNullValue()))
            .andExpect(jsonPath("[*].temperature").value(notNullValue()))
            .andExpect(jsonPath("[*].userId").value(notNullValue()))
            .andExpect(jsonPath("[*].inputTokens").value(notNullValue()))
            .andExpect(jsonPath("[*].outputTokens").value(notNullValue()))
            .andExpect(jsonPath("[*].cost").value(notNullValue()))
            .andExpect(jsonPath("[*].usedDocuments").value(notNullValue()))
            .andExpect(status().isOk());
    }
}
