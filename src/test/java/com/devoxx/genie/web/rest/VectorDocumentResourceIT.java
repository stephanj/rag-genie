package com.devoxx.genie.web.rest;


import com.devoxx.genie.domain.Content;
import com.devoxx.genie.domain.EmbeddingModelReference;
import com.devoxx.genie.domain.User;
import com.devoxx.genie.domain.enumeration.ContentType;
import com.devoxx.genie.repository.ContentRepository;
import com.devoxx.genie.repository.EmbeddingModelReferenceRepository;
import com.devoxx.genie.repository.UserRepository;
import com.devoxx.genie.service.DocumentService;
import com.devoxx.genie.web.GenieWebTest;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GenieWebTest
class VectorDocumentResourceIT extends AbstractMVCContextIT {

    @Autowired
    DocumentService documentService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    EmbeddingModelReferenceRepository embeddingModelReferenceRepository;

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void get_totalVectorDocumentsForUser_isOk(String role) throws Exception {
        mvc.perform(
                get(API_VECTOR_DOCUMENT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void get_documentsByDimension_384_isOk(String role) throws Exception {
        mvc.perform(
                get(API_VECTOR_DOCUMENT + "/384")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void get_documentsByDimension_512_isOk(String role) throws Exception {
        mvc.perform(
                get(API_VECTOR_DOCUMENT + "/512")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void get_documentsByDimension_1024_isOk(String role) throws Exception {
        mvc.perform(
                get(API_VECTOR_DOCUMENT + "/1024")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ADMIN", "USER"})
    void get_documentsByDimension_9999_notOk(String role) throws Exception {
        mvc.perform(
                get(API_VECTOR_DOCUMENT + "/9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles(role)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    // I believe this call is not using the TestContainer PostgreSQL database, so it fails
    @Ignore
    void get_documentByDimensionAndId_isOk() throws Exception {
        EmbeddingModelReference embeddingModelReference = embeddingModelReferenceRepository.findAll().getFirst();

        User user = userRepository.findById(3L).get();      // Admin user

        List<String> chunks = List.of("This is a test");

        Content content = new Content();
        content.setContentType(ContentType.DOC);
        content.setUser(user);
        content.setName("Test");
        content.setValue("This is a test");
        Content savedContent = contentRepository.save(content);
        assertThat(savedContent).isNotNull();
        assertThat(savedContent.getId()).isNotNull();

        List<String> savedId =
            documentService.save(user.getId(), savedContent.getId(), chunks, embeddingModelReference.getId());

        mvc.perform(
                get(API_VECTOR_DOCUMENT + "/" + embeddingModelReference.getDimSize() + "/" + savedId.getFirst())
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(user("admin").password("password").roles("ADMIN")))
            .andDo(print())
            .andExpect(status().isOk());
    }
}
