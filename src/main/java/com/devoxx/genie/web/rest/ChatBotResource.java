package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.ChatModelService;
import com.devoxx.genie.service.EmbeddingModelService;
import com.devoxx.genie.service.LanguageModelService;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.chatbot.Assistant;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatBotResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBotResource.class);

    private final UserService userService;
    private final EmbeddingModelService embeddingModelService;
    private final LanguageModelService languageModelService;
    private final EmbeddingStore<TextSegment> embeddingStore384;
    private final EmbeddingStore<TextSegment> embeddingStore512;
    private final EmbeddingStore<TextSegment> embeddingStore1024;
    private final ChatModelService chatModelService;

    public ChatBotResource(UserService userService,
                           EmbeddingModelService embeddingModelService,
                           LanguageModelService languageModelService,
                           @Qualifier("dbEmbeddingStore384") EmbeddingStore<TextSegment> embeddingStore384,
                           @Qualifier("dbEmbeddingStore512") EmbeddingStore<TextSegment> embeddingStore512,
                           @Qualifier("dbEmbeddingStore1024") EmbeddingStore<TextSegment> embeddingStore1024, ChatModelService chatModelService) {
        this.userService = userService;
        this.embeddingModelService = embeddingModelService;
        this.languageModelService = languageModelService;
        this.embeddingStore384 = embeddingStore384;
        this.embeddingStore512 = embeddingStore512;
        this.embeddingStore1024 = embeddingStore1024;
        this.chatModelService = chatModelService;
    }

    /**
     * POST /chatbot: Chat with the bot and get a response.
     * @param chatModelDTO the chat model DTO
     * @return the response
     */
    @PostMapping(value = "/chat", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> chatMsg(@RequestBody ChatModelDTO chatModelDTO) {

        LOGGER.debug("REST request to chat with the bot: {}", chatModelDTO.getQuestion());

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException("USER_NOT_FOUND", "USER", "USER_NOT_FOUND_CODE"));
        chatModelDTO.setUserId(user.getId());

        EmbeddingModel embeddingModel = embeddingModelService.getEmbeddingModelByUserIdAndRefId(user.getId(), chatModelDTO.getEmbeddingModelRefId())
            .orElseThrow(() -> new IllegalArgumentException("Embedding model not found"));

        chatModelDTO.setLanguageModelDTO(languageModelService.findById(chatModelDTO.getLanguageModelDTO().getId()));
        ChatLanguageModel chatLanguageModel = chatModelService.createChatModel(chatModelDTO);

        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatLanguageModel);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore384)
            .embeddingModel(embeddingModel)
            .maxResults(chatModelDTO.getMaxResults())
            .minScore(chatModelDTO.getMinScore())
            .build();

        // The RetrievalAugmentor serves as the entry point into the RAG flow in LangChain4j.
        // It can be configured to customize the RAG behavior according to your requirements.
        // In subsequent examples, we will explore more customizations.
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
            .queryTransformer(queryTransformer)
            .contentRetriever(contentRetriever)
            .build();

        Assistant build = AiServices.builder(Assistant.class)
            .chatLanguageModel(chatLanguageModel)
            .retrievalAugmentor(retrievalAugmentor)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build();

        String answer = build.answer(chatModelDTO.getQuestion());

        return ResponseEntity.ok().body(answer);
    }
}
