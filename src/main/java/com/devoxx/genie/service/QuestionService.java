package com.devoxx.genie.service;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.domain.enumeration.UserVote;
import com.devoxx.genie.service.dto.*;
import com.devoxx.genie.service.dto.enumeration.DocumentType;
import com.devoxx.genie.service.exception.LanguageModelException;
import com.devoxx.genie.service.gpt.GPTService;
import com.devoxx.genie.service.splitter.SimilarityService;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;

@Service
public class QuestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionService.class);
    public static final String HTML_RESPONSE_PROMPT = """
        Today is {{date}}
        ---------
        PROMPT: {{prompt}}
        ---------
        CONTEXT:
        {{context}}
        """;

    private final LanguageModelService languageModelService;
    private final ContentService contentService;
    private final InteractionService interactionService;
    private final SimilarityService similarityService;
    private final WebSearchService webSearchService;
    private final EmbeddingModelService embeddingModelService;
    private final GPTService gptService;
    private final ReRankService reRankService;
    private final ChatModelService chatModelService;

    public QuestionService(final LanguageModelService languageModelService,
                           final ContentService contentService,
                           final InteractionService interactionService,
                           final EmbeddingModelService embeddingModelService,
                           final SimilarityService similarityService,
                           final GPTService gptService,
                           final ReRankService reRankService,
                           final WebSearchService webSearchService,
                           final ChatModelService chatModelService) {
        this.languageModelService = languageModelService;
        this.contentService = contentService;
        this.interactionService = interactionService;
        this.embeddingModelService = embeddingModelService;
        this.similarityService = similarityService;
        this.gptService = gptService;
        this.reRankService = reRankService;
        this.webSearchService = webSearchService;
        this.chatModelService = chatModelService;
    }

    /**
     * Generate response from documents.
     * @param chatModelDTO     the chat model DTO
     * @param documents        the documents
     * @param allContentUsed   use all content
     * @return the response entity
     */
    public GenieResponse<AiMessage> generateResponseFromSimilarityOrAllContent(ChatModelDTO chatModelDTO,
                                                                               List<DocumentDTO> documents,
                                                                               boolean allContentUsed) {

        LOGGER.debug("GenerateResponseFromDocuments with params: {} using all content? {}", chatModelDTO, allContentUsed);

        // Create a chat language model
        ChatLanguageModel chatLanguageModel = chatModelService.createChatModel(chatModelDTO);

        // Build the prompt context
        String context = buildContext(documents);

        // Combine the prompt and context to one message
        String userPrompt = buildUserPrompt(chatModelDTO.getPrompt(), context);

        // Prepare chat messages
        List<ChatMessage> chatMessages = prepareChatMessages(chatModelDTO.getLanguageModelDTO(),
                                                             userPrompt,
                                                             chatModelDTO.getQuestion());

        // Generate response
        Response<AiMessage> aiMessageResponse = callLanguageModel(chatLanguageModel, chatMessages);

        if (aiMessageResponse == null || aiMessageResponse.content() == null) {
            return new GenieResponse<>(new AiMessage("No answer found"), null, null);
        }

        if (aiMessageResponse.tokenUsage() == null) {
            // Calc token usage if not already calculated
            TokenUsage tokenUsage = new TokenUsage(TokenService.countTokens(userPrompt), TokenService.countTokens(aiMessageResponse.content().text()));
            aiMessageResponse = new Response<>(aiMessageResponse.content(), tokenUsage, aiMessageResponse.finishReason());
        }

        if (chatModelDTO.isFormatResponse()) {
            String answerInHTMLFormat = gptService.getAnswerInHTMLFormat(chatLanguageModel, aiMessageResponse.content().text());
             return new GenieResponse<>(aiMessageResponse.content(),
                                        aiMessageResponse.tokenUsage(),
                                        aiMessageResponse.finishReason(),
                                        answerInHTMLFormat);
        } else {
            return new GenieResponse<>(aiMessageResponse.content(),
                                       aiMessageResponse.tokenUsage(),
                                       aiMessageResponse.finishReason());
        }
    }

    /**
     * Process model response
     *
     * @param chatModel    the chat model
     * @param chatMessages the chat messages
     * @return the response AI message
     */
    @Nullable
    private static Response<AiMessage> callLanguageModel(ChatLanguageModel chatModel,
                                                         List<ChatMessage> chatMessages) {
        LOGGER.debug("Calling language model");

        try {
            return chatModel.generate(chatMessages);
        } catch (Exception e) {
            LOGGER.error("OpenAI error calling language model", e);
            throw new LanguageModelException("Error calling language model", e);
        }
    }

    /**
     * This method will generate a response from the language model based on the user's question.
     * Using either a similarity search or all available content.
     * @param chatModelDTO the chat model dto
     * @return the response entity
     */
    @Transactional
    public InteractionDTO generateResponseFromSimilarityOrAllContent(final ChatModelDTO chatModelDTO) {
        LOGGER.debug("GenerateResponseFromDocuments with params: {}", chatModelDTO);

        LanguageModelDTO languageModelDTO = languageModelService.findById(chatModelDTO.getLanguageModelDTO().getId());
        chatModelDTO.setLanguageModelDTO(languageModelDTO);

        GenieResponse<AiMessage> genieResponse;
        List<DocumentDTO> similarDocumentsBasedOnQuery = new ArrayList<>();

        Long start = System.currentTimeMillis();

        if (isQueryOnCompleteContent(chatModelDTO, languageModelDTO)) {
            genieResponse = getResponseFromAllContent(chatModelDTO);
        } else {
            similarDocumentsBasedOnQuery =
                similarityService.findSimilarDocumentsBasedOnQuery(chatModelDTO);

            // Add the content info, so we know where the document comes from.
            similarDocumentsBasedOnQuery.forEach(contentService::addContentInfo);

            if (chatModelDTO.isRerank()) {
                similarDocumentsBasedOnQuery = reRankService.reRankDocuments(chatModelDTO.getQuestion(), similarDocumentsBasedOnQuery);
            }

            genieResponse = getResponseFromSimilarityDocuments(chatModelDTO, similarDocumentsBasedOnQuery);
        }

        Long end = System.currentTimeMillis();

        InteractionDTO savedInteraction = addInteraction(chatModelDTO, genieResponse, languageModelDTO, end - start);

        if (!similarDocumentsBasedOnQuery.isEmpty()) {
            savedInteraction.setUsedDocuments(similarDocumentsBasedOnQuery);
        }

        return savedInteraction;
    }

    /**
     * Ask a question using params and provided documents.
     * @param chatModelDTO the chat model param dto
     * @param documents        the documents to use
     * @return the response entity
     */
    @Transactional
    public InteractionDTO generateResponseFromSimilarityOrAllContent(final ChatModelDTO chatModelDTO,
                                                                     final List<DocumentDTO> documents) {

        LOGGER.debug("GenerateResponseFromDocuments with params: {}", chatModelDTO);

        LanguageModelDTO languageModelDTO = languageModelService.findById(chatModelDTO.getLanguageModelDTO().getId());

        GenieResponse<AiMessage> genieResponse;
        List<DocumentDTO> similarDocumentsBasedOnQuery = new ArrayList<>();

        Long start = System.currentTimeMillis();

        // This is used by the Code Analyzer (Experimental)
        genieResponse = generateResponseFromSimilarityOrAllContent(chatModelDTO, documents, false);

        Long end = System.currentTimeMillis();

        InteractionDTO savedInteraction = addInteraction(chatModelDTO, genieResponse, languageModelDTO, end - start);

        savedInteraction.setUsedDocuments(similarDocumentsBasedOnQuery);

        return savedInteraction;
    }

    /**
     * Check if query is on complete content
     *
     * @param questionParamDTO the question param dto
     * @param languageModelDTO the language model dto
     * @return the boolean
     */
    private static boolean isQueryOnCompleteContent(ChatModelDTO questionParamDTO,
                                                    LanguageModelDTO languageModelDTO) {
        return Boolean.TRUE.equals(questionParamDTO.getAllDocs()) && languageModelDTO != null;
    }

    /**
     * Get response on similarity documents
     * @param chatModelDTO the chat model param dto
     * @param similarDocumentsBasedOnQuery the similar documents based on query
     * @return the response entity
     */
    private GenieResponse<AiMessage> getResponseFromSimilarityDocuments(ChatModelDTO chatModelDTO,
                                                                        List<DocumentDTO> similarDocumentsBasedOnQuery) {

        LOGGER.debug("GetResponseFromSimilarityChunks with params: {}", chatModelDTO);

        if (chatModelDTO.isSearchWeb()) {
            searchWebWithSimilarityResults(chatModelDTO, similarDocumentsBasedOnQuery);
        }

        // Use the similarity score query documents
        return generateResponseFromSimilarityOrAllContent(chatModelDTO,
                                                          similarDocumentsBasedOnQuery,
                                                        false);
    }

    /**
     * Search web with similarity results
     * @param chatModelDTO the chat model DTO
     * @param similarDocumentsBasedOnQuery the similar documents based on query
     */
    private void searchWebWithSimilarityResults(ChatModelDTO chatModelDTO,
                                                List<DocumentDTO> similarDocumentsBasedOnQuery) {

        LOGGER.debug("SearchWebWithSimilarityResults with params: {}", chatModelDTO);

        // Web search
        List<SearchResultDTO> similarDocumentsBasedOnWebQuery =
            webSearchService.retrieve(chatModelDTO.getUserId(), chatModelDTO.getQuestion(), 3);

        // Do similarity search on in memory content
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        similarDocumentsBasedOnWebQuery.forEach(item -> {
            // TODO We only use the search snippet for now, but we could use the full text using WebPage content
            Embedding embedding = embeddingModel.embed(item.getSnippet()).content();
            Metadata metadata = new Metadata().put("userId", chatModelDTO.getUserId().toString());
            embeddingStore.add(embedding, new TextSegment(item.getText(), metadata));
        });

        Response<Embedding> embedQuestion = embeddingModel.embed(chatModelDTO.getQuestion());

        // Find relevant documents with min score
        embeddingStore.findRelevant(embedQuestion.content(), chatModelDTO.getMaxResults(), chatModelDTO.getMinScore())
            .forEach(match -> addDocuments(similarDocumentsBasedOnQuery, match));
    }

    private void addDocuments(List<DocumentDTO> similarDocumentsBasedOnQuery,
                              EmbeddingMatch<TextSegment> match) {

        similarDocumentsBasedOnQuery.add(DocumentDTO.builder()
            .id(match.embeddingId())
            .text(match.embedded().text())
            .score(match.score())
            .docType(DocumentType.WEB_SEARCH)
            .metadata(match.embedded().metadata().toString())
            .build());
    }

    /**
     * Get response on complete content
     * @param chatModelDTO the chat model DTO
     * @return the response entity
     */
    private GenieResponse<AiMessage> getResponseFromAllContent(ChatModelDTO chatModelDTO) {

        LOGGER.debug("GetResponseFromAllContent with params: {}", chatModelDTO);

        List<ContentDTO> allContentByUser = contentService.findAllByUserId(Pageable.unpaged(), chatModelDTO.getUserId()).getContent();

        // Create DocumentDTO objects in one go, avoiding a separate forEach
        List<DocumentDTO> newDocuments = allContentByUser.stream()
            .map(contentDTO ->
                DocumentDTO.builder()
                    .id(contentDTO.getId().toString())
                    .text(contentDTO.getValue())
                    .docType(DocumentType.CONTENT)
                    .build())
            .toList();

        LOGGER.debug("Total documents: {}", newDocuments.size());

        if (chatModelDTO.isSearchWeb()) {
            searchWebWithSimilarityResults(chatModelDTO, newDocuments);
        }

        // Directly return the response from generateResponseFromDocuments
        return generateResponseFromSimilarityOrAllContent(chatModelDTO, newDocuments, true);
    }

    /**
     * Add interaction
     *
     * @param chatModelDTO the chat model DTO
     * @param genieResponse    the AI response
     * @param languageModelDTO the language model dto
     */
    private InteractionDTO addInteraction(ChatModelDTO chatModelDTO,
                                          GenieResponse<AiMessage> genieResponse,
                                          LanguageModelDTO languageModelDTO,
                                          Long durationInMs) {

        LOGGER.debug("AddInteraction with params: {}", chatModelDTO);

        InteractionDTO interactionDTO = new InteractionDTO();
        interactionDTO.setUserId(chatModelDTO.getUserId());
        interactionDTO.setQuestion(chatModelDTO.getQuestion());

        interactionDTO.setEmbeddingModel(
            embeddingModelService.getEmbeddingModelReferenceDTOById(chatModelDTO.getEmbeddingModelRefId())
        );

        if (genieResponse.getHtmlAnswer() != null) {
            interactionDTO.setAnswer(genieResponse.getHtmlAnswer());
        } else {
            interactionDTO.setAnswer(genieResponse.content().text());
        }

        interactionDTO.setDurationInMs(durationInMs);
        interactionDTO.setVote(UserVote.NO_VOTE);
        interactionDTO.setLanguageModel(languageModelDTO);

        calcCost(genieResponse.tokenUsage(), interactionDTO, languageModelDTO);

        return interactionService.save(interactionDTO);
    }

    /**
     * Calculate the cost of language model interaction
     *
     * @param languageModelDTO the language model dto
     * @param interactionDTO   the interaction dto
     */
    private void calcCost(TokenUsage tokenUsage,
                          InteractionDTO interactionDTO,
                          LanguageModelDTO languageModelDTO) {

        LOGGER.debug("CalcCost with params: {}", interactionDTO);

        double costInput1M = languageModelDTO.getCostInput1M();
        double costOutput1M = languageModelDTO.getCostOutput1M();

        if (tokenUsage == null) {
            interactionDTO.setCost(0.0);
            return;
        }

        Integer inputTokens = tokenUsage.inputTokenCount();
        if (inputTokens == null) {
            inputTokens = interactionDTO.getInputTokens();
        }

        Integer outputTokens = tokenUsage.outputTokenCount();
        interactionDTO.setOutputTokens(outputTokens);
        interactionDTO.setInputTokens(inputTokens);

        if (inputTokens != null && inputTokens > 0 &&
            outputTokens != null && outputTokens > 0 &&
            costInput1M > 0 && costOutput1M > 0) {

            var inputCost = inputTokens * (costInput1M / 1_000_000);
            var outputCost = outputTokens * (costOutput1M / 1_000_000);

            interactionDTO.setCost(inputCost + outputCost);
        } else {
            interactionDTO.setCost(0.0);
        }
    }

    /**
     * Build context for prompt
     *
     * @param documents the documents
     * @return the context
     */
    private String buildContext(List<DocumentDTO> documents) {
        return documents.stream()
            .map(DocumentDTO::getText)
            .collect(Collectors.joining());
    }

    /**
     * Build message
     *
     * @param prompt  the prompt
     * @param context the context
     * @return the message
     */
    private String buildUserPrompt(String prompt, String context) {
        return HTML_RESPONSE_PROMPT.replace("{{prompt}}", prompt)
            .replace("{{context}}", context)
            .replace("{{date}}", ZonedDateTime.now().toString());
    }

    /**
     * Prepare chat messages
     *
     * @param languageModelDTO the language model dto
     * @param message          the message
     * @param question         the question
     * @return the list of chat messages
     */
    private List<ChatMessage> prepareChatMessages(LanguageModelDTO languageModelDTO,
                                                  String message,
                                                  String question) {
        LOGGER.debug("PrepareChatMessages with message: {}", message);
        List<ChatMessage> messages = new ArrayList<>();

        if (languageModelDTO.getModelType().equals(LanguageModelType.GEMINI)) {
            messages.add(userMessage(message + "\n\n" + question));
        } else {
            messages.add(systemMessage(message));
            messages.add(userMessage("question", question));
        }
        return messages;
    }
}
