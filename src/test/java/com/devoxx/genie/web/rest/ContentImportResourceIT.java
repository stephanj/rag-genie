package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.dto.ContentDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import java.time.ZonedDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentImportResourceIT extends AbstractMVCContextIT {

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER"})
    void test_createContent(String role) throws Exception {
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setSource("webpage");
        contentDTO.setCreatedOn(ZonedDateTime.now());
        contentDTO.setSource("https://devoxx.be/faq");
        contentDTO.setName("FAQ");
        contentDTO.setDescription("Testing the Devoxx BE FAQ");
        contentDTO.setValue("FAQ Devoxx Belgium");

        mvc.perform(
                post(API_CONTENT + "/upload-text")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(contentDTO))
                    .with(user("admin").password("password").roles(role)))
            .andExpect(status().isOk())
            .andDo(print());
    }
}
