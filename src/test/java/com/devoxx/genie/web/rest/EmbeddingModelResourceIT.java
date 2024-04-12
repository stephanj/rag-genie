package com.devoxx.genie.web.rest;

import com.devoxx.genie.web.GenieWebTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GenieWebTest
class EmbeddingModelResourceIT extends AbstractMVCContextIT {

    @Test
    void getEmbeddingModels_isOk() throws Exception {
        mvc.perform(
                get(API_EMBEDDING_MODEL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles("ADMIN")))
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
