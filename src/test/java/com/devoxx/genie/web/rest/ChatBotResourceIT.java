package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.EmbeddingModelReference;
import com.devoxx.genie.domain.LanguageModel;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.domain.enumeration.SplitterStrategy;
import com.devoxx.genie.repository.EmbeddingModelReferenceRepository;
import com.devoxx.genie.repository.LanguageModelRepository;
import com.devoxx.genie.service.ContentImportService;
import com.devoxx.genie.service.ContentService;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import com.devoxx.genie.service.dto.enumeration.ContentType;
import com.devoxx.genie.service.splitter.SplitterService;
import com.devoxx.genie.web.GenieWebTest;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@GenieWebTest
class ChatBotResourceIT extends AbstractMVCContextIT {
    @Autowired
    ContentService contentService;

    @Autowired
    ContentImportService contentImportService;

    @Autowired
    LanguageModelRepository languageModelRepository;

    @Autowired
    EmbeddingModelReferenceRepository embeddingModelReferenceRepository;

    @Autowired
    SplitterService splitterService;

    @Autowired
    @Qualifier("dbEmbeddingStore384") EmbeddingStore<TextSegment> embeddingStore384;

    @Test
    void test_chatMsg_isOk() throws Exception {

        // Create content based on web page Devoxx BE FAQ
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setSource("https://devoxx.be/faq");
        contentDTO.setContentType(ContentType.HTML);
        contentDTO.setName("Devoxx FAQ");
        String contentFromUrl = contentImportService.getContentFromUrl(contentDTO.getSource());
        contentDTO.setValue(contentFromUrl);
        var savedContent = contentService.save(contentDTO);

        // NOTE This will only work locally !! (not in CI)
        // Select Ollama Mistral language model
        LanguageModel languageModel = languageModelRepository.findAll().stream().filter(
            model -> model.getName().contains("mistral") && model.getModelType().equals(LanguageModelType.OLLAMA)
        ).findFirst().orElseThrow();

        // Get first embedding model reference
        EmbeddingModelReference embeddingModelReference = embeddingModelReferenceRepository.findAll().getFirst();

        AllMiniLmL6V2EmbeddingModel allMiniLmL6V2EmbeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // Split content into chunks, embed and store them
        splitterService.split(SplitterStrategy.PARAGRAPH, savedContent.getId().toString(), savedContent.getValue(), 550, 25)
            .forEach(chunk -> {
                Response<Embedding> embed = allMiniLmL6V2EmbeddingModel.embed(chunk);
                TextSegment textSegment = new TextSegment(chunk, new Metadata());
                embeddingStore384.add(embed.content(), textSegment);
            });

        LanguageModelDTO languageModelDTO = new LanguageModelDTO();
        languageModelDTO.setId(languageModel.getId());

        ChatModelDTO chatModelDTO = ChatModelDTO.builder()
            .question("What are the ticket prices?")
            .embeddingModelRefId(embeddingModelReference.getId())
            .languageModelDTO(languageModelDTO)
            .embeddingModelRefId(embeddingModelReference.getId())
            .build();

        mvc.perform(
                MockMvcRequestBuilders.post(API_CHAT_BOT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(chatModelDTO))
                    .with(user("admin").password("password").roles("ADMIN")))
            .andDo(print())
            .andExpect(status().isOk());
    }

}
