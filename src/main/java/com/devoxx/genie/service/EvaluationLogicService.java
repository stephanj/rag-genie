package com.devoxx.genie.service;

import com.devoxx.genie.service.dto.*;
import com.devoxx.genie.service.score.SimilarityScore;
import com.devoxx.genie.service.splitter.SimilarityService;
import dev.langchain4j.data.message.AiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class EvaluationLogicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationLogicService.class);

    private final EvaluationResultService evaluationEntryService;
    private final QuestionService questionService;
    private final SimilarityService similarityService;
    private final EmbeddingModelService embeddingModelService;
    private final ReRankService reRankService;

    public EvaluationLogicService(EvaluationResultService evaluationEntryService,
                                  QuestionService questionService,
                                  SimilarityService similarityService,
                                  EmbeddingModelService embeddingModelService,
                                  ReRankService reRankService) {
        this.evaluationEntryService = evaluationEntryService;
        this.questionService = questionService;
        this.similarityService = similarityService;
        this.embeddingModelService = embeddingModelService;
        this.reRankService = reRankService;
    }

    /**
     * Evaluate the question
     *
     * @param chatModelDTO     the chat model DTO
     * @param evaluationDTO    the evaluation DTO
     */
    public void evaluate(ChatModelDTO chatModelDTO,
                         EvaluationDTO evaluationDTO) {
        LOGGER.debug("Evaluating question: {}", evaluationDTO);

        List<DocumentDTO> relevantDocuments = similarityService.findSimilarDocumentsBasedOnQuery(chatModelDTO);

        if (chatModelDTO.isRerank()) {
            relevantDocuments = reRankService.reRankDocuments(evaluationDTO.getQuestion(), relevantDocuments);
        }

        Long start = System.currentTimeMillis();

        GenieResponse<AiMessage> response =
            questionService.generateResponseFromSimilarityOrAllContent(chatModelDTO, relevantDocuments,  false);

        Long end = System.currentTimeMillis();

        String answer = response.content().text();

        createEvaluationResultItem(chatModelDTO,
            evaluationDTO,
            answer,
            response,
            relevantDocuments,
            end - start,
            chatModelDTO.isRerank());
    }

    /**
     * Create an evaluation result item
     * @param chatModelDTO chat model DTO
     * @param evaluationDTO the evaluation DTO
     * @param answer the answer
     * @param response the response
     * @param usedDocuments the used documents
     * @param durationInMs the duration in milliseconds
     * @param rerank the rerank flag
     */
    private void createEvaluationResultItem(ChatModelDTO chatModelDTO,
                                            EvaluationDTO evaluationDTO,
                                            String answer,
                                            GenieResponse<AiMessage> response,
                                            List<DocumentDTO> usedDocuments,
                                            Long durationInMs,
                                            Boolean rerank) {

        EvaluationResultDTO result = new EvaluationResultDTO();

        result.setKeywordMatch(getKeywordMatch(evaluationDTO, answer));
        Double simScore = SimilarityScore.computeSimilarityScore(evaluationDTO.getAnswer(), answer);
        result.setSimilarityScore(simScore);
        result.setAnswer(answer);

        result.setEmbeddingModelReference(embeddingModelService.getEmbeddingModelReferenceDTOById(chatModelDTO.getEmbeddingModelRefId()));

        result.setRerank(rerank);
        result.setUserId(chatModelDTO.getUserId());
        result.setLanguageModel(chatModelDTO.getLanguageModelDTO());
        result.setEvaluation(evaluationDTO);

        result.setDurationInMs(durationInMs);

        result.setUsedDocuments(new HashSet<>(usedDocuments));

        if (response.tokenUsage() != null) {
            result.setInputTokens(response.tokenUsage().inputTokenCount());
            result.setOutputTokens(response.tokenUsage().outputTokenCount());
        }

        evaluationEntryService.save(result);
    }

    /**
     * Get the keyword match
     *
     * @param evaluationDTO the evaluation DTO
     * @param answer        the answered response
     * @return the keyword match
     */
    public Double getKeywordMatch(EvaluationDTO evaluationDTO, String answer) {
        LOGGER.debug("Evaluating keyword match: {}", evaluationDTO);

        var keywords = evaluationDTO.getKeywords();
        if (keywords == null || keywords.isEmpty()) {
            return 0.0;
        }

        String answerLowercase = answer.toLowerCase().trim();
        String[] theKeywords = evaluationDTO.getKeywords().getFirst().split(",");

        List<String> list = Arrays.stream(theKeywords).toList();
        // Check if all keywords are present in the response
        double hits = list.stream()
            .filter(keyword -> answerLowercase.contains(keyword.toLowerCase().trim()))
            .count();

        return (hits / theKeywords.length) * 100.0;
    }
}
