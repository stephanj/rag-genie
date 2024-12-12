package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.ContentService;
import com.devoxx.genie.service.LanguageModelService;
import com.devoxx.genie.service.QuestionService;
import com.devoxx.genie.service.dto.*;
import com.devoxx.genie.service.dto.enumeration.DocumentType;
import com.google.common.util.concurrent.AtomicDouble;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;

record AnalysisPrompt(String userPrompt, String systemPrompt) {
}

@RestController
@RequestMapping("/api")
public class AnalyseResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyseResource.class);
    private final ContentService contentService;
    private final QuestionService questionService;
    private final LanguageModelService languageModelService;
    private final List<AnalysisPrompt> prompts = new ArrayList<>();

    /**
     * Experimental
     * @param contentService   the content service
     * @param questionService  the question service
     * @param languageModelService the language model service
     */
    public AnalyseResource(ContentService contentService,
                           QuestionService questionService,
                           LanguageModelService languageModelService) {
        this.contentService = contentService;
        this.questionService = questionService;
        this.languageModelService = languageModelService;
        createPrompts();
    }

    private void createPrompts() {

        AnalysisPrompt improveCodePrompt = new AnalysisPrompt(
            "How can this code be improved?  Give me example code of how it can be achieved.",
            """
                You're a Java programming language expert which knows about all the design patterns,
                spring boot and the latest java algorithms. You have to analyze the code and provide the best possible solution to improve the code.
                """
        );

        AnalysisPrompt findBugsPrompt = new AnalysisPrompt(
            "Does this code have any (logical) bugs?",
            """
                You're a Java programming language expert developer which knows everything about possible coding bugs.
                You have to analyze the code and provide the best possible solution to fix the bugs with code examples.
                """
        );

        AnalysisPrompt securityPrompt = new AnalysisPrompt(
            "Does this code have any potential security issues or bugs?",
            """
                You're a Java programming language expert developer which knows everything about possible security issues, possible hacks etc.
                You have to analyze the code and provide the best possible solution to fix the security issues with code examples.
                """
        );

        AnalysisPrompt unitTestPrompt = new AnalysisPrompt(
            "Create unit tests for the provided code.",
            """
                You're a Java programming language expert developer which knows everything about junit and unit testing.
                You have to analyze the code and provide the best possible solution to write unit tests for the code with code examples.
                """
        );

        AnalysisPrompt integrationTestPrompt = new AnalysisPrompt(
            "Create integration tests for the provided code.",
            """
                You're a Java programming language expert developer which knows everything about junit and unit testing.
                You have to analyze the code and provide the best possible solution to write unit tests for the code with code examples.
                """
        );

        prompts.add(0, improveCodePrompt);
        prompts.add(1, findBugsPrompt);
        prompts.add(2, securityPrompt);
        prompts.add(3, unitTestPrompt);
        prompts.add(4, integrationTestPrompt);
    }

    /**
     * Analyze the content.
     * @param contentId          the content ID
     * @param modelId            the model ID
     * @param promptId           the prompt ID
     * @param includeComments    whether to exclude comments
     * @param includeRelatedCode whether to exclude related code
     * @return the response entity
     */
    @PostMapping("/analyze/content")
    public ResponseEntity<String> analyseContent(@RequestParam Long contentId,
                                                 @RequestParam Long modelId,
                                                 @RequestParam String customPrompt,
                                                 @RequestParam Integer promptId,
                                                 @RequestParam boolean includeComments,
                                                 @RequestParam boolean includeRelatedCode) {
        LOGGER.debug("REST request to analyse content: {}", contentId);

        ContentDTO byId = contentService.findById(contentId);

        List<Long> contentIds = new ArrayList<>();

        if (includeRelatedCode) {
            Set<String> processedImports = new HashSet<>();
            Set<String> totalImports = new HashSet<>();
            analyseImportsRecursively(byId, processedImports, totalImports, contentIds);
        } else {
            contentIds.add(contentId);
        }

        List<DocumentDTO> documents = new ArrayList<>();

        contentIds.forEach(id -> {
            ContentDTO content = contentService.findById(id);

            String codeValue = content.getValue();
            if (Boolean.FALSE.equals(includeComments)) {
                codeValue = codeValue.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "");
            }
            documents.add(DocumentDTO.builder()
                    .id(content.getId().toString())
                    .text(codeValue)
                    .score(1.0)
                    .docType(DocumentType.CONTENT)
                .build());
        });

        String prompt = "";
        if (customPrompt.isEmpty()) {
            prompt = prompts.get(promptId).userPrompt();
        } else {
            prompt = prompts.get(promptId).systemPrompt();
        }

        ChatModelDTO chatModelDTO = ChatModelDTO.builder()
            .userId(HARD_CODED_USER_ID)
            .question("")
            .prompt(prompt)
            .languageModelDTO(languageModelService.findById(modelId))
            .build();

        InteractionDTO interactionDTO =
            questionService.generateResponseFromSimilarityOrAllContent(chatModelDTO, documents);

        return ResponseEntity.ok().body(interactionDTO.getAnswer());
    }

    public record TokenInfo(double inputTokens,
                            double outputTokens,
                            double inputCost,
                            double outputCost) implements Serializable {
    }

    /**
     * Get the analysis cost for the selected large language model.
     *
     * @param contentId          the content ID
     * @param modelId            the model ID
     * @param excludeRelatedCode whether to exclude related code
     * @return the response entity
     */
    @PostMapping("/analyze/cost")
    public ResponseEntity<TokenInfo> analyseCost(@RequestParam Long modelId,
                                                 @RequestParam(defaultValue = "4000") double maxOutputTokens,
                                                 @RequestParam(required = false) Long contentId,
                                                 @RequestParam(required = false) Boolean excludeRelatedCode) {

        LOGGER.debug("REST request to get analysis cost: {}", contentId);

        final AtomicDouble inputTokenSize = new AtomicDouble(0.0);
        if (contentId == null) {
            // Get all content
            contentService.findAllByUserId(Pageable.unpaged(), HARD_CODED_USER_ID)
                .forEach(contentDTO ->
                    inputTokenSize.addAndGet(getDocumentTokenSize(contentDTO.getId(), true)));
        } else {
            inputTokenSize.addAndGet(getDocumentTokenSize(contentId, excludeRelatedCode));
        }

        if (inputTokenSize.get() > 0.0 && maxOutputTokens > 0) {
            LanguageModelDTO languageModel = languageModelService.findById(modelId);

            double costInput1M = languageModel.getCostInput1M();
            double costOutput1M = languageModel.getCostOutput1M();

            double inputCost = (inputTokenSize.get() / 1_000_000) * costInput1M;
            double outputCost = (maxOutputTokens / 1_000_000) * costOutput1M;

            return ResponseEntity.ok().body(new TokenInfo(inputTokenSize.get(), maxOutputTokens, inputCost, outputCost));
        } else {
            return ResponseEntity.ok().body(new TokenInfo(0, 0, 0, 0));
        }
    }

    private double getDocumentTokenSize(Long contentId, Boolean excludeRelatedCode) {
        ContentDTO byId = contentService.findById(contentId);

        List<Long> contentIds = new ArrayList<>();

        if (Boolean.TRUE.equals(excludeRelatedCode)) {
            contentIds.add(contentId);
        } else {
            Set<String> processedImports = new HashSet<>();
            Set<String> totalImports = new HashSet<>();
            analyseImportsRecursively(byId, processedImports, totalImports, contentIds);
        }

        AtomicDouble tokenCount = new AtomicDouble();

        contentIds.forEach(id -> {
            ContentDTO content = contentService.findById(id);
            tokenCount.addAndGet(content.getTokenCount());
        });

        return tokenCount.get();
    }

    /**
     * Analyse the imports of the content recursively.
     *
     * @param content          the content
     * @param processedImports the processed imports
     * @param totalImports     the total imports
     * @param contentIds       the content IDs
     */
    private void analyseImportsRecursively(ContentDTO content,
                                           Set<String> processedImports,
                                           Set<String> totalImports,
                                           List<Long> contentIds) {

        contentIds.add(content.getId());

        List<String> imports = getImports(content);
        imports.forEach(originalImport -> {
            LOGGER.debug("Import: {}", originalImport);

            // Format the import to match your content names or paths
            String packageClassName = originalImport.replace("import ", "")
                .replace(".", "/")
                .replace(";", "")
                .trim() + ".java";

            // Skip already processed imports to avoid infinite loops
            if (!processedImports.add(originalImport)) {
                return;
            }

            contentService.findByName(packageClassName).ifPresentOrElse(
                contentDTO -> {
                    LOGGER.debug("Content: {}", contentDTO);
                    totalImports.add(packageClassName); // Use the new variable here
                    // Recursively analyse imports of the found content
                    analyseImportsRecursively(contentDTO, processedImports, totalImports, contentIds);
                },
                () -> LOGGER.debug("Content not found for import: {}", packageClassName) // Use the new variable here
            );
        });
    }

    /**
     * Extracts the imports from the content.
     *
     * @param byId the content
     * @return the imports
     */
    @NotNull
    private static List<String> getImports(ContentDTO byId) {
        String regex = "\\bimport\\s+([\\w.]+);";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(byId.getValue());

        List<String> imports = new ArrayList<>();
        while (matcher.find()) {
            // TODO Filter the imports to match your content names or paths
            if (matcher.group().contains("com.devoxx.genie")) {
                imports.add(matcher.group());
            }
        }
        return imports;
    }
}
