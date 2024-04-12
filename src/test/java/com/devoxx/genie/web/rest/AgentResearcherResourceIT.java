package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import com.devoxx.genie.web.GenieWebTest;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GenieWebTest
class AgentResearcherResourceIT extends AbstractMVCContextIT {

    // Work in prgress
    @Ignore
    void post_AgentResearcher_isOk() throws Exception {
        LanguageModelDTO languageModelDTO = new LanguageModelDTO();
        languageModelDTO.setId(1L);

        ChatModelDTO parameterDTO = ChatModelDTO.builder()
            .question("Tell me more about Devoxx")
            .languageModelDTO(languageModelDTO)
            .embeddingModelRefId(1L)
            .build();

        var result = mvc.perform(
                MockMvcRequestBuilders
                    .post(API_AGENT_RESEARCHER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(parameterDTO))
                    .with(user("admin").password("password").roles("ADMIN")))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(result).isNotNull();
    }
}
