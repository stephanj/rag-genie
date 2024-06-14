package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.*;
import com.devoxx.genie.service.agent.researcher.ResearchAgent;
import com.devoxx.genie.service.dto.*;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static com.devoxx.genie.web.rest.ContentResource.USER_NOT_FOUND_CODE;
import static com.devoxx.genie.web.rest.VectorDocumentResource.USER_NOT_FOUND;

@RestController
@RequestMapping("/api")
public class AgentResearcherResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentResearcherResource.class);

    private final UserService userService;
    private final WebSearchService webSearchService;
    private final LanguageModelService languageModelService;
    private final ChatModelService chatModelService;
    private final ContentService contentService;
    private final ContentImportService contentImportService;

    public AgentResearcherResource(UserService userService,
                                   WebSearchService webSearchService,
                                   LanguageModelService languageModelService,
                                   ChatModelService chatModelService,
                                   ContentService contentService,
                                   ContentImportService contentImportService) {
        this.userService = userService;
        this.webSearchService = webSearchService;
        this.languageModelService = languageModelService;
        this.chatModelService = chatModelService;
        this.contentService = contentService;
        this.contentImportService = contentImportService;
    }

    /**
     * POST /agent/researcher: Agent researcher researches a topic.
     *
     * @param chatModelDTO the chat model DTO
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     * @link <a href="https://github.com/mshumer/ai-researcher">https://github.com/mshumer/ai-researcher</a>
     */
    @PostMapping("/agent-researcher")
    public ResponseEntity<String> researchTopic(@RequestBody ChatModelDTO chatModelDTO) {
        LOGGER.debug("Adding text content: {}", chatModelDTO);
        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));
        // Set selected language model

        chatModelDTO.setUserId(user.getId());

        setLanguageModel(chatModelDTO);

        // Create a chat model
        ChatLanguageModel chatModel = chatModelService.createChatModel(chatModelDTO);

        // Use the selected language model by user to create a chat model
        ResearchAgent researchAgent = AiServices.create(ResearchAgent.class, chatModel);
        List<String> searchQueries = researchAgent.getSearchQueries(chatModelDTO.getQuestion());

        LOGGER.debug("Search queries: {}", searchQueries);
        List<SearchResultDTO> searchResults = getSearchResultDTOS(searchQueries, user);

        List<ContentDTO> contentDTOS = new ArrayList<>();
        searchResults.forEach(searchResult -> {
            try {
                storeSearchResults(searchResult, user, contentDTOS);
            } catch (MalformedURLException | RuntimeException e) {
                LOGGER.error("Error while retrieving content from URL: {}", searchResult.getLink(), e);
            }
        });

        StringBuilder context = new StringBuilder();
        contentDTOS.forEach(contentDTO -> context.append(contentDTO.getValue()));

        String report = researchAgent.getReport(chatModelDTO.getQuestion(), context.toString());

        // The LLM takes in all of the sub-topic reports, and generates a final, comprehensive report

        return ResponseEntity.ok().body(report);
    }

    private void setLanguageModel(ChatModelDTO chatModelDTO) {
        LanguageModelDTO languageModelDTO = languageModelService.findById(chatModelDTO.getLanguageModelDTO().getId());
        chatModelDTO.setLanguageModelDTO(languageModelDTO);
    }

    private void storeSearchResults(SearchResultDTO searchResult,
                                    User user,
                                    List<ContentDTO> contentDTOS) throws MalformedURLException {
        String contentFromUrl = contentImportService.getContentFromUrl(searchResult.getLink());

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setUserId(user.getId());
        contentDTO.setValue(contentFromUrl);
        contentDTO.setSource(searchResult.getLink());
        contentDTO.setContentType(com.devoxx.genie.service.dto.enumeration.ContentType.HTML);
        contentDTO.setName(searchResult.getSnippet());
        try{
            contentDTOS.add(contentService.save(contentDTO));

        }catch (Exception ignored){}
    }

    private @NotNull List<SearchResultDTO> getSearchResultDTOS(List<String> searchQueries, User user) {
        List<SearchResultDTO> searchResults = new ArrayList<>();
        searchQueries.stream().filter((str) -> !str.trim().isEmpty()).forEach(query -> {
            searchResults.addAll(webSearchService.retrieve(user.getId(), query, 3));
            LOGGER.debug("Retrieved documents: {}", searchResults);
        });
        return searchResults;
    }
}
