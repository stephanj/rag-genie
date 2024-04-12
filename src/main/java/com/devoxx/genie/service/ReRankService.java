package com.devoxx.genie.service;

import com.devoxx.genie.service.dto.DocumentDTO;
import com.devoxx.genie.service.util.bm25.BM25;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReRankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReRankService.class);

    /**
     * Re-rank documents based on the question and used documents.
     * @param usedDocuments the used documents
     * @return the list of re-ranked documents content
     */
    public List<DocumentDTO> reRankDocuments(String query, List<DocumentDTO> usedDocuments) {

        List<String> usedDocumentStrings = usedDocuments.stream()
            .map(DocumentDTO::getText)
            .toList();

        List<DocumentDTO> response = new ArrayList<>();
        List<Map.Entry<Integer, Double>> results;
        BM25 bm25;

        // Check if JSON objects are used as documents
        if (!usedDocuments.isEmpty()) {

            List<String> concatenatedTexts = new ArrayList<>();
            Gson gson = new Gson();

            for (String jsonStr : usedDocumentStrings) {
                if (isValidJson(jsonStr)) {
                    convertJsonToListOfStrings(jsonStr, gson, concatenatedTexts);
                } else {
                    concatenatedTexts.add(jsonStr);
                }
            }

            bm25 = new BM25(concatenatedTexts);
        } else {
            bm25 = new BM25(usedDocumentStrings);
        }

        results = bm25.search(query);
        LOGGER.debug("BM25 search results: {}", results);

        convertToDocumentDTOList(usedDocuments, results, response);

        return response;
    }

    public static boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }

        try {
            Gson gson = new Gson();
            JsonElement element = gson.fromJson(jsonString, JsonElement.class);
            return element.isJsonObject() || element.isJsonArray();
        } catch (JsonSyntaxException ex) {
            // The string is not in valid JSON format
            return false;
        }
    }

    /**
     * Convert the results to a list of DocumentDTO objects with score > 0.
     * @param usedDocuments the used documents
     * @param results the search results
     * @param response the response list
     */
    private static void convertToDocumentDTOList(List<DocumentDTO> usedDocuments,
                                                 List<Map.Entry<Integer, Double>> results,
                                                 List<DocumentDTO> response) {
        for (Map.Entry<Integer, Double> entry : results) {
            if (entry.getValue() > 0.0) {
                response.add(usedDocuments.get(entry.getKey()));
            }
        }
    }

    private static void convertJsonToListOfStrings(String jsonStr, Gson gson, List<String> concatenatedTexts) {
        JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
        StringBuilder concatenatedText = new StringBuilder();
        extractAndConcatTexts(element, concatenatedText);
        concatenatedTexts.add(concatenatedText.toString());
    }

    /**
     * Extract and concatenate texts from a JSON element.
     * @param element the JSON element
     * @param concatenatedText the concatenated text
     */
    private static void extractAndConcatTexts(JsonElement element,
                                              StringBuilder concatenatedText) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            concatenatedText.append(element.getAsString());
            concatenatedText.append(" "); // Add a space between each text for readability
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                extractAndConcatTexts(entry.getValue(), concatenatedText);
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement el : array) {
                extractAndConcatTexts(el, concatenatedText);
            }
        }
    }

}
