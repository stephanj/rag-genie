package com.devoxx.genie.service;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.service.dto.SearchResultDTO;
import com.google.gson.JsonObject;
import com.hw.serpapi.GoogleSearch;
import com.hw.serpapi.SerpApiSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebSearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSearchService.class);

    private final ApiKeyService apiKeyService;

    public WebSearchService(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    /**
     * Retrieve search results from the web.
     * @param userId the user ID
     * @param query the search query
     * @param maxResults the maximum number of results to return
     * @return the search results
     */
    public List<SearchResultDTO> retrieve(Long userId,
                                          String query,
                                          int maxResults) {
        return apiKeyService.getApiKeyForUserIdAndLanguageModelType(userId, LanguageModelType.SERPAPI)
            .map(apiKey -> searchWeb(query, maxResults, apiKey))
            .orElseThrow(() -> new RuntimeException("No API key found for user"));
    }

    /**
     * Search the web for the given query.
     * @param query the search query
     * @param maxResults the maximum number of results to return
     * @param apiKey the SerpAPI key
     * @return the search results
     */
    private List<SearchResultDTO> searchWeb(String query,
                                            Integer maxResults,
                                            String apiKey) {
        LOGGER.debug("Search web for query: {}", query);

        Map<String, String> parameter = new HashMap<>();
        parameter.put(SerpApiSearch.API_KEY_NAME, apiKey);
        parameter.put("q", query);
        parameter.put("engine", "google");
        parameter.put("google_domain", "google.com");
        parameter.put("gl", "us");
        parameter.put("num", maxResults.toString());      // Only return 3 results for now
        parameter.put("hl", "en");
        parameter.put("device", "desktop");

        SerpApiSearch search = new GoogleSearch();
        search.setParameter(parameter);

        JsonObject result = search.getJson();

        List<SearchResultDTO> searchResults = new ArrayList<>();

        result.getAsJsonArray("organic_results")
            .forEach(element -> {
                LOGGER.debug("Search element from organic_results: {}", element);
                searchResults.add(
                    SearchResultDTO.builder()
                        .snippet(element.getAsJsonObject().get("snippet").getAsString())
                        .link(element.getAsJsonObject().get("link").getAsString())
                        .build());
            });

        return searchResults;
    }
}
