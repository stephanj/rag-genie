package com.devoxx.genie.web.rest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentResourceIT extends AbstractMVCContextIT {

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER"})
    void getContent_isOk(String role) throws Exception {

        mvc.perform(
                get("/api/content")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andExpect(status().isOk())
            .andDo(print());
    }
}
